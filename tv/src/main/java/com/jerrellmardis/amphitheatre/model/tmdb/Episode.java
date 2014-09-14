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

package com.jerrellmardis.amphitheatre.model.tmdb;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Episode extends SugarRecord<Episode> implements Serializable {

    private String airDate;
    private Long episodeNumber;
    private String name;
    private String overview;
    private String stillPath;
    private Long voteAverage;
    private Long voteCount;
    private Long tmdbId;
    private int seasonNumber;
    @Ignore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public Long getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Long episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public Long getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Long voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}