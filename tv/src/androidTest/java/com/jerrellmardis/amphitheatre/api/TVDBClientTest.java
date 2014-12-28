package com.jerrellmardis.amphitheatre.api;

import com.jerrellmardis.amphitheatre.model.tmdb.SearchResult;
import com.jerrellmardis.amphitheatre.model.tmdb.Episode;

import android.test.AndroidTestCase;

/**
 * Tests TVDB client
 */
public class TVDBClientTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();
    }


    public void testShouldSearchForHeroesSeries() {
        SearchResult searchResult = ApiClient.getInstance().createTVDBClient().findTvShow("Heroes");
        assertNotNull(searchResult);
        assertEquals(searchResult.getTotal_results(), Integer.valueOf(23));
        assertNotNull(searchResult.getResults());
        assertEquals(searchResult.getResults().get(0).getName(), "Heroes");
    }

    public void testShouldFetchHeroesEpisode() {
        Long seriesid = 79501l;
        Long id = 308906l;
        Episode episode= ApiClient.getInstance().createTVDBClient().getEpisode(seriesid,"2006-09-25");
        assertNotNull(episode);
        assertEquals(episode.getId(),id);
        assertEquals(episode.getAirDate(), "2006-09-25");
        assertEquals(episode.getName(), "Genesis");
        assertEquals(episode.getTmdbId(), seriesid);
    }
}
