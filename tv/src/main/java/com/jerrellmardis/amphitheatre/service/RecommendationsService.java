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

package com.jerrellmardis.amphitheatre.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.RecommendationBuilder;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RecommendationsService extends IntentService {

    private static final String TAG = "RecommendationsService";

    private static final int MAX_TV_SHOWS_RECOMMENDATIONS = 2;
    private static final int MAX_MOVIE_RECOMMENDATIONS = 1;

    public RecommendationsService() {
        super("RecommendationsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RecommendationBuilder builder = new RecommendationBuilder()
                .setContext(getApplicationContext())
                .setSmallIcon(R.drawable.ic_tv_small);

        recommendMovies(builder);
        recommendTvShows(builder);
    }

    private void recommendMovies(RecommendationBuilder builder) {
        List<Video> videos = Select
                .from(Video.class)
                .where(Condition.prop("is_matched").eq(1),
                        Condition.prop("is_movie").eq(1))
                .list();

        // filter out movies without a release date
        for (Iterator<Video> i = videos.iterator(); i.hasNext();) {
            Video video = i.next();
            if (video == null || video.getMovie() == null ||
                    TextUtils.isEmpty(video.getMovie().getReleaseDate())) {

                i.remove();
            }
        }

        Collections.sort(videos, new Comparator<Video>() {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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

        try {
            int numOfRecommendedVideos = 0;

            for (Video video : videos) {
                if (video.getMovie() != null && !TextUtils.isEmpty(video.getCardImageUrl()) &&
                        !TextUtils.isEmpty(video.getBackgroundImageUrl())) {

                    builder.setBackground(video.getBackgroundImageUrl())
                            .setId(MAX_TV_SHOWS_RECOMMENDATIONS + numOfRecommendedVideos)
                            .setPriority(MAX_TV_SHOWS_RECOMMENDATIONS + numOfRecommendedVideos)
                            .setTitle(video.getName())
                            .setDescription(getString(R.string.recently_released))
                            .setImage(video.getCardImageUrl())
                            .setIntent(buildPendingIntent(video))
                            .build();

                    if (++numOfRecommendedVideos >= MAX_MOVIE_RECOMMENDATIONS) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to update recommendation", e);
        }
    }

    private void recommendTvShows(RecommendationBuilder builder) {
        List<Video> videos = Select
                .from(Video.class)
                .where(Condition.prop("is_matched").eq(1),
                        Condition.prop("is_movie").eq(0))
                .list();

        // filter out TV shows without an air,m  date
        for (Iterator<Video> i = videos.iterator(); i.hasNext();) {
            Video video = i.next();
            if (video == null || video.getTvShow() == null || video.getTvShow().getEpisode() == null ||
                    TextUtils.isEmpty(video.getTvShow().getEpisode().getAirDate())) {

                i.remove();
            }
        }

        Collections.sort(videos, new Comparator<Video>() {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public int compare(Video o1, Video o2) {
                if (o1.getTvShow().getEpisode().getAirDate() == null) {
                    return (o2.getTvShow().getEpisode().getAirDate() == null) ? 0 : -1;
                }
                if (o2.getTvShow().getEpisode().getAirDate() == null) {
                    return 1;
                }

                try {
                    return sdf.parse(o2.getTvShow().getEpisode().getAirDate())
                            .compareTo(sdf.parse(o1.getTvShow().getEpisode().getAirDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });

        try {
            int numOfRecommendedVideos = 0;

            Set<String> recommendedTvShows = new HashSet<String>();

            for (Video video : videos) {
                if (video.getTvShow() != null && !TextUtils.isEmpty(video.getCardImageUrl()) &&
                        !TextUtils.isEmpty(video.getBackgroundImageUrl()) &&
                        !recommendedTvShows.contains(video.getName())) {

                    // add only distinct TV shows since the user will be taken to the Detail
                    // Activity which lists episodes and seasons
                    recommendedTvShows.add(video.getName());

                    builder.setBackground(video.getBackgroundImageUrl())
                            .setId(numOfRecommendedVideos)
                            .setPriority(numOfRecommendedVideos)
                            .setTitle(video.getName())
                            .setDescription(getString(R.string.recently_aired))
                            .setImage(video.getCardImageUrl())
                            .setIntent(buildPendingIntent(video))
                            .build();

                    if (++numOfRecommendedVideos >= MAX_TV_SHOWS_RECOMMENDATIONS) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to update recommendation", e);
        }
    }

    private PendingIntent buildPendingIntent(Video video) {
        Intent detailsIntent = new Intent(this, DetailsActivity.class);
        detailsIntent.putExtra(Constants.VIDEO, video);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DetailsActivity.class);
        stackBuilder.addNextIntent(detailsIntent);
        detailsIntent.setAction(Long.toString(video.getId()));

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}