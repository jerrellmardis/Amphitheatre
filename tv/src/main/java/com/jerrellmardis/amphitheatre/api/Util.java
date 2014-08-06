package com.jerrellmardis.amphitheatre.api;

import com.google.gson.Gson;

import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

final class Util {
    static final Converter JSON_CONVERTER = new GsonConverter(new Gson());

    private Util() {
        throw new AssertionError("No instances.");
    }
}
