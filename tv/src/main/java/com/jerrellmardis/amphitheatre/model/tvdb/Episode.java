package com.jerrellmardis.amphitheatre.model.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Episode", strict = false)
public class Episode extends BaseResponse {

    @Element(name = "EpisodeName", required = false)
    private String mName;

    @Element(name = "SeasonNumber", required = false)
    private int mSeasonNumber;

    @Element(name = "filename", required = false)
    private String mImageUrl;

    public String getName() {
        return mName;
    }

    public int getSeasonNumber() {
        return mSeasonNumber;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
