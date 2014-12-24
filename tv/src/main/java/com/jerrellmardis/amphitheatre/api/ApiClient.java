package com.jerrellmardis.amphitheatre.api;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ApiClient {

    private static MediaClientFactory instance = null;

    private ApiClient() {

    }

    public static synchronized MediaClientFactory getInstance() {
        if (instance == null) {
            instance = new MediaClientFactory();
        }
        return instance;
    }
}
