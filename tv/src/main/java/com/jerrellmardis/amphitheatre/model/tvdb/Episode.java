/*
 * Copyright (C) 2014 Jerrell Mardis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
