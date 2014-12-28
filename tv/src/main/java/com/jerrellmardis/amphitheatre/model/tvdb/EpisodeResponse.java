package com.jerrellmardis.amphitheatre.model.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Data", strict = false)
public class EpisodeResponse {

    @Element(name = "Episode", required = false)
    private Episode mEpisode;

    public Episode getEpisode() {
        return mEpisode;
    }

    @Override
    public String toString() {
        return "EpisodeResponse{" +
                "mEpisode=" + mEpisode +
                '}';
    }
}
