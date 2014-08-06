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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jerrellmardis.amphitheatre.BuildConfig;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Metadata;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import static retrofit.RestAdapter.LogLevel.FULL;
import static retrofit.RestAdapter.LogLevel.NONE;

/**
 * Created by Jerrell Mardis on 7/8/14.
 */
public class TMDbClient {

    private interface TMDbService {
        @GET("/configuration")
        Config getConfig();

        @GET("/search/movie")
        Metadata getMetadata(@Query("query") CharSequence name, @Query("year") CharSequence year);

        @GET("/movie/{id}")
        Movie getMovie(@Path("id") Long id);
    }

    public static Config getConfig() {
        return getHttpService().getConfig();
    }

    public static Metadata getMetadata(CharSequence name, CharSequence year) {
        return getHttpService().getMetadata(name, year);
    }

    public static Movie getMovie(Long id) {
        return getHttpService().getMovie(id);
    }

    private static TMDbService getHttpService() {
        Gson gson = new GsonBuilder().create();

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

        restAdapter.setLogLevel(BuildConfig.DEBUG ? FULL : NONE);

        return restAdapter.create(TMDbService.class);
    }
}