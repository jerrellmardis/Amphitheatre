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
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.util.DisplayMetrics;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.listeners.RowBuilderTaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.task.DetailRowBuilderTask;
import com.jerrellmardis.amphitheatre.util.BlurTransform;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.jerrellmardis.amphitheatre.util.VideoUtils;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VideoDetailsFragment extends DetailsFragment implements RowBuilderTaskListener {

    private Transformation mBlurTransformation;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBlurTransformation = new BlurTransform(getActivity());

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        Video video = (Video) getActivity().getIntent().getSerializableExtra(Constants.VIDEO);

        if (video.isMovie()) {
            new DetailRowBuilderTask(getActivity(), this).execute(video);
        } else {
            List<Video> videos = Select
                    .from(Video.class)
                    .where(Condition.prop("name").eq(video.getName()),
                            Condition.prop("is_movie").eq(0))
                    .list();

            Map<String, List<Video>> groups = new TreeMap<String, List<Video>>(Collections.reverseOrder());

            for (Video vid : videos) {
                // if an Episode item exists then categorize it
                // otherwise, add it to the uncategorized list
                if (vid.getTvShow() != null && vid.getTvShow().getEpisode() != null) {
                    int seasonNumber = vid.getTvShow().getEpisode().getSeasonNumber();
                    String key = String.format(getString(R.string.season_number), seasonNumber);

                    if (groups.containsKey(key)) {
                        List<Video> subVideos = groups.get(key);
                        subVideos.add(vid);
                    } else {
                        List<Video> list = new ArrayList<Video>();
                        list.add(vid);
                        groups.put(key, list);
                    }
                } else {
                    String key = getString(R.string.uncategorized);

                    if (groups.containsKey(key)) {
                        groups.get(key).add(vid);
                    } else {
                        List<Video> list = new ArrayList<Video>();
                        list.add(vid);
                        groups.put(key, list);
                    }
                }
            }

            new DetailRowBuilderTask(getActivity(), groups, this).execute(video);
        }

        setOnItemClickedListener(getDefaultItemClickedListener());
        updateBackground(video.getBackgroundImageUrl());
    }

    protected OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof Video) {
                    VideoUtils.playVideo(new WeakReference<Activity>(getActivity()), (Video) item);
                }
            }
        };
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

    @Override
    public void taskCompleted(ArrayObjectAdapter adapter) {
        setAdapter(adapter);
    }
}
