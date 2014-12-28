
package com.jerrellmardis.amphitheatre.api;

import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;
import com.jerrellmardis.amphitheatre.model.tmdb.Movie;
import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.TvShow;

/**
 * Media data source.
 */
public interface MediaClient {

    public Config getConfig();

    public Movie getMovie(Long id);

    public TvShow getTvShow(Long id);

    public SearchResult findMovie(CharSequence name, Integer year);

    public SearchResult findTvShow(CharSequence name);

    public Episode getEpisode(Long id, int seasonNumber, int episodeNumber);

    public Episode getEpisode(Long id, String airDate);

    /**
     * Adds the best trailer to the movie record.
     *
     * Currently, 'best' is defined as the first trailer from YouTube.
     *
     * @param movie The movie for which to fetch a trailer.
     * @return The same movie with the trailer property set.
     */
    public Movie addBestTrailer(Movie movie);
}
