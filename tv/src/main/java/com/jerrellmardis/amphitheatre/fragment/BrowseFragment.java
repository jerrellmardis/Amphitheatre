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

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.activity.SearchActivity;
import com.jerrellmardis.amphitheatre.model.Source;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.jerrellmardis.amphitheatre.task.GetFilesTask;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.jerrellmardis.amphitheatre.util.SecurePreferences;
import com.jerrellmardis.amphitheatre.util.VideoUtils;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;
import com.jerrellmardis.amphitheatre.widget.TvShowsCardPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.OnClickListener;

public class BrowseFragment extends android.support.v17.leanback.app.BrowseFragment {

    private final Handler mHandler = new Handler();

    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundImageUrl;

    private List<Video> mMatchedMovies;
    private List<Video> mUnmatchedVideos;
    private List<Video> mMatchedTvShows;

    private AddSourceDialogFragment mAddSourceDialogFragment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();
        setupUIElements();
        refreshAdapter();
        setupEventListeners();
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mAddSourceDialogFragment != null) {
            mAddSourceDialogFragment.setOnClickListener(getDefaultClickListener());
        }
    }

    private void prepareBackgroundManager() {
        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.amphitheatre);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        Picasso.with(getActivity())
                .load(R.drawable.amphitheatre)
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .skipMemoryCache()
                .into(mBackgroundTarget);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.browse_title));

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        setBrandColor(getResources().getColor(R.color.fastlane_background));
        setSearchAffordanceColor(getResources().getColor(R.color.search_button_background));
    }

    private void refreshAdapter() {
        List<Video> allVideos = Source.listAll(Video.class);

        if (!allVideos.isEmpty()) {
            mMatchedMovies = new ArrayList<Video>();
            mUnmatchedVideos = new ArrayList<Video>();
            mMatchedTvShows = new ArrayList<Video>();

            for (Video video : allVideos) {
                if (video.isMatched() && video.isMovie()) {
                    mMatchedMovies.add(video);
                } else if (video.isMatched()) {
                    mMatchedTvShows.add(video);
                } else {
                    mUnmatchedVideos.add(video);
                }
            }

            updateAdapter();
        }
    }

    private void setupEventListeners() {
        setOnItemSelectedListener(getDefaultItemSelectedListener());
        setOnItemClickedListener(getDefaultItemClickedListener());
        setOnSearchClickedListener(getSearchClickedListener());
        setBrowseTransitionListener(getBrowseTransitionListener());
    }

    private void resetBackground() {
        // Make sure default background is loaded
        if (mBackgroundImageUrl != null) {
            mBackgroundImageUrl = null;
        }
        startBackgroundTimer();
    }

    private void updateAdapter() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter(getActivity());
        TvShowsCardPresenter tvShowsCardPresenter = new TvShowsCardPresenter(getActivity());

        List<Source> sources = Source.listAll(Source.class);

        Map<String, List<Video>> categoryMovieMap = new HashMap<String, List<Video>>();
        Map<String, VideoGroup> categoryTvShowMap = new HashMap<String, VideoGroup>();

        for (Video video : mMatchedMovies) {
            for (Source source : sources) {
                if (video.getVideoUrl().contains(source.toString())) {
                    String[] sections = source.toString().split("/");
                    String category = sections[sections.length - 1];

                    if (categoryMovieMap.containsKey(category)) {
                        categoryMovieMap.get(category).add(video);
                    } else {
                        List<Video> videos = new ArrayList<Video>();
                        videos.add(video);
                        categoryMovieMap.put(category, videos);
                    }

                    break;
                }
            }
        }

        // add matched movies
        for (Map.Entry<String, List<Video>> entry : categoryMovieMap.entrySet()) {
            if (entry.getValue().size() == 0) {
                continue;
            }

            sort(entry.getValue());

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            listRowAdapter.addAll(0, entry.getValue());

            HeaderItem header = new HeaderItem(0, entry.getKey(), null);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        // group TV shows by name
        for (Video video : mMatchedTvShows) {
            if (!categoryTvShowMap.containsKey(video.getName())) {
                VideoGroup group = new VideoGroup();
                group.setVideo(video);
                group.increment();
                categoryTvShowMap.put(video.getName(), group);
            } else {
                categoryTvShowMap.get(video.getName()).increment();
            }
        }

        // add matched TV shows
        if (!categoryTvShowMap.isEmpty()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(tvShowsCardPresenter);
            listRowAdapter.addAll(0, categoryTvShowMap.values());

            HeaderItem header = new HeaderItem(0, getString(R.string.tv_shows), null);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        // add unmatched videos
        if (!mUnmatchedVideos.isEmpty()) {
            sort(mUnmatchedVideos);

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            listRowAdapter.addAll(0, mUnmatchedVideos);

            HeaderItem header = new HeaderItem(0, getString(R.string.unmatched), null);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        // add a Settings header and action buttons
        HeaderItem gridHeader = new HeaderItem(0, getResources().getString(R.string.settings), null);
        GridItemPresenter gridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(gridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.add_source));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(rowsAdapter);
    }

    private void updateBackground(String url) {
        RequestCreator rc;

        if (TextUtils.isEmpty(url)) {
            rc = Picasso.with(getActivity()).load(R.drawable.placeholder);
        } else {
            rc = Picasso.with(getActivity()).load(url);
        }

        int w = mMetrics.widthPixels;
        int h = mMetrics.heightPixels;

        rc.resize(w, h).centerCrop().error(mDefaultBackground).skipMemoryCache().into(mBackgroundTarget);
    }

    private void clearBackground() {
        BackgroundManager.getInstance(getActivity()).setDrawable(mDefaultBackground);
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), 300);
    }

    private void sort(List<Video> videos) {
        // sort alphabetically
        Collections.sort(videos, new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                if (o2.getName() == null) {
                    return (o1.getName() == null) ? 0 : -1;
                }
                if (o1.getName() == null) {
                    return 1;
                }
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
    }

    private OnClickListener getSearchClickedListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        };
    }

    private OnItemSelectedListener getDefaultItemSelectedListener() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(Object item, Row row) {
                if (item instanceof Video) {
                    try {
                        mBackgroundImageUrl = ((Video) item).getBackgroundImageUrl();
                        startBackgroundTimer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item instanceof VideoGroup) {
                    try {
                        mBackgroundImageUrl = ((VideoGroup) item).getVideo().getBackgroundImageUrl();
                        startBackgroundTimer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private BrowseTransitionListener getBrowseTransitionListener() {
        return new BrowseTransitionListener() {

            @Override
            public void onHeadersTransitionStop(boolean withHeaders) {
                if (withHeaders) {
                    resetBackground();
                }
            }

        };
    }

    private OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof Video || item instanceof VideoGroup) {
                    Video video;

                    if (item instanceof VideoGroup) {
                        video = ((VideoGroup) item).getVideo();
                    } else {
                        video = (Video) item;
                    }

                    if (!video.isMatched()) {
                        VideoUtils.playVideo(new WeakReference<Activity>(getActivity()), video);
                        return;
                    }

                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.VIDEO, video);
                    startActivity(intent);
                } else if (item instanceof String) {
                    if (((String) item).contains(getString(R.string.add_source))) {
                        FragmentManager fm = getFragmentManager();
                        mAddSourceDialogFragment = AddSourceDialogFragment.newInstance();
                        mAddSourceDialogFragment.setOnClickListener(getDefaultClickListener());
                        mAddSourceDialogFragment.show(fm, AddSourceDialogFragment.class.getSimpleName());
                    }
                }
            }
        };
    }

    private AddSourceDialogFragment.OnClickListener getDefaultClickListener() {
        return new AddSourceDialogFragment.OnClickListener() {

            @Override
            public void onAddClicked(CharSequence user, CharSequence password,
                                     final CharSequence path, boolean isMovie) {

                Toast.makeText(getActivity(), getString(R.string.updating_library),
                        Toast.LENGTH_SHORT).show();

                new GetFilesTask(user.toString(), password.toString(), path.toString(), isMovie,
                        new GetFilesTask.Callback() {

                            @Override
                            public void success() {
                                if (getActivity() == null) return;

                                Source source = new Source();
                                source.setSource(path.toString());
                                source.save();

                                refreshAdapter();

                                Toast.makeText(getActivity(), getString(R.string.update_complete),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure() {
                                Toast.makeText(getActivity(), getString(R.string.update_failed),
                                        Toast.LENGTH_LONG).show();
                            }
                        }).execute();

                SecurePreferences securePreferences = new SecurePreferences(getActivity().getApplicationContext());
                securePreferences.edit().putString(Constants.PREFS_USER_KEY, user.toString()).apply();
                securePreferences.edit().putString(Constants.PREFS_PASSWORD_KEY, password.toString()).apply();
            }
        };
    }

    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundImageUrl != null) {
                        updateBackground(mBackgroundImageUrl);
                    } else {
                        clearBackground();
                    }
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(240, 240));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.primary));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) { }
    }
}