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

package com.jerrellmardis.amphitheatre.task;

import android.content.Context;

import com.jerrellmardis.amphitheatre.listeners.TaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;

import java.util.List;

import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public class DownloadMovieTask extends DownloadVideoTask {

    public DownloadMovieTask(Context context, Config config, List<SmbFile> files,
                             TaskListener l) {

        super(context, config, files, l);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        for (SmbFile file : mFiles) {
            Video video = DownloadTaskHelper.downloadMovieData(mConfig, file);
            if (video != null) {
                publishProgress(video);
            }
        }
        return true;
    }
}