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

import com.jerrellmardis.amphitheatre.util.VideoUtils;

import org.apache.commons.collections4.ListUtils;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/5/14.
 */
public class GetFilesTask extends AsyncTask<Void, Void, List<SmbFile>> implements DownloadMovieInfoTask.OnTaskCompletedListener {

    private String mPath;
    private String mUser;
    private String mPassword;
    private AtomicInteger mSetsProcessedCounter;
    private OnTaskCompletedListener mOnTaskCompletedListener;
    private int mNumOfSets;

    public interface OnTaskCompletedListener {
        void onTaskCompleted();
        void onTaskFailed();
    }

    public GetFilesTask(String user, String password, String path, OnTaskCompletedListener l) {
        mUser = user;
        mPassword = password;
        mPath = path;
        mOnTaskCompletedListener = l;

        mSetsProcessedCounter = new AtomicInteger(0);

        if (!mPath.endsWith("/")) {
            mPath += "/";
        }
    }

    @Override
    protected List<SmbFile> doInBackground(Void... params) {
        return new ArrayList<SmbFile>(getFiles());
    }

    @Override
    protected void onPostExecute(List<SmbFile> files) {
        try {
            final int cpuCount = Runtime.getRuntime().availableProcessors();
            final int maxPoolSize = cpuCount * 2 + 1;
            final int partitionSize = files.size() < maxPoolSize ? files.size() : (files.size() / maxPoolSize);

            List<List<SmbFile>> subSets = ListUtils.partition(files, partitionSize);

            mNumOfSets = subSets.size();

            String[] sections = mPassword.split("/");
            String directory = sections[sections.length - 1];
            for (List<SmbFile> subSet : subSets) {
                new DownloadMovieInfoTask(directory, subSet, this).executeOnExecutor(THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            if (mOnTaskCompletedListener != null) {
                mOnTaskCompletedListener.onTaskFailed();
            }
        }
    }

    @Override
    public void onTaskCompleted() {
        if (incrementAndGetSetsProcessed() == mNumOfSets - 1 && mOnTaskCompletedListener != null) {
            mOnTaskCompletedListener.onTaskCompleted();
        }
    }

    private List<SmbFile> getFiles() {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", mUser, mPassword);

        List<SmbFile> files = Collections.emptyList();
        try {
            String domain = "smb://" + mPath;
            files = VideoUtils.getFilesFromDir(domain, auth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    private int incrementAndGetSetsProcessed() {
        return mSetsProcessedCounter.getAndIncrement();
    }
}
