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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.jerrellmardis.amphitheatre.listeners.TaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;
import com.jerrellmardis.amphitheatre.util.Constants;

import java.util.List;

import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public abstract class DownloadVideoTask extends AsyncTask<Void, Video, Boolean> {

    protected Config mConfig;
    protected List<SmbFile> mFiles;

    private Context mContext;
    private TaskListener mTaskListener;

    public DownloadVideoTask(Context context, Config config, List<SmbFile> files, TaskListener l) {

        mContext = context;
        mFiles = files;
        mTaskListener = l;
        mConfig = config;
    }

    @Override
    protected void onProgressUpdate(Video... values) {
        super.onProgressUpdate(values);
        Intent i = new Intent(Constants.VIDEO_UPDATE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.VIDEO, values[0]);
        i.putExtras(bundle);
        mContext.sendBroadcast(i);
    }

    @Override
    protected void onPostExecute(Boolean processed) {
        if (mTaskListener != null) {
            mTaskListener.taskCompleted();
        }
    }
}