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
import com.jerrellmardis.amphitheatre.model.guessit.Guess;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Jerrell Mardis on 7/8/14.
 */
public class GuessItClient {

    private interface GuessItService {
        @GET("/guess")
        Guess guess(@Query("filename") CharSequence filename);
    }

    private static GuessItService service;

    private static GuessItService getService() {
        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(new Gson()))
                    .setEndpoint(ApiConstants.GUESS_IT_SERVER_URL)
                    .build();
            service = restAdapter.create(GuessItService.class);
        }
        return service;
    }

    public static Guess guess(CharSequence filename) {
        try {
            Guess guess = getService().guess(filename);
            return guess;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}