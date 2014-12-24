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
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * HTTP client for the TVDB API
 */
public class TVDBClient implements MediaClient{

    private interface TVDBService {
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

    private static TVDBService service;

    private static TVDBService getService() {

        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new SimpleXMLConverter() )
                    .setEndpoint(ApiConstants.TMDB_SERVER_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("api_key", ApiConstants.TVDB_SERVER_API_KEY);
                        }
                    })
                    .build();
            service = restAdapter.create(TVDBService.class);
        }
        return service;
    }

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public Movie getMovie(Long id) {
        return null;
    }

    @Override
    public TvShow getTvShow(Long id) {
        return null;
    }

    @Override
    public SearchResult findMovie(CharSequence name, Integer year) {
        return null;
    }

    @Override
    public SearchResult findTvShow(CharSequence name) {
        return null;
    }

    @Override
    public Episode getEpisode(Long id, int seasonNumber, int episodeNumber) {
        return null;
    }

    @Override
    public Movie addBestTrailer(Movie movie) {
        return null;
    }
}
