package com.jerrellmardis.amphitheatre.api;

/**
 * Create the media client type to use.
 */
public class MediaClientFactory {

    public MediaClient createTMDbClient() {
        return new TMDbClient();
    }

    public MediaClient createTVDBClient() {
        return new TVDBClient();
    }
}
