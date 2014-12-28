package com.jerrellmardis.amphitheatre.api;

/**
 * For access the different media clients available
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
