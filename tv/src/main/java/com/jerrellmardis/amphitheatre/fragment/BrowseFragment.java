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
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Row;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.activity.GridViewActivity;
import com.jerrellmardis.amphitheatre.activity.SearchActivity;
import com.jerrellmardis.amphitheatre.model.GridGenre;
import com.jerrellmardis.amphitheatre.model.Source;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.jerrellmardis.amphitheatre.service.RecommendationsService;
import com.jerrellmardis.amphitheatre.task.GetFilesTask;
import com.jerrellmardis.amphitheatre.util.BlurTransform;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.jerrellmardis.amphitheatre.util.SecurePreferences;
import com.jerrellmardis.amphitheatre.util.VideoUtils;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;
import com.jerrellmardis.amphitheatre.widget.GridItemPresenter;
import com.jerrellmardis.amphitheatre.widget.SortedObjectAdapter;
import com.jerrellmardis.amphitheatre.widget.TvShowsCardPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import static android.view.View.OnClickListener;

public class BrowseFragment extends android.support.v17.leanback.app.BrowseFragment
        implements AddSourceDialogFragment.OnClickListener {

    private final Handler mHandler = new Handler();

    private Transformation mBlurTransformation;
    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundImageUrl;
    private ArrayObjectAdapter mAdapter;
    private CardPresenter mCardPresenter;
    private TvShowsCardPresenter mTvShowsCardPresenter;

    private BroadcastReceiver videoUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Serializable obj = bundle.getSerializable(Constants.VIDEO);
                if (obj instanceof Video) {
                    addVideoToUi((Video) bundle.getSerializable(Constants.VIDEO));
                }
            }
        }
    };

    private BroadcastReceiver libraryUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
            Toast.makeText(getActivity(), getString(R.string.update_complete),
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mCardPresenter = new CardPresenter(getActivity());
        mTvShowsCardPresenter = new TvShowsCardPresenter(getActivity());
        mAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        addSettingsHeader();
        setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBlurTransformation = new BlurTransform(getActivity());
        prepareBackgroundManager();
        setupUIElements();
        setupEventListeners();


        if (Video.count(Video.class, null, null) == 0) {
            showAddSourceDialog();
        } else {
            loadVideos();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().registerReceiver(videoUpdateReceiver,
                new IntentFilter(Constants.VIDEO_UPDATE_ACTION));
        getActivity().registerReceiver(libraryUpdateReceiver,
                new IntentFilter(Constants.LIBRARY_UPDATED_ACTION));

        // TODO video(s) may have been watched before returning back here, so we need to refresh the view.
        // This could be important if we want to display "watched" indicators on the cards.
    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(videoUpdateReceiver);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        try {
            getActivity().unregisterReceiver(libraryUpdateReceiver);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        super.onStop();
    }

    @Override
    public void onAddClicked(CharSequence user, CharSequence password,
                             final CharSequence path, boolean isMovie) {

        Toast.makeText(getActivity(), getString(R.string.updating_library),
                Toast.LENGTH_SHORT).show();

        Source source = new Source();
        source.setSource(path.toString());
        source.setType(isMovie ? Source.Type.MOVIE.name() : Source.Type.TV_SHOW.name());
        source.save();

        new GetFilesTask(getActivity(), user.toString(), password.toString(), path.toString(),
                isMovie, new GetFilesTask.Callback() {

                    @Override
                    public void success() {
                        if (getActivity() == null) return;

                        Toast.makeText(getActivity(), getString(R.string.update_complete),
                                Toast.LENGTH_SHORT).show();

                        rebuildSubCategories();

                        reloadAdapters();

                        updateRecommendations();
                    }

                    @Override
                    public void failure() {
                        if (getActivity() == null) return;

                        Toast.makeText(getActivity(), getString(R.string.update_failed),
                                Toast.LENGTH_LONG).show();
                    }
                }).execute();

        SecurePreferences securePreferences = new SecurePreferences(getActivity().getApplicationContext());
        securePreferences.edit().putString(Constants.PREFS_USER_KEY, user.toString()).apply();
        securePreferences.edit().putString(Constants.PREFS_PASSWORD_KEY, password.toString()).apply();
    }

    private void reloadAdapters() {
        for (int i = 0; i < mAdapter.size(); i++) {
            ListRow listRow = (ListRow) mAdapter.get(i);
            ObjectAdapter objectAdapter = listRow.getAdapter();
            if (objectAdapter instanceof ArrayObjectAdapter) {
                ArrayObjectAdapter arrayObjectAdapter = ((ArrayObjectAdapter) objectAdapter);
                arrayObjectAdapter.notifyArrayItemRangeChanged(0, arrayObjectAdapter.size());
            }
        }
    }

    private void loadVideos() {
        List<Video> videos = Source.listAll(Video.class);
        if (videos != null && !videos.isEmpty()) {
            for (Video video : videos) {
                addVideoToUi(video);
            }

            rebuildSubCategories();

            reloadAdapters();
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

    private void addVideoToUi(Video video) {
        Comparator<Video> videoNameComparator = new Comparator<Video>() {
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
        };

        Comparator<VideoGroup> videoGroupNameComparator = new Comparator<VideoGroup>() {
            @Override
            public int compare(VideoGroup o1, VideoGroup o2) {
                if (o2.getVideo().getName() == null) {
                    return (o1.getVideo().getName() == null) ? 0 : -1;
                }
                if (o1.getVideo().getName() == null) {
                    return 1;
                }
                return o1.getVideo().getName().toLowerCase().compareTo(o2.getVideo().getName().toLowerCase());
            }
        };

        if (!video.isMatched()) {
            ListRow row = findListRow(getString(R.string.unmatched));

            // if found add this video
            // if not, create a new row and add it
            if (row != null) {
                ((SortedObjectAdapter) row.getAdapter()).add(video);
            } else {
                SortedObjectAdapter listRowAdapter = new SortedObjectAdapter(
                        videoNameComparator, mCardPresenter);
                listRowAdapter.add(video);

                HeaderItem header = new HeaderItem(0, getString(R.string.unmatched), null);
                int index = mAdapter.size() > 1 ? mAdapter.size() - 1 : 0;
                mAdapter.add(index, new ListRow(header, listRowAdapter));
            }
        } else if (video.isMovie()) {
            List<Source> sources = Source.listAll(Source.class);

            for (Source source : sources) {
                // find the video's "source" and use it as a category
                if (video.getVideoUrl().contains(source.toString())) {
                    String[] sections = source.toString().split("/");
                    String category = String.format(getString(R.string.all_category_placeholder),
                            sections[sections.length - 1]);

                    ListRow row = findListRow(category);

                    // if found add this video
                    // if not, create a new row and add it
                    if (row != null) {
                        ((SortedObjectAdapter) row.getAdapter()).add(video);
                    } else {
                        SortedObjectAdapter listRowAdapter = new SortedObjectAdapter(
                                videoNameComparator, mCardPresenter);
                        listRowAdapter.add(video);

                        HeaderItem header = new HeaderItem(0, category, null);
                        mAdapter.add(0, new ListRow(header, listRowAdapter));
                    }

                    break;
                }
            }
        } else {
            ListRow row = findListRow(getString(R.string.all_tv_shows));

            // if found add this video
            // if not, create a new row and add it
            if (row != null) {
                boolean found = false;

                // find the video group and increment the episode count
                for (int i = 0; i < row.getAdapter().size(); i++) {
                    VideoGroup group = (VideoGroup) row.getAdapter().get(i);

                    if (group.getVideo().getName().equals(video.getName())) {
                        if (TextUtils.isEmpty(group.getVideo().getCardImageUrl())) {
                            group.getVideo().setCardImageUrl(video.getCardImageUrl());
                        }

                        group.increment();
                        found = true;
                        break;
                    }
                }

                // if not found, then add the VideoGroup to the row
                if (!found) {
                    ((SortedObjectAdapter) row.getAdapter()).add(new VideoGroup(video));
                }
            } else {
                SortedObjectAdapter listRowAdapter = new SortedObjectAdapter(
                        videoGroupNameComparator, mTvShowsCardPresenter);
                listRowAdapter.add(new VideoGroup(video));

                HeaderItem header = new HeaderItem(0, getString(R.string.all_tv_shows), null);
                mAdapter.add(0, new ListRow(header, listRowAdapter));
            }
        }
    }

    private void rebuildSubCategories() {
        List<Video> videos = Video.listAll(Video.class);
        Collections.sort(videos, new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                return Long.valueOf(o2.getCreated()).compareTo(o1.getCreated());
            }
        });

        // get top 15 movies and TV shows
        final int max = 15;
        List<Video> movies = new ArrayList<Video>(max);
        List<Video> tvShows = new ArrayList<Video>(max);

        for (Video video : videos) {
            if (movies.size() == max && tvShows.size() == max) {
                break;
            }

            if (video.isMatched() && video.isMovie() && movies.size() <= max) {
                movies.add(video);
            } else if (video.isMatched() && !video.isMovie() && tvShows.size() <= max) {
                tvShows.add(video);
            }
        }

        ListRow unMatchedRow = findListRow(getString(R.string.unmatched));

        // recently added movies
        addRecentlyAddedMovies(movies, unMatchedRow);

        // recently added TV shows
        addRecentlyAddedTvShows(tvShows, unMatchedRow);

        // add genres for movies & TV Shows
        addGenres(videos, unMatchedRow);
    }

    private void addRecentlyAddedTvShows(List<Video> tvShows, ListRow unMatchedRow) {
        if (!tvShows.isEmpty()) {
            ListRow row = findListRow(getString(R.string.recently_added_tv_episodes));
            if (row != null) {
                ((ArrayObjectAdapter) row.getAdapter()).clear();
                ((ArrayObjectAdapter) row.getAdapter()).addAll(0, tvShows);
            } else {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mTvShowsCardPresenter);
                listRowAdapter.addAll(0, tvShows);

                HeaderItem header = new HeaderItem(0, getString(R.string.recently_added_tv_episodes), null);
                int index = mAdapter.size() > 1 ? mAdapter.size() - 1 : 0;
                if (unMatchedRow != null) index -= 1;
                mAdapter.add(index, new ListRow(header, listRowAdapter));
            }
        }
    }

    private void addRecentlyAddedMovies(List<Video> movies, ListRow unMatchedRow) {
        if (!movies.isEmpty()) {
            ListRow row = findListRow(getString(R.string.recently_added_movies));
            if (row != null) {
                ((ArrayObjectAdapter) row.getAdapter()).clear();
                ((ArrayObjectAdapter) row.getAdapter()).addAll(0, movies);
            } else {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mCardPresenter);
                listRowAdapter.addAll(0, movies);

                HeaderItem header = new HeaderItem(0, getString(R.string.recently_added_movies), null);
                int index = mAdapter.size() > 1 ? mAdapter.size() - 1 : 0;
                if (unMatchedRow != null) index -= 1;
                mAdapter.add(index, new ListRow(header, listRowAdapter));
            }
        }
    }

    private void addGenres(List<Video> videos, ListRow unMatchedRow) {
        Set<String> movieGenres = new TreeSet<String>();
        Set<String> tvShowGenres = new TreeSet<String>();

        for (Video video : videos) {
            if (video.isMovie()) {
                if (video.getMovie() != null && video.getMovie().getFlattenedGenres() != null) {
                    String[] gs = video.getMovie().getFlattenedGenres().split(",");
                    if (gs.length > 0) {
                        for (String genre : gs) {
                            if (genre.trim().length() > 0) {
                                movieGenres.add(genre);
                            }
                        }
                    }
                }
            } else {
                if (video.getTvShow() != null && video.getTvShow().getFlattenedGenres() != null) {
                    String[] gs = video.getTvShow().getFlattenedGenres().split(",");
                    if (gs.length > 0) {
                        for (String genre : gs) {
                            if (genre.trim().length() > 0) {
                                tvShowGenres.add(genre);
                            }
                        }
                    }
                }
            }
        }

        if (!movieGenres.isEmpty()) {
            HeaderItem gridHeader = new HeaderItem(0, getString(R.string.movies_genre), null);
            ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(new GridItemPresenter(getActivity()));
            for (String genre : movieGenres) {
                gridRowAdapter.add(new GridGenre(genre, Source.Type.MOVIE));
            }
            int index = mAdapter.size() > 1 ? mAdapter.size() - 1 : 0;
            if (unMatchedRow != null) index -= 1;
            mAdapter.add(index, new ListRow(gridHeader, gridRowAdapter));
        }

        if (!tvShowGenres.isEmpty()) {
            HeaderItem gridHeader = new HeaderItem(0, getString(R.string.tvshows_genre), null);
            ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(new GridItemPresenter(getActivity()));
            for (String genre : tvShowGenres) {
                gridRowAdapter.add(new GridGenre(genre, Source.Type.TV_SHOW));
            }
            int index = mAdapter.size() > 1 ? mAdapter.size() - 1 : 0;
            if (unMatchedRow != null) index -= 1;
            mAdapter.add(index, new ListRow(gridHeader, gridRowAdapter));
        }
    }

    private void refresh() {
        mAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        addSettingsHeader();
        loadVideos();
        setAdapter(mAdapter);
    }

    private void addSettingsHeader() {
        HeaderItem gridHeader = new HeaderItem(0, getString(R.string.settings), null);
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(new GridItemPresenter(getActivity()));
        gridRowAdapter.add(getString(R.string.add_source));
        mAdapter.add(new ListRow(gridHeader, gridRowAdapter));
    }

    private ListRow findListRow(String headerName) {
        for (int i = 0; i < mAdapter.size(); i++) {
            ListRow row = (ListRow) mAdapter.get(i);
            if (headerName.equals(row.getHeaderItem().getName())) {
                return row;
            }
        }

        return null;
    }

    private void updateRecommendations() {
        getActivity().startService(new Intent(getActivity(), RecommendationsService.class));
    }

    private void updateBackground(String url) {
        Picasso.with(getActivity())
                .load(url)
                .transform(mBlurTransformation)
                .placeholder(R.drawable.placeholder)
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .skipMemoryCache()
                .into(mBackgroundTarget);
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
                    if (item instanceof VideoGroup) {
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(Constants.IS_VIDEO, false);
                        intent.putExtra(Constants.VIDEO_GROUP, (VideoGroup) item);
                        startActivity(intent);
                    } else if (((Video) item).isMatched()) {
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(Constants.IS_VIDEO, true);
                        intent.putExtra(Constants.VIDEO, (Video) item);
                        startActivity(intent);
                    } else {
                        VideoUtils.playVideo(new WeakReference<Activity>(getActivity()), (Video) item);
                    }
                } else if (item instanceof GridGenre) {
                    GridGenre genre = (GridGenre) item;
                    Intent intent = new Intent(getActivity(), GridViewActivity.class);
                    intent.putExtra(Constants.GENRE, genre.getTitle());
                    if (genre.getType() == Source.Type.MOVIE) {
                        intent.putExtra(Constants.IS_VIDEO, true);
                    } else {
                        intent.putExtra(Constants.IS_VIDEO, false);
                    }
                    startActivity(intent);
                } else if (item instanceof String && ((String) item).contains(getString(R.string.add_source))) {
                    showAddSourceDialog();
                }
            }
        };
    }

    private void showAddSourceDialog() {
        FragmentManager fm = getFragmentManager();
        AddSourceDialogFragment addSourceDialog = AddSourceDialogFragment.newInstance();
        addSourceDialog.setTargetFragment(this, 0);
        addSourceDialog.show(fm, AddSourceDialogFragment.class.getSimpleName());
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
}