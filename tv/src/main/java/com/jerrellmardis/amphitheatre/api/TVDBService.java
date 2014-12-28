package com.jerrellmardis.amphitheatre.api;

import com.jerrellmardis.amphitheatre.model.tvdb.Episode;
import com.jerrellmardis.amphitheatre.model.tvdb.EpisodeResponse;
import com.jerrellmardis.amphitheatre.model.tvdb.Language;
import com.jerrellmardis.amphitheatre.model.tvdb.SeriesResult;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TVDBService {

    @GET("/GetEpisodeByAirDate.php")
    EpisodeResponse getEpisode(@Query("apikey") String apiKey, @Query("seriesid") Long seriesId, @Query("airdate") String airDate);

    // Search for series
    @GET("/GetSeries.php")
    SeriesResult findTvShow(@Query("seriesname") String seriesName,
            @Query("language") Language language);
}
