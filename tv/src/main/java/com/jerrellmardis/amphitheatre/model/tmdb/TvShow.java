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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvShow extends SugarRecord<TvShow> implements Serializable {

    private Long tmdbId;
    private String backdropPath;
    private String firstAirDate;
    private String flattenedGenres;
    private String homepage;
    private Boolean inProduction;
    private String lastAirDate;
    private Long numberOfEpisodes;
    private int numberOfSeasons;
    private String originalName;
    private String overview;
    private Double popularity;
    private String posterPath;
    private String status;
    private Double voteAverage;
    private Long voteCount;
    private Episode episode;

    @Ignore private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @Ignore private List<Genre> genres = new ArrayList<Genre>();

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public Boolean getInProduction() {
        return inProduction;
    }

    public void setInProduction(Boolean inProduction) {
        this.inProduction = inProduction;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public Long getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Long numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public String getFlattenedGenres() {
        return flattenedGenres;
    }

    public void setFlattenedGenres(String flattenedGenres) {
        this.flattenedGenres = flattenedGenres;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static TvShow copy(TvShow tvShow) {
        TvShow clone = new TvShow();
        clone.tmdbId = tvShow.getTmdbId();
        clone.backdropPath = tvShow.getBackdropPath();
        clone.firstAirDate = tvShow.getFirstAirDate();
        clone.flattenedGenres = tvShow.getFlattenedGenres();
        clone.homepage = tvShow.getHomepage();
        clone.inProduction = tvShow.getInProduction();
        clone.lastAirDate = tvShow.getLastAirDate();
        clone.numberOfEpisodes = tvShow.getNumberOfEpisodes();
        clone.numberOfSeasons = tvShow.getNumberOfSeasons();
        clone.originalName = tvShow.getOriginalName();
        clone.overview = tvShow.getOverview();
        clone.popularity = tvShow.getPopularity();
        clone.posterPath = tvShow.getPosterPath();
        clone.status = tvShow.getStatus();
        clone.voteAverage = tvShow.getVoteAverage();
        clone.voteCount = tvShow.getVoteCount();
        return clone;
    }
}