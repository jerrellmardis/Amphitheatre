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

package com.jerrellmardis.amphitheatre.util;

import com.jerrellmardis.amphitheatre.BuildConfig;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public final class ApiConstants {

    public static final String GUESS_IT_SERVER_URL = "http://guessit.io";

    public static final String TMDB_SERVER_URL = "https://api.themoviedb.org/3";
    public static final String TMDB_SERVER_API_KEY = BuildConfig.TMDB_API_KEY;

    public static final String TVDB_SERVER_URL = "http://thetvdb.com/api";

    public static final String TVDB_SERVER_API_KEY = BuildConfig.TVDB_API_KEY;
}