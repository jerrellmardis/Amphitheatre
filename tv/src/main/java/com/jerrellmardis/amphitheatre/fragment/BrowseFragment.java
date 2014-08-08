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
import com.jerrellmardis.amphitheatre.activity.BrowseActivity;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.activity.SearchActivity;
import com.jerrellmardis.amphitheatre.model.Movie;
import com.jerrellmardis.amphitheatre.model.Source;
import com.jerrellmardis.amphitheatre.task.GetFilesTask;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.jerrellmardis.amphitheatre.util.SecurePreferences;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BrowseFragment extends android.support.v17.leanback.app.BrowseFragment implements
        BrowseActivity.BackPressedCallback{

    private final Handler mHandler = new Handler();

    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private List<Movie> mVideos;
    private List<Movie> mUnmatchedVideos;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        mVideos = Collections.synchronizedList(new ArrayList<Movie>());
        mUnmatchedVideos = Collections.synchronizedList(new ArrayList<Movie>());

        List<Movie> allMovies = Movie.listAll(Movie.class);

        if (!allMovies.isEmpty()) {
            refreshAdapter();
        }

        setupEventListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetBackground();
    }

    private void resetBackground() {
        // Make sure default background is loaded
        if (mBackgroundURI != null) {
            mBackgroundURI = null;
        }
        startBackgroundTimer();
    }

    private OnItemSelectedListener getDefaultItemSelectedListener() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(Object item, Row row) {
                if (item instanceof Movie) {
                    try {
                        mBackgroundURI = ((Movie) item).getBackgroundImageURI();
                        startBackgroundTimer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void refreshAdapter() {
        List<Movie> allMovies = Movie.listAll(Movie.class);

        if (!allMovies.isEmpty()) {
            mVideos.clear();
            mUnmatchedVideos.clear();

            for (Movie movie : allMovies) {
                if (movie.isMatched()) {
                    mVideos.add(movie);
                } else {
                    mUnmatchedVideos.add(movie);
                }
            }

            updateAdapter();
        }
    }

    private OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof Movie) {
                    Movie movie = (Movie) item;
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(getString(R.string.movie), movie);
                    startActivity(intent);
                }
                else if (item instanceof String) {
                    if (((String) item).contains(getString(R.string.add_source))) {
                        FragmentManager fm = getFragmentManager();
                        AddSourceDialogFragment addSourceDialog =
                                AddSourceDialogFragment.newInstance(getString(R.string.add_source),
                                        getDefaultClickListener());
                        addSourceDialog.show(fm, AddSourceDialogFragment.class.getSimpleName());
                    }
                }
            }
        };
    }

    private AddSourceDialogFragment.OnClickListener getDefaultClickListener() {
        return new AddSourceDialogFragment.OnClickListener() {
            @Override
            public void onAddClicked(CharSequence user, CharSequence password, final CharSequence path) {
                Toast.makeText(getActivity(), getString(R.string.updating_library),
                        Toast.LENGTH_SHORT).show();

                new GetFilesTask(user.toString(), password.toString(), path.toString(),
                        new GetFilesTask.OnTaskCompletedListener() {

                            @Override
                            public void onTaskCompleted() {
                                if (getActivity() == null) return;

                                Source source = new Source();
                                source.setSource(path.toString());
                                source.save();

                                refreshAdapter();

                                Toast.makeText(getActivity(), getString(R.string.update_complete),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onTaskFailed() {
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

    private void updateBackground(URI uri) {
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }

    private void clearBackground() {
        BackgroundManager.getInstance(getActivity()).setDrawable(mDefaultBackground);
    }

    private void updateAdapter() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter(getActivity());

        // get all categories
        List<Source> sources = Source.listAll(Source.class);

        sort(mVideos);

        Map<String, List<Movie>> categoryMap = new HashMap<String, List<Movie>>();
        for (Movie movie : mVideos) {
            for (Source source : sources) {
                if (movie.getVideoUrl().contains(source.toString())) {
                    String[] sections = source.toString().split("/");
                    String category = sections[sections.length - 1];

                    if (categoryMap.containsKey(category)) {
                        categoryMap.get(category).add(movie);
                    } else {
                        List<Movie> movies = new ArrayList<Movie>();
                        movies.add(movie);
                        categoryMap.put(category, movies);
                    }
                }
            }
        }

        for (Map.Entry<String, List<Movie>> entry : categoryMap.entrySet()) {
            if (entry.getValue().size() == 0) {
                continue;
            }

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            listRowAdapter.addAll(0, entry.getValue());

            HeaderItem header = new HeaderItem(0, entry.getKey() + " (" + entry.getValue().size() + ")", null);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        // add a section for any video files that weren't matched
        if (!mUnmatchedVideos.isEmpty()) {
            sort(mUnmatchedVideos);

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            listRowAdapter.addAll(0, mUnmatchedVideos);

            HeaderItem header = new HeaderItem(0, String.format(getString(R.string.unmatched), mUnmatchedVideos.size()), null);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        // add a Settings header
        HeaderItem gridHeader = new HeaderItem(0, getResources().getString(R.string.settings), null);

        // add action buttons to the settings header
        GridItemPresenter gridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(gridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.add_source));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(rowsAdapter);
    }

    private void sort(List<Movie> movies) {
        // sort alphabetically
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                if (o2.getTitle() == null) {
                    return (o1.getTitle() == null) ? 0 : -1;
                }
                if (o1.getTitle() == null) {
                    return 1;
                }
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            }
        });
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
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
                                                    // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_button_background));
    }

    private void setupEventListeners() {
        setOnItemSelectedListener(getDefaultItemSelectedListener());
        setOnItemClickedListener(getDefaultItemClickedListener());
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), 300);
    }

    @Override
    public void onBackPressedCallback() {
        this.resetBackground();
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
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
