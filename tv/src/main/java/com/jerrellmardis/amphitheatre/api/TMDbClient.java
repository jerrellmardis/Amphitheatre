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
import com.jerrellmardis.amphitheatre.model.tmdb.Metadata;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;

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

        @GET("/search/movie")
        Metadata getMetadata(@Query("query") CharSequence name, @Query("year") CharSequence year);

        @GET("/movie/{id}")
        Movie getMovie(@Path("id") Long id);
    }

    private static TMDbService service;

    private static TMDbService getService() {
        if (service == null) {
            service = Clients.getRestAdapter().create(TMDbService.class);
        }
        return service;
    }

    public static Config getConfig() {
        return getService().getConfig();
    }

    public static Metadata getMetadata(CharSequence name, CharSequence year) {
        return getService().getMetadata(name, year);
    }

    public static Movie getMovie(Long id) {
        return getService().getMovie(id);
    }

}
