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

import android.os.AsyncTask;
import android.text.TextUtils;

import com.jerrellmardis.amphitheatre.api.GuessItClient;
import com.jerrellmardis.amphitheatre.api.TMDbClient;
import com.jerrellmardis.amphitheatre.listeners.TaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.guessit.Guess;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;
import com.jerrellmardis.amphitheatre.util.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public class DownloadTvShowInfoTask extends AsyncTask<Void, Void, Boolean> {

    private String mDirectory;
    private List<SmbFile> mFiles;
    private TaskListener mTaskListener;

    public DownloadTvShowInfoTask(String directory, List<SmbFile> files, TaskListener l) {
        mDirectory = directory;
        mFiles = files;
        mTaskListener = l;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Config config = TMDbClient.getConfig();

        for (SmbFile file : mFiles) {
            if (TextUtils.isEmpty(file.getPath()) || file.getName().toLowerCase().contains(Constants.SAMPLE)) {
                continue;
            }

            Guess guess = GuessItClient.guess(file.getName());

            // if a guess is not found, search again using the parent directory's name
            if (guess != null &&
                    (TextUtils.isEmpty(guess.getSeries()) || guess.getSeries().equals(file.getName()))) {

                String[] sections = file.getPath().split("/");
                String name = sections[sections.length - 2];

                if (!name.equals(mDirectory)) {
                    int indexOf = file.getPath().lastIndexOf(".");
                    String ext = file.getPath().substring(indexOf, file.getPath().length());
                    guess = GuessItClient.guess(name + ext);
                }
            }

            Video video = new Video();

            // couldn't find a match. Create a TV Show, mark it as unmatched and move on.
            if (guess == null || TextUtils.isEmpty(guess.getSeries())) {
                video.setName(WordUtils.capitalizeFully(file.getName()));
                video.setVideoUrl(file.getPath());
                video.setIsMatched(false);
                video.setIsMovie(false);
                video.save();
                continue;
            }

            video.setName(WordUtils.capitalizeFully(guess.getSeries()));
            video.setVideoUrl(file.getPath());
            video.setIsMovie(false);

            if (!TextUtils.isEmpty(guess.getSeries())) {
                try {
                    // search for the TV show
                    SearchResult searchResult = TMDbClient.findTvShow(guess.getSeries());

                    // if found, get the detailed info for the show
                    if (searchResult.getResults() != null && !searchResult.getResults().isEmpty()) {
                        Long id = searchResult.getResults().get(0).getId();

                        if (id != null) {
                            TvShow tvShow = TMDbClient.getTvShow(id);
                            tvShow.setTmdbId(id);
                            tvShow.setId(null);

                            if (guess.getEpisodeNumber() != null && guess.getSeason() != null) {
                                Episode episode = TMDbClient.getEpisode(tvShow.getTmdbId(),
                                        guess.getSeason(), guess.getEpisodeNumber());

                                if (episode != null) {
                                    String stillPathUrl = config.getImages().getBase_url() + "original" +
                                            episode.getStillPath();
                                    episode.setStillPath(stillPathUrl);

                                    episode.setTmdbId(id);
                                    episode.setId(null);

                                    episode.save();
                                    tvShow.setEpisode(episode);
                                }
                            }

                            tvShow.setFlattenedGenres(StringUtils.join(tvShow.getGenres(), ","));
                            tvShow.save();

                            video.setName(tvShow.getOriginalName());
                            video.setOverview(tvShow.getOverview());
                            video.setIsMatched(true);
                            video.setTvShow(tvShow);
                        }

                        String cardImageUrl = config.getImages().getBase_url() + "original" +
                                searchResult.getResults().get(0).getPoster_path();
                        video.setCardImageUrl(cardImageUrl);

                        String bgImageUrl = config.getImages().getBase_url() + "original" +
                                searchResult.getResults().get(0).getBackdrop_path();
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