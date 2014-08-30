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

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.listeners.RowBuilderTaskListener;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.jerrellmardis.amphitheatre.task.DetailRowBuilderTask;
import com.jerrellmardis.amphitheatre.util.BlurTransform;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        boolean isVideo = getActivity().getIntent().getBooleanExtra(Constants.IS_VIDEO, true);
        Video video;

        if (isVideo) {
            video = (Video) getActivity().getIntent().getSerializableExtra(Constants.VIDEO);

            if (video.getTvShow() != null && video.getTvShow().getEpisode() != null) {
                updateBackground(video.getTvShow().getEpisode().getStillPath());
            } else {
                updateBackground(video.getBackgroundImageUrl());
            }

            Map<String, List<Video>> relatedVideos = Collections.emptyMap();

            if (video.isMovie()) {
                relatedVideos = getRelatedMovies(video);
            } else if (video.getTvShow() != null && video.getTvShow().getEpisode() != null) {
                relatedVideos = getRelatedTvShows(video);
            }

            new DetailRowBuilderTask(getActivity(), relatedVideos, true, this).execute(video);
        } else {
            VideoGroup videoGroup = (VideoGroup) getActivity().getIntent().getSerializableExtra(Constants.VIDEO_GROUP);
            video = videoGroup.getVideo();
            updateBackground(video.getBackgroundImageUrl());
            new DetailRowBuilderTask(getActivity(), getRelatedTvShows(video), false, this).execute(video);
        }

        setOnItemClickedListener(getDefaultItemClickedListener());
    }

    protected OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof Video) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.IS_VIDEO, true);
                    intent.putExtra(Constants.VIDEO, (Video) item);
                    startActivity(intent);
                }
            }
        };
    }

    private Map<String, List<Video>> getRelatedMovies(Video video) {
        List<Video> videos = Select
                .from(Video.class)
                .where(Condition.prop("is_matched").eq(1),
                        Condition.prop("is_movie").eq(1))
                .list();

        Map<String, List<Video>> relatedVideos = new HashMap<String, List<Video>>();
        String key = getString(R.string.related_videos);
        relatedVideos.put(key, new ArrayList<Video>());

        if (video.getMovie() != null && !TextUtils.isEmpty(video.getMovie().getFlattenedGenres())) {
            String[] genresArray = video.getMovie().getFlattenedGenres().split(",");
            Set<String> genres = new HashSet<String>(Arrays.asList(genresArray));

            for (Video vid : videos) {
                if (vid.getMovie() != null &&
                        !TextUtils.isEmpty(vid.getMovie().getFlattenedGenres())) {

                    Set<String> intersection = new HashSet<String>(Arrays.asList(
                            vid.getMovie().getFlattenedGenres().split(",")));
                    intersection.retainAll(genres);

                    if (intersection.size() == genresArray.length &&
                            !video.getMovie().getTitle().equals(vid.getMovie().getTitle())) {

                        relatedVideos.get(key).add(vid);
                    }
                }
            }
        }

        return relatedVideos;
    }

    private Map<String, List<Video>> getRelatedTvShows(Video video) {
        List<Video> videos = Select
                .from(Video.class)
                .where(Condition.prop("name").eq(video.getName()),
                        Condition.prop("is_movie").eq(0))
                .list();

        Map<String, List<Video>> relatedVideos = new TreeMap<String, List<Video>>(Collections.reverseOrder());

        for (Video vid : videos) {
            // if an Episode item exists then categorize it
            // otherwise, add it to the uncategorized list
            if (vid.getTvShow() != null && vid.getTvShow().getEpisode() != null) {
                int seasonNumber = vid.getTvShow().getEpisode().getSeasonNumber();
                String key = String.format(getString(R.string.season_number), seasonNumber);

                if (relatedVideos.containsKey(key)) {
                    List<Video> subVideos = relatedVideos.get(key);
                    subVideos.add(vid);
                } else {
                    List<Video> list = new ArrayList<Video>();
                    list.add(vid);
                    relatedVideos.put(key, list);
                }
            } else {
                String key = getString(R.string.uncategorized);

                if (relatedVideos.containsKey(key)) {
                    relatedVideos.get(key).add(vid);
                } else {
                    List<Video> list = new ArrayList<Video>();
                    list.add(vid);
                    relatedVideos.put(key, list);
                }
            }
        }

        return relatedVideos;
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