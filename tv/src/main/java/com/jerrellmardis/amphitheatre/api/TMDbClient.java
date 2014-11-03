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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;
import com.jerrellmardis.amphitheatre.model.tmdb.Videos;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Jerrell Mardis on 7/8/14.
 */
public class TMDbClient {

    private interface TMDbService {
        @GET("/configuration")
        Config getConfig();

        @GET("/movie/{id}")
        Movie getMovie(@Path("id") Long id);

        @GET("/movie/{id}/videos")
        Videos getVideos(@Path("id") Long id);

        @GET("/tv/{id}")
        TvShow getTvShow(@Path("id") Long id);

        @GET("/tv/{id}/season/{season_number}/episode/{episode_number}")
        Episode getEpisode(@Path("id") Long id, @Path("season_number") int seasonNumber,
                           @Path("episode_number") int episodeNumber);

        @GET("/search/movie")
        SearchResult findMovie(@Query("query") CharSequence name, @Query("year") Integer year);

        @GET("/search/tv")
        SearchResult findTvShow(@Query("query") CharSequence name);
    }

    private static TMDbService service;

    private static TMDbService getService() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint(ApiConstants.TMDB_SERVER_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("api_key", ApiConstants.TMDB_SERVER_API_KEY);
                        }
                    })
                    .build();
            service = restAdapter.create(TMDbService.class);
        }
        return service;
    }

    public static Config getConfig() {
        return getService().getConfig();
    }

    public static Movie getMovie(Long id) {
        Movie movie = getService().getMovie(id);
        addBestTrailer(movie);
        return movie;
    }

    public static TvShow getTvShow(Long id) {
        return getService().getTvShow(id);
    }

    public static SearchResult findMovie(CharSequence name, Integer year) {
        return getService().findMovie(name, year);
    }

    public static SearchResult findTvShow(CharSequence name) {
        return getService().findTvShow(name);
    }

    public static Episode getEpisode(Long id, int seasonNumber, int episodeNumber) {
        return getService().getEpisode(id, seasonNumber, episodeNumber);
    }

    /**
     * Adds the best trailer to the movie record.
     *
     * Currently, 'best' is defined as the first trailer from YouTube.
     *
     * @param movie The movie for which to fetch a trailer.
     * @return The same movie with the trailer property set.
     */
    public static Movie addBestTrailer(Movie movie) {
        Videos vids = getService().getVideos(movie.getId());
        if (vids == null || vids.getResults() == null || vids.getResults().isEmpty()) {
            return movie;
        }

        for (Videos.Video vid : vids.getResults()) {
            if (vid.getType().equals("Trailer") && vid.getSite().equals("YouTube")) {
                movie.setTrailer(String.format("http://youtube.com/watch?v=%s", vid.getKey()));
                break;
            }
        }

        return movie;
    }
}