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

import com.jerrellmardis.amphitheatre.api.ApiClient;
import com.jerrellmardis.amphitheatre.api.GuessItClient;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.guessit.Guess;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.VideoUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/15/14.
 */
public final class DownloadTaskHelper {

    public static List<SmbFile> getFiles(String user, String password, String path) {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", user, password);

        List<SmbFile> files = Collections.emptyList();
        try {
            files = VideoUtils.getFilesFromDir(path, auth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    public static Video downloadMovieData(Config config, SmbFile file) {
        if (TextUtils.isEmpty(file.getPath()) || file.getName().toLowerCase().contains(Constants.SAMPLE)) {
            return null;
        }

        Guess guess = GuessItClient.guess(file.getName());

        // if a guess is not found, search again using the parent directory's name
        if (guess != null &&
                (TextUtils.isEmpty(guess.getTitle()) || guess.getTitle().equals(file.getName()))) {

            String[] sections = file.getPath().split("/");
            String name = sections[sections.length - 2];

            int indexOf = file.getPath().lastIndexOf(".");
            String ext = file.getPath().substring(indexOf, file.getPath().length());
            guess = GuessItClient.guess(name + ext);
        }

        Video video = new Video();

        try {
            video.setCreated(file.createTime());
        } catch (SmbException e) {
            // do nothing
        }

        if (guess == null || TextUtils.isEmpty(guess.getTitle())) {
            video.setName(WordUtils.capitalizeFully(file.getName()));
            video.setVideoUrl(file.getPath());
            video.setIsMatched(false);
            video.setIsMovie(true);
            video.save();
            return video;
        }

        video.setName(WordUtils.capitalizeFully(guess.getTitle()));
        video.setVideoUrl(file.getPath());
        video.setIsMovie(true);

        if (!TextUtils.isEmpty(guess.getTitle())) {
            try {
                // search for the movie
                SearchResult result = ApiClient
                        .getInstance().createTMDbClient().findMovie(guess.getTitle(),
                                guess.getYear());

                // if found, get the detailed info for the movie
                if (result.getResults() != null && !result.getResults().isEmpty()) {
                    Long id = result.getResults().get(0).getId();

                    if (id != null) {
                        Movie movie;
                        movie = ApiClient.getInstance().createTMDbClient().getMovie(id);
                        if(movie == null) {
                            movie = ApiClient.getInstance().createTVDBClient().getMovie(id);
                        }
                        movie.setTmdbId(id);
                        movie.setId(null);
                        movie.setFlattenedGenres(StringUtils.join(movie.getGenres(), ","));
                        movie.setFlattenedProductionCompanies(
                                StringUtils.join(movie.getProductionCompanies(), ","));
                        movie.save();

                        video.setOverview(movie.getOverview());
                        video.setName(movie.getTitle());
                        video.setIsMatched(true);
                        video.setMovie(movie);
                    }

                    String cardImageUrl = config.getImages().getBase_url() + "original" +
                            result.getResults().get(0).getPoster_path();
                    video.setCardImageUrl(cardImageUrl);

                    String bgImageUrl = config.getImages().getBase_url() + "original" +
                            result.getResults().get(0).getBackdrop_path();
                    video.setBackgroundImageUrl(bgImageUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        video.save();

        return video;
    }

    public static Video downloadTvShowData(Config config, SmbFile file) {
        if (TextUtils.isEmpty(file.getPath()) || file.getName().toLowerCase().contains(Constants.SAMPLE)) {
            return null;
        }

        Guess guess = GuessItClient.guess(file.getName());

        // if a guess is not found, search again using the parent directory's name
        if (guess != null &&
                (TextUtils.isEmpty(guess.getSeries()) || guess.getSeries().equals(file.getName()))) {

            String[] sections = file.getPath().split("/");
            String name = sections[sections.length - 2];

            int indexOf = file.getPath().lastIndexOf(".");
            String ext = file.getPath().substring(indexOf, file.getPath().length());
            guess = GuessItClient.guess(name + ext);
        }

        Video video = new Video();

        // couldn't find a match. Create a TV Show, mark it as unmatched and move on.
        if (guess == null || TextUtils.isEmpty(guess.getSeries())) {
            video.setName(WordUtils.capitalizeFully(file.getName()));
            video.setVideoUrl(file.getPath());
            video.setIsMatched(false);
            video.setIsMovie(false);
            video.save();
            return video;
        }

        video.setName(WordUtils.capitalizeFully(guess.getSeries()));
        video.setVideoUrl(file.getPath());
        video.setIsMovie(false);

        if (!TextUtils.isEmpty(guess.getSeries())) {
            try {
                TvShow tvShow = null;
                Long tmdbId = null;

                // look for the TV show in the database first
                List<TvShow> tvShows = TvShow.find(TvShow.class, "original_name = ?",
                        guess.getSeries());

                // if a TV show is found, clone it.
                // if not, run a TMDb search for the TV show
                if (tvShows != null && !tvShows.isEmpty()) {
                    tvShow = TvShow.copy(tvShows.get(0));
                    tmdbId = tvShow.getTmdbId();
                } else {
                    SearchResult result;
                    result = ApiClient.getInstance().createTMDbClient()
                            .findTvShow(guess.getSeries());
                    if(result == null) {
                        result = ApiClient.getInstance().createTVDBClient()
                                .findTvShow(guess.getSeries());
                    }
                    if (result.getResults() != null && !result.getResults().isEmpty()) {
                        tmdbId = result.getResults().get(0).getId();
                        tvShow = ApiClient.getInstance().createTMDbClient().getTvShow(tmdbId);
                        if(tvShow == null) {
                            tvShow = ApiClient.getInstance().createTVDBClient().getTvShow(tmdbId);
                        }
                        tvShow.setTmdbId(tmdbId);
                        tvShow.setId(null);
                        tvShow.setFlattenedGenres(StringUtils.join(tvShow.getGenres(), ","));
                    }
                }

                if (tmdbId != null) {
                    // get the Episode information
                    if (guess.getEpisodeNumber() != null && guess.getSeason() != null) {
                        Episode episode;
                        episode = ApiClient.getInstance().createTMDbClient()
                                .getEpisode(tvShow.getTmdbId(),
                                        guess.getSeason(), guess.getEpisodeNumber());
                        if (episode == null) {
                            episode = ApiClient.getInstance().createTVDBClient()
                                    .getEpisode(tvShow.getId(),tvShow.getEpisode().getAirDate());
                        }
                        if (episode != null) {
                            if (!TextUtils.isEmpty(episode.getStillPath())) {
                                String stillPathUrl = config.getImages().getBase_url() + "original" +
                                        episode.getStillPath();
                                episode.setStillPath(stillPathUrl);
                            }

                            episode.setTmdbId(tmdbId);
                            episode.setId(null);

                            episode.save();
                            tvShow.setEpisode(episode);
                            video.setIsMatched(true);
                        }
                    }

                    tvShow.save();

                    video.setName(tvShow.getOriginalName());
                    video.setOverview(tvShow.getOverview());
                    video.setTvShow(tvShow);

                    String cardImageUrl = config.getImages().getBase_url() + "original" +
                            tvShow.getPosterPath();
                    video.setCardImageUrl(cardImageUrl);

                    String bgImageUrl = config.getImages().getBase_url() + "original" +
                            tvShow.getBackdropPath();
                    video.setBackgroundImageUrl(bgImageUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        video.save();

        return video;
    }
}
