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

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.listeners.RowBuilderTaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.util.Utils;
import com.jerrellmardis.amphitheatre.util.VideoUtils;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;
import com.jerrellmardis.amphitheatre.widget.DetailsDescriptionPresenter;
import com.jerrellmardis.amphitheatre.widget.SeasonCardPresenter;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Jerrell Mardis on 8/8/14.
 */
public class DetailRowBuilderTask extends AsyncTask<Video, Integer, DetailsOverviewRow> {

    private static final int ACTION_PLAY = 1;
    private static final int ACTION_VIEW_TRAILER = 2;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    private static final int DETAIL_THUMB_WIDTH = Math.round(DETAIL_THUMB_HEIGHT * (2 / 3f));

    private Video mVideo;
    private Map<String, List<Video>> mVideoGroups;
    private boolean mShowPlayButton;
    private Activity mActivity;
    private RowBuilderTaskListener mRowBuilderTaskListener;
    
    public DetailRowBuilderTask(Activity context, Map<String, List<Video>> videoGroups,
                                boolean showPlayButton, RowBuilderTaskListener l) {

        mActivity = context;
        mShowPlayButton = showPlayButton;
        mRowBuilderTaskListener = l;
        mVideoGroups = videoGroups;
    }
    
    @Override
    protected DetailsOverviewRow doInBackground(Video... videos) {
        mVideo = videos[0];

        DetailsOverviewRow row = new DetailsOverviewRow(mVideo);
        try {
            Bitmap poster = Picasso.with(mActivity)
                    .load(mVideo.getCardImageUrl())
                    .resize(Utils.dpToPx(DETAIL_THUMB_WIDTH, mActivity.getApplicationContext()),
                            Utils.dpToPx(DETAIL_THUMB_HEIGHT, mActivity.getApplicationContext()))
                    .centerCrop()
                    .get();
            row.setImageBitmap(mActivity, poster);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mShowPlayButton) {
            row.addAction(new Action(ACTION_PLAY, mActivity.getString(R.string.play)));
        }

        if (mVideo.getMovie() != null && !TextUtils.isEmpty(mVideo.getMovie().getTrailer())) {
            row.addAction(
                    new Action(ACTION_VIEW_TRAILER, mActivity.getString(R.string.watch_trailer)));
        }

        return row;
    }

    @Override
    protected void onPostExecute(DetailsOverviewRow detailRow) {
        ClassPresenterSelector ps = new ClassPresenterSelector();
        DetailsOverviewRowPresenter dorPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter(mActivity));

        dorPresenter.setBackgroundColor(mActivity.getResources().getColor(R.color.fastlane_background));
        dorPresenter.setStyleLarge(true);
        dorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_PLAY) {
                    VideoUtils.playVideo(new WeakReference<Activity>(mActivity), mVideo);
                }
                else if (action.getId() == ACTION_VIEW_TRAILER) {
                    Uri trailerUri = Uri.parse(mVideo.getMovie().getTrailer());
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, trailerUri));
                }
                else {
                    Toast.makeText(mActivity, action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ps.addClassPresenter(DetailsOverviewRow.class, dorPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(ps);
        adapter.add(detailRow);

        addGroups(adapter);

        if (mRowBuilderTaskListener != null) {
            mRowBuilderTaskListener.taskCompleted(adapter);
        }
    }

    private void addGroups(ArrayObjectAdapter adapter) {
        if (mVideoGroups != null && !mVideoGroups.isEmpty()) {
            for (Map.Entry<String, List<Video>> entry : mVideoGroups.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    CardPresenter presenter;
                    ArrayObjectAdapter listRowAdapter;

                    if (mVideo.isMovie()) {
                        presenter = new CardPresenter(mActivity);
                        sortMovies(entry.getValue());
                    } else {
                        presenter = new SeasonCardPresenter(mActivity);
                        sortTvShows(entry.getValue());
                    }

                    listRowAdapter = new ArrayObjectAdapter(presenter);

                    int max = 15;
                    int end = entry.getValue().size() > max ? max : entry.getValue().size();
                    List<Video> subList = entry.getValue().subList(0, end);

                    listRowAdapter.addAll(0, subList);
                    HeaderItem header = new HeaderItem(0, entry.getKey(), null);
                    adapter.add(new ListRow(header, listRowAdapter));
                }
            }
        }
    }

    private void sortMovies(List<Video> videos) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // sort by date
        Collections.sort(videos, new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                if (o1.getMovie().getReleaseDate() == null) {
                    return (o2.getMovie().getReleaseDate() == null) ? 0 : -1;
                }
                if (o2.getMovie().getReleaseDate() == null) {
                    return 1;
                }

                try {
                    return sdf.parse(o2.getMovie().getReleaseDate())
                            .compareTo(sdf.parse(o1.getMovie().getReleaseDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });
    }

    private void sortTvShows(List<Video> videos) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // sort by date
        Collections.sort(videos, new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                if (o2.getTvShow().getEpisode().getAirDate() == null) {
                    return (o1.getTvShow().getEpisode().getAirDate() == null) ? 0 : -1;
                }
                if (o1.getTvShow().getEpisode().getAirDate() == null) {
                    return 1;
                }

                try {
                    return sdf.parse(o1.getTvShow().getEpisode().getAirDate())
                            .compareTo(sdf.parse(o2.getTvShow().getEpisode().getAirDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });
    }
}