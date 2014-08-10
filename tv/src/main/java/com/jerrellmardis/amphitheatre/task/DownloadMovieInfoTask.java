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
import com.jerrellmardis.amphitheatre.listeners.TaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.guessit.Guess;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.util.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.List;

import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public class DownloadMovieInfoTask extends AsyncTask<Void, Void, Boolean> {

    private Config mConfig;
    private String mDirectory;
    private List<SmbFile> mFiles;
    private TaskListener mTaskListener;

    public DownloadMovieInfoTask(Config config, String directory, List<SmbFile> files, TaskListener l) {
        mDirectory = directory;
        mFiles = files;
        mTaskListener = l;
        mConfig = config;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        for (SmbFile file : mFiles) {
            if (TextUtils.isEmpty(file.getPath()) || file.getName().toLowerCase().contains(Constants.SAMPLE)) {
                continue;
            }

            Guess guess = GuessItClient.guess(file.getName());

            // if a guess is not found, search again using the parent directory's name
            if (guess != null &&
                    (TextUtils.isEmpty(guess.getTitle()) || guess.getTitle().equals(file.getName()))) {

                String[] sections = file.getPath().split("/");
                String name = sections[sections.length - 2];

                if (!name.equals(mDirectory)) {
                    int indexOf = file.getPath().lastIndexOf(".");
                    String ext = file.getPath().substring(indexOf, file.getPath().length());
                    guess = GuessItClient.guess(name + ext);
                }
            }

            Video video = new Video();

            if (guess == null || TextUtils.isEmpty(guess.getTitle())) {
                video.setName(WordUtils.capitalizeFully(file.getName()));
                video.setVideoUrl(file.getPath());
                video.setIsMatched(false);
                video.setIsMovie(true);
                video.save();
                continue;
            }

            video.setName(WordUtils.capitalizeFully(guess.getTitle()));
            video.setVideoUrl(file.getPath());
            video.setIsMovie(true);

            if (!TextUtils.isEmpty(guess.getTitle())) {
                try {
                    // search for the movie
                    SearchResult result = TMDbClient.findMovie(guess.getTitle(), guess.getYear());

                    // if found, get the detailed info for the movie
                    if (result.getResults() != null && !result.getResults().isEmpty()) {
                        Long id = result.getResults().get(0).getId();

                        if (id != null) {
                            Movie movie = TMDbClient.getMovie(id);
                            movie.setTmdbId(id);
                            movie.setId(null);
                            movie.setFlattenedGenres(StringUtils.join(movie.getGenres(), ","));
                            movie.setFlattenedProductionCompanies(StringUtils.join(movie.getProductionCompanies(), ","));
                            movie.save();

                            video.setOverview(movie.getOverview());
                            video.setName(movie.getTitle());
                            video.setIsMatched(true);
                            video.setMovie(movie);
                        }

                        String cardImageUrl = mConfig.getImages().getBase_url() + "original" +
                                result.getResults().get(0).getPoster_path();
                        video.setCardImageUrl(cardImageUrl);

                        String bgImageUrl = mConfig.getImages().getBase_url() + "original" +
                                result.getResults().get(0).getBackdrop_path();
                        video.setBackgroundImageUrl(bgImageUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            video.save();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean processed) {
        if (mTaskListener != null) {
            mTaskListener.taskCompleted();
        }
    }
}