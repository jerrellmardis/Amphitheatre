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

import com.jerrellmardis.amphitheatre.model.Movie;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Jerrell Mardis on 7/8/14.
 */
public class GuessItClient {

    private interface GuessItService {
        @GET("/guess")
        Movie guess(@Query("filename") CharSequence filename);
    }

    private static GuessItService service;

    private static GuessItService getService() {
        if (service == null) {
            service = Clients.getRestAdapter().create(GuessItService.class);
        }
        return service;
    }

    public static Movie guess(CharSequence filename) {
        try {
            Movie movie = getService().guess(filename);
            return movie;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
