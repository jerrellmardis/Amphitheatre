package com.jerrellmardis.amphitheatre.model.tvdb;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Series", strict = false)
public class Series extends BaseResponse {
    @Element(name = "seriesid", required = false)
    private int mSeriesId;

    @Element(name = "SeriesName", required = false)
    private String mSeriesName;

    @Element(name = "banner", required = false)
    private String mImageUrl;

    @Element(name = "posters", required = false)
    private String mPosters;

    public int getSeriesId() {
        return mSeriesId;
    }

    public String getSeriesName() {
        return mSeriesName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getPosters() {
        return mPosters;
    }

    @Override
    public String toString() {
        return "Series{" +
                "mSeriesId=" + mSeriesId +
                ", mSeriesName='" + mSeriesName + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mPosters='" + mPosters + '\'' +
                '}';
    }
}
