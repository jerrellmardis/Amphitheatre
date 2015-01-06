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
