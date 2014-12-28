package com.jerrellmardis.amphitheatre.api;

import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;
import com.jerrellmardis.amphitheatre.model.tvdb.EpisodeResponse;
import com.jerrellmardis.amphitheatre.model.tvdb.Language;
import com.jerrellmardis.amphitheatre.model.tvdb.Series;
import com.jerrellmardis.amphitheatre.model.tvdb.SeriesResult;
import com.jerrellmardis.amphitheatre.util.ApiConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;

/**
 * HTTP client for the TVDB API
 */
public class TVDBClient implements MediaClient {

    private static TVDBService service;

    private static TVDBService getService() {

        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new SimpleXMLConverter())
                    .setEndpoint(ApiConstants.TVDB_SERVER_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
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
    public Episode getEpisode(Long id, int seasonNumber, int episodeNumber) {
        return this.getEpisode(id, 0, 0);
    }

    @Override
    public Episode getEpisode(Long id, String airDate) {

        EpisodeResponse episodeResponse = getService().getEpisode(
                ApiConstants.TVDB_SERVER_API_KEY, id, airDate);
        System.out.println("Episodes "+episodeResponse.toString());
        if(episodeResponse !=null) {
            com.jerrellmardis.amphitheatre.model.tvdb.Episode tvdbEpisode = episodeResponse
                  .getEpisode();
            System.out.println("Episodess: "+tvdbEpisode.toString());
            if (tvdbEpisode != null) {
                Episode episode = new Episode();
                episode.setName(tvdbEpisode.getEpisodeName());
                episode.setAirDate(tvdbEpisode.getFirstAiringDate());
                episode.setOverview(tvdbEpisode.getDescription());
                episode.setSeasonNumber(tvdbEpisode.getSeasonNumber());
                episode.setStillPath(tvdbEpisode.getImageUrl());
                episode.setEpisodeNumber(tvdbEpisode.getEpisodeNumber());
                //Use Series id as TMDbId
                episode.setTmdbId(tvdbEpisode.getSeriesId());
                episode.setId(tvdbEpisode.getId());
                return episode;
            }
        }

        return null;
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
            result.setId(series.getId());
            result.setPoster_path(series.getPosters());
            results.add(result);
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setTotal_results(seriesResult.getSeries().size());
        searchResult.setResults(results);
        return searchResult;
    }
}
