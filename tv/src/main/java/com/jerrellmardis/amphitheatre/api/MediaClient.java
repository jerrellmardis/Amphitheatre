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

package com.jerrellmardis.amphitheatre.api;

import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;

/**
 * Media data source.
 */
public interface MediaClient {

    public Config getConfig();

    public Movie getMovie(Long id);

    public TvShow getTvShow(Long id);

    public SearchResult findMovie(CharSequence name, Integer year);

    public SearchResult findTvShow(CharSequence name);

    public Episode getEpisode(Long id, int seasonNumber, int episodeNumber);

    public Episode getEpisode(Long id, String airDate);

    /**
     * Adds the best trailer to the movie record.
     *
     * Currently, 'best' is defined as the first trailer from YouTube.
     *
     * @param movie The movie for which to fetch a trailer.
     * @return The same movie with the trailer property set.
     */
    public Movie addBestTrailer(Movie movie);
}
