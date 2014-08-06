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
import com.jerrellmardis.amphitheatre.model.Movie;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;

import static retrofit.RestAdapter.LogLevel.FULL;
import static retrofit.RestAdapter.LogLevel.NONE;

/**
 * Created by Jerrell Mardis on 7/8/14.
 */
public class GuessItClient {

    private interface GuessItService {
        @GET("/guess")
        Movie guess(@Query("filename") CharSequence filename);
    }

    public static Movie guess(CharSequence filename) {
        try {
            Movie movie = getHttpService().guess(filename);
            return movie;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static GuessItService getHttpService() {
        Gson gson = new GsonBuilder().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(ApiConstants.GUESS_IT_SERVER_URL)
                .build();

        restAdapter.setLogLevel(BuildConfig.DEBUG ? FULL : NONE);

        return restAdapter.create(GuessItService.class);
    }
}