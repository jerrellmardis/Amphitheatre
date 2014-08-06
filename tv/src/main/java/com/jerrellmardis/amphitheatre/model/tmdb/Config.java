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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private Images images;
    private List<String> change_keys = new ArrayList<String>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<String> getChange_keys() {
        return change_keys;
    }

    public void setChange_keys(List<String> change_keys) {
        this.change_keys = change_keys;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static class Images {

        private String base_url;
        private String secure_base_url;
        private List<String> backdrop_sizes = new ArrayList<String>();
        private List<String> logo_sizes = new ArrayList<String>();
        private List<String> poster_sizes = new ArrayList<String>();
        private List<String> profile_sizes = new ArrayList<String>();
        private List<String> still_sizes = new ArrayList<String>();
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public String getBase_url() {
            return base_url;
        }

        public void setBase_url(String base_url) {
            this.base_url = base_url;
        }

        public String getSecure_base_url() {
            return secure_base_url;
        }

        public void setSecure_base_url(String secure_base_url) {
            this.secure_base_url = secure_base_url;
        }

        public List<String> getBackdrop_sizes() {
            return backdrop_sizes;
        }

        public void setBackdrop_sizes(List<String> backdrop_sizes) {
            this.backdrop_sizes = backdrop_sizes;
        }

        public List<String> getLogo_sizes() {
            return logo_sizes;
        }

        public void setLogo_sizes(List<String> logo_sizes) {
            this.logo_sizes = logo_sizes;
        }

        public List<String> getPoster_sizes() {
            return poster_sizes;
        }

        public void setPoster_sizes(List<String> poster_sizes) {
            this.poster_sizes = poster_sizes;
        }

        public List<String> getProfile_sizes() {
            return profile_sizes;
        }

        public void setProfile_sizes(List<String> profile_sizes) {
            this.profile_sizes = profile_sizes;
        }

        public List<String> getStill_sizes() {
            return still_sizes;
        }

        public void setStill_sizes(List<String> still_sizes) {
            this.still_sizes = still_sizes;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }
    }
}