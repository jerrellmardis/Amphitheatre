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

public enum Language {
    ALL(0, "all"),
    ENGLISH(7, "en");

    private int mId;

    private String mCode;

    private Language(int id, String code) {
        mId = id;
        mCode = code;
    }

    public static Language parse(String code) {
        for (Language language : values()) {
            if (language.getCode().equalsIgnoreCase(code)) {
                return language;
            }
        }
        return null;
    }

    public int getId() {
        return mId;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public String toString() {
        return mCode;
    }

}
