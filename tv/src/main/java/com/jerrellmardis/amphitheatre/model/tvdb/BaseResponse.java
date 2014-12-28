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

@Root(strict = false)
public abstract class BaseResponse {

    @Element(name = "id", required = false)
    private Long mId;

    @Element(name = "IMDB_ID", required = false)
    private String mImdbId;

    @Element(name = "language", required = false)
    private String mLanguage;

    @Element(name = "Overview", required = false)
    private String mDescription;

    @Element(name = "Rating", required = false)
    private float mRating;

    @Element(name = "RatingCount", required = false)
    private int mRatingCount;

    @Element(name = "lastupdated", required = false)
    private long mLastUpdated;

    @Element(name = "FirstAired", required = false)
    private String mFirstAiringDate;

    public Long getId() {
        return mId;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public String getRawLanguage() {
        return mLanguage;
    }

    public Language getLanguage() {
        return Language.parse(mLanguage);
    }

    public String getDescription() {
        return mDescription;
    }

    public float getRating() {
        return mRating;
    }

    public int getRatingCount() {
        return mRatingCount;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public String getFirstAiringDate() {
        return mFirstAiringDate;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "mId=" + mId +
                ", mImdbId='" + mImdbId + '\'' +
                ", mLanguage='" + mLanguage + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mRating=" + mRating +
                ", mRatingCount=" + mRatingCount +
                ", mLastUpdated=" + mLastUpdated +
                ", mFirstAiringDate='" + mFirstAiringDate + '\'' +
                '}';
    }
}
