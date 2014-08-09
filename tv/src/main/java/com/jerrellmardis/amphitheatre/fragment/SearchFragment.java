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

package com.jerrellmardis.amphitheatre.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.text.TextUtils;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.model.Source;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.VideoUtils;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends android.support.v17.leanback.app.SearchFragment
        implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider {

    private static final int SEARCH_DELAY_MS = 300;

    private List<Video> allVideos;
    private ArrayObjectAdapter mListRowAdapter;
    private ArrayObjectAdapter mRowsAdapter;
    private Handler mHandler = new Handler();
    private SearchRunnable mDelayedLoad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allVideos = Source.listAll(Video.class);

        mListRowAdapter = new ArrayObjectAdapter(new CardPresenter(getActivity()));
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);
        setOnItemClickedListener(getDefaultItemClickedListener());
        mDelayedLoad = new SearchRunnable();
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        mRowsAdapter.clear();
        if (!TextUtils.isEmpty(newQuery)) {
            mDelayedLoad.setSearchQuery(newQuery);
            mHandler.removeCallbacks(mDelayedLoad);
            mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mRowsAdapter.clear();
        if (!TextUtils.isEmpty(query)) {
            mDelayedLoad.setSearchQuery(query);
            mHandler.removeCallbacks(mDelayedLoad);
            mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
        }
        return true;
    }

    private OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof Video) {
                    Video video = (Video) item;

                    if (!video.isMatched()) {
                        VideoUtils.playVideo(new WeakReference<Activity>(getActivity()), (Video) item);
                        return;
                    }

                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.VIDEO, video);
                    startActivity(intent);
                }
            }
        };
    }

    private class SearchRunnable implements Runnable {

        private volatile String mSearchQuery;

        @Override
        public void run() {
            loadRows(mSearchQuery);
        }

        public void setSearchQuery(String query) {
            mSearchQuery = query;
        }

        private void loadRows(String query) {
            mListRowAdapter.clear();

            Map<String, Video> searchMap = new HashMap<String, Video>();

            for (Video video : allVideos) {
                if (video == null || video.getName() == null) {
                    continue;
                }

                if (video.getName().toLowerCase().contains(query.toLowerCase())) {
                    if (!searchMap.containsKey(video.getName())) {
                        searchMap.put(video.getName(), video);
                    }
                }
            }

            mListRowAdapter.addAll(0, searchMap.values());

            HeaderItem header = new HeaderItem(0, getResources().getString(R.string.search_results), null);
            mRowsAdapter.add(new ListRow(header, mListRowAdapter));
        }
    }
}