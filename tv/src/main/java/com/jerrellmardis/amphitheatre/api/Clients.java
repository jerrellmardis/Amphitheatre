package com.jerrellmardis.amphitheatre.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.jerrellmardis.amphitheatre.BuildConfig;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import static retrofit.RestAdapter.LogLevel.FULL;
import static retrofit.RestAdapter.LogLevel.NONE;

final class Clients {
    private static RestAdapter restAdapter;

    static RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            Gson gson = new GsonBuilder().create();

            restAdapter = new RestAdapter.Builder()
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
        }
        return restAdapter;
    }

    private Clients() {
        throw new AssertionError("No instances.");
    }
}
