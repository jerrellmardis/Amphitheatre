/*
 * Copyright (C) 2014 Jerrell Mardis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jerrellmardis.amphitheatre.task;

import com.jerrellmardis.amphitheatre.api.GuessItClient;
import com.jerrellmardis.amphitheatre.api.TMDbClient;
import com.jerrellmardis.amphitheatre.model.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Metadata;

import org.apache.commons.lang3.text.WordUtils;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public class DownloadMovieInfoTask extends AsyncTask<Void, Void, Boolean> {

    private static final String SAMPLE = "sample";

    private String mDirectory;
    private List<Movie> mMovies;
    private List<SmbFile> mFiles;
    private OnTaskCompletedListener mOnTaskCompletedListener;

    public interface OnTaskCompletedListener {
        void onTaskCompleted();
    }

    public DownloadMovieInfoTask(String directory, List<SmbFile> files, OnTaskCompletedListener l) {
        mDirectory = directory;
        mFiles = files;
        mOnTaskCompletedListener = l;
        mMovies = new ArrayList<Movie>();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Config config = TMDbClient.getConfig();

        for (SmbFile file : mFiles) {
            if (TextUtils.isEmpty(file.getPath()) || file.getName().toLowerCase().contains(SAMPLE)) {
                continue;
            }

            Movie movie = GuessItClient.guess(file.getName());

            // try to determine the movie's name based on its parent folder name if and only if
            // 1. The movie was not matched
            // or
            // 2. The movie title that was matched is equal to the filename (This is a special
            // case. Since my files are not named nicely, if the movie title that came back is equal
            // to the filename then that means that GuessIt didn't find a match)
            if (movie != null &&
                    (TextUtils.isEmpty(movie.getTitle()) || movie.getTitle().equals(file.getName()))) {

                String[] sections = file.getPath().split("/");
                String name = sections[sections.length - 2];

                if (!name.equals(mDirectory)) {
                    int indexOf = file.getPath().lastIndexOf(".");
                    String ext = file.getPath().substring(indexOf, file.getPath().length());
                    movie = GuessItClient.guess(name + ext);
                }
            }

            if (movie == null || TextUtils.isEmpty(movie.getTitle())) {
                if (movie == null) {
                    movie = new Movie();
                }

                movie.setTitle(WordUtils.capitalizeFully(file.getName()));
                movie.setVideoUrl(file.getPath());
                movie.setMatched(false);

                mMovies.add(movie);

                continue;
            }

            movie.setTitle(WordUtils.capitalizeFully(movie.getTitle()));

            if (!TextUtils.isEmpty(movie.getTitle())) {
                try {
                    Metadata metadata = TMDbClient.getMetadata(movie.getTitle(), movie.getYear());

                    if (metadata.getResults() != null && !metadata.getResults().isEmpty()) {
                        String cardImageUrl = config.getImages().getBase_url() + "original" +
                                metadata.getResults().get(0).getPoster_path();
                        movie.setCardImageUrl(cardImageUrl);

                        String bgImageUrl = config.getImages().getBase_url() + "original" +
                                metadata.getResults().get(0).getBackdrop_path();
                        movie.setBackgroundImageUrl(bgImageUrl);

                        movie.settMDbId(metadata.getResults().get(0).getId());
                    }

                    if (movie.gettMDbId() != null) {
                        com.jerrellmardis.amphitheatre.model.tmdb.Movie tmDbMovie = TMDbClient.getMovie(movie.gettMDbId());
                        movie.setDescription(tmDbMovie.getOverview());
                        movie.setYear(tmDbMovie.getRelease_date());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            movie.setVideoUrl(file.getPath());
            movie.setMatched(true);

            mMovies.add(movie);
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean processed) {
        Movie.saveInTx(mMovies);

        if (mOnTaskCompletedListener != null) {
            mOnTaskCompletedListener.onTaskCompleted();
        }
    }
}