package com.jerrellmardis.amphitheatre.model.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Episode", strict = false)
public class Episode extends BaseResponse {

    @Element(name = "EpisodeName", required = false)
    private String mEpisodeName;

    @Element(name = "EpisodeNumber", required = false)
    private Long mEpisodeNumber;

    @Element(name = "SeasonNumber", required = false)
    private int mSeasonNumber;

    @Element(name = "filename", required = false)
    private String mImageUrl;

    @Element(name = "seasonid", required = false)
    private Long mSeasonId;

    @Element(name = "seriesid", required = false)
    private Long mSeriesId;

    public String getEpisodeName() {
        return mEpisodeName;
    }

    public Long getEpisodeNumber() {
        return mEpisodeNumber;
    }

    public int getSeasonNumber() {
        return mSeasonNumber;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public Long getSeasonId() {
        return mSeasonId;
    }

    public Long getSeriesId() {
        return mSeriesId;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "mEpisodeName='" + mEpisodeName + '\'' +
                ", mEpisodeNumber=" + mEpisodeNumber +
                ", mSeasonNumber=" + mSeasonNumber +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mSeasonId=" + mSeasonId +
                ", mSeriesId=" + mSeriesId +
                '}';
    }
}
