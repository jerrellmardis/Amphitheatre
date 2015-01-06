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
import android.os.AsyncTask;

import com.jerrellmardis.amphitheatre.api.ApiClient;
import com.jerrellmardis.amphitheatre.api.TMDbClient;
import com.jerrellmardis.amphitheatre.listeners.TaskListener;
import com.jerrellmardis.amphitheatre.model.tmdb.Config;

import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public class GetFilesTask extends AsyncTask<Void, Void, List<SmbFile>> implements TaskListener {

    private Context mContext;
    private String mPath;
    private String mUser;
    private String mPassword;
    private AtomicInteger mSetsProcessedCounter;
    private Callback mCallback;
    private Config mConfig;
    private int mNumOfSets;
    private boolean mIsMovie;

    public interface Callback {
        void success();
        void failure();
    }

    public GetFilesTask(Context context, String user, String password, String path, boolean isMovie,
                        Callback l) {

        mContext = context;
        mUser = user;
        mPassword = password;
        mPath = path;
        mIsMovie = isMovie;
        mCallback = l;

        mSetsProcessedCounter = new AtomicInteger(0);

        if (!mPath.startsWith("smb://")) {
            mPath = "smb://" + mPath;
        }
        if (!mPath.endsWith("/")) {
            mPath += "/";
        }
    }

    @Override
    protected List<SmbFile> doInBackground(Void... params) {
        mConfig = ApiClient.getInstance().createTMDbClient().getConfig();
        return new ArrayList<SmbFile>(DownloadTaskHelper.getFiles(mUser, mPassword, mPath));
    }

    @Override
    protected void onPostExecute(List<SmbFile> files) {
        try {
            final int cpuCount = Runtime.getRuntime().availableProcessors();
            final int maxPoolSize = cpuCount * 2 + 1;
            final int partitionSize = files.size() < maxPoolSize ? files.size() : (files.size() / maxPoolSize);

            List<List<SmbFile>> subSets = ListUtils.partition(files, partitionSize);

            mNumOfSets = subSets.size();

            for (List<SmbFile> subSet : subSets) {
                if (mIsMovie) {
                    new DownloadMovieTask(mContext, mConfig, subSet, this)
                            .executeOnExecutor(THREAD_POOL_EXECUTOR);
                } else {
                    new DownloadTvShowTask(mContext, mConfig, subSet, this)
                            .executeOnExecutor(THREAD_POOL_EXECUTOR);
                }
            }
        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.failure();
            }
        }
    }

    @Override
    public void taskCompleted() {
        if (mSetsProcessedCounter.getAndIncrement() == mNumOfSets - 1 && mCallback != null) {
            mCallback.success();
        }
    }
}