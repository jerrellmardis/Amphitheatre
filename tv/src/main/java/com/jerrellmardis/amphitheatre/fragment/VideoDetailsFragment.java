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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Movie;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.jerrellmardis.amphitheatre.util.VideoUtils;
import com.jerrellmardis.amphitheatre.widget.DetailsDescriptionPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;

public class VideoDetailsFragment extends DetailsFragment {

    private static final int ACTION_PLAY = 1;
    private static final float RATIO = 2 / 3f;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    private static final int DETAIL_THUMB_WIDTH = Math.round(DETAIL_THUMB_HEIGHT * RATIO);

    private static final String MOVIE = "Movie";

    private Movie selectedMovie;
    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.amphitheatre);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        selectedMovie = (Movie) getActivity().getIntent().getSerializableExtra(MOVIE);
        new DetailRowBuilderTask().execute(selectedMovie);

        setOnItemClickedListener(getDefaultItemClickedListener());
        updateBackground(selectedMovie.getBackgroundImageURI());
    }

    private class DetailRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {

        @Override
        protected DetailsOverviewRow doInBackground(Movie... movies) {
            selectedMovie = movies[0];

            DetailsOverviewRow row = new DetailsOverviewRow(selectedMovie);
            try {
                Bitmap poster = Picasso.with(getActivity())
                        .load(selectedMovie.getCardImageUrl())
                        .resize(dpToPx(DETAIL_THUMB_WIDTH, getActivity().getApplicationContext()),
                                dpToPx(DETAIL_THUMB_HEIGHT, getActivity().getApplicationContext()))
                        .centerCrop()
                        .get();
                row.setImageBitmap(getActivity(), poster);
            } catch (IOException e) {
                e.printStackTrace();
            }

            row.addAction(new Action(ACTION_PLAY, getString(R.string.play)));

            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow detailRow) {
            ClassPresenterSelector ps = new ClassPresenterSelector();
            DetailsOverviewRowPresenter dorPresenter =
                    new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter(getActivity()));

            dorPresenter.setBackgroundColor(getResources().getColor(R.color.fastlane_background));
            dorPresenter.setStyleLarge(true);
            dorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
                @Override
                public void onActionClicked(Action action) {
                    if (action.getId() == ACTION_PLAY) {
                        VideoUtils.playVideo(new WeakReference<Activity>(getActivity()), selectedMovie);
                    }
                    else {
                        Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ps.addClassPresenter(DetailsOverviewRow.class, dorPresenter);
            ps.addClassPresenter(ListRow.class, new ListRowPresenter());

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(ps);
            adapter.add(detailRow);

            setAdapter(adapter);
        }
    }

    protected OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof Movie) {
                    VideoUtils.playVideo(new WeakReference<Activity>(getActivity()), (Movie) item);
                }
            }
        };
    }

    protected void updateBackground(URI uri) {
        if (uri == null) return;

        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }

    public static int dpToPx(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}