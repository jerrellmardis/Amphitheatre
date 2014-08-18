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

package com.jerrellmardis.amphitheatre.model;

import java.io.Serializable;

/**
 * Created by Jerrell Mardis on 8/8/14.
 */
public class VideoGroup implements Serializable {

    private Video mVideo;
    private int numOfVideos = 1;

    public VideoGroup(Video video) {
        mVideo = video;
    }

    public Video getVideo() {
        return mVideo;
    }

    public void setVideo(Video video) {
        mVideo = video;
    }

    public int getNumOfVideos() {
        return numOfVideos;
    }

    public void increment() {
        numOfVideos++;
    }
}