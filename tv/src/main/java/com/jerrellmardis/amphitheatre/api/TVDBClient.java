package com.jerrellmardis.amphitheatre.api;

import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;
import com.jerrellmardis.amphitheatre.model.tmdb.Videos;
import com.jerrellmardis.amphitheatre.model.tvdb.Language;
import com.jerrellmardis.amphitheatre.model.tvdb.Series;
import com.jerrellmardis.amphitheatre.model.tvdb.SeriesResult;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * HTTP client for the TVDB API
 */
public class TVDBClient implements MediaClient{

    private interface TVDBService {
        
        @GET("/{apikey}/series/{seriesid}/{language}")
        com.jerrellmardis.amphitheatre.model.tvdb.Episode getEpisode(@Query("apikey") String apiKey,
                @Query("seriesid") long seriesId, @Query("language") Language language);

        @GET("/search/movie")
        SearchResult findMovie(@Query("query") CharSequence name, @Query("year") Integer year);

        // Search for series
        @GET("/GetSeries.php")
        SeriesResult findTvShow(@Query("seriesname") String seriesName,
                @Query("language") Language language);
    }

    private static TVDBService service;

    private static TVDBService getService() {

        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new SimpleXMLConverter())
                    .setEndpoint(ApiConstants.TVDB_SERVER_URL)
                    .build();
            service = restAdapter.create(TVDBService.class);
        }
        return service;
    }

    @Override
    public Config getConfig() {
        //Do nothing
        return null;
    }

    @Override
    public Movie getMovie(Long id) {
        // Do nothing
        return null;
    }

    @Override
    public TvShow getTvShow(Long id) {
        // Do nothing
        return null;
    }

    @Override
    public SearchResult findMovie(CharSequence name, Integer year) {
        // Do nothing
        return null;
    }

    @Override
    public SearchResult findTvShow(CharSequence name) {
        // Fetch series details from tvdb
        SeriesResult seriesResult = getService().findTvShow(name.toString(), Language.ENGLISH);
        return map(seriesResult);
    }

    @Override
    public Episode getEpisode(Long id) {
        return this.getEpisode(id, 0, 0);
    }

    @Override
    public Episode getEpisode(Long id, int seasonNumber, int episodeNumber) {
        com.jerrellmardis.amphitheatre.model.tvdb.Episode tvdbEpisode = getService().getEpisode(
                ApiConstants.TVDB_SERVER_API_KEY, id, Language.ALL);
        Episode episode = new Episode();
        episode.setName(tvdbEpisode.getName());
        episode.setAirDate(tvdbEpisode.getFirstAiringDate());
        episode.setOverview(tvdbEpisode.getDescription());
        episode.setSeasonNumber(tvdbEpisode.getSeasonNumber());
        episode.setStillPath(tvdbEpisode.getImageUrl());
        //Use Series id as TMDbId
        episode.setTmdbId(tvdbEpisode.getId());
        episode.setId(tvdbEpisode.getId());

        return episode;
    }

    @Override
    public Movie addBestTrailer(Movie movie) {
        //Do nothing
        return null;
    }

    private SearchResult map(SeriesResult seriesResult) {
        // Map series search results unto tmdb movie result details
        List<SearchResult.Result> results = new ArrayList<>();
        for (Series series : seriesResult.getSeries()) {
            SearchResult.Result result = new SearchResult.Result();
            result.setName(series.getSeriesName());
            result.setId(Long.valueOf(series.getId()));
            result.setPoster_path(series.getPosters());
            results.add(result);
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setTotal_results(seriesResult.getSeries().size());
        searchResult.setResults(results);
        return searchResult;
    }
}
