/*
 * Copyright (C) 2014 Jerrell Mardis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jerrellmardis.amphitheatre.model;

import static com.jerrellmardis.amphitheatre.model.Source.Type;

public class GridGenre {

    private String mTitle;
    private Type mType;

    public GridGenre(String title, Type type) {
        mTitle = title;
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Source.Type getType() {
        return mType;
    }

    public void setType(Source.Type mType) {
        this.mType = mType;
    }
}