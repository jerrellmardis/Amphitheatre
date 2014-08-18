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

package com.jerrellmardis.amphitheatre.widget;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TvShowsCardPresenter extends CardPresenter {

    public TvShowsCardPresenter(Context context) {
        super(context);
        mCardHeight = 338;
        mCardWidth = 600;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.fastlane_background));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (item instanceof VideoGroup) {
            VideoGroup group = (VideoGroup) item;
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.mCardView.setTitleText(group.getVideo().getName());
            holder.mCardView.setMainImageDimensions(mCardWidth, mCardHeight);

            if (!group.getVideo().isMovie() && group.getNumOfVideos() > 0) {
                String contentText = mContext.getResources()
                        .getQuantityString(R.plurals.numberOfEpisodesAvailable, group.getNumOfVideos(),
                                group.getNumOfVideos());
                holder.mCardView.setContentText(contentText);
            }

            String url = group.getVideo().getBackgroundImageUrl();
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .resize(mCardWidth, mCardHeight)
                    .centerCrop()
                    .into(holder);
        } else {
            Video video = (Video) item;
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.mCardView.setTitleText(video.getName());
            holder.mCardView.setContentText("");
            holder.mCardView.setMainImageDimensions(mCardWidth, mCardHeight);

            String url = video.getBackgroundImageUrl();

            if (video.getTvShow() != null && video.getTvShow().getEpisode() != null) {
                holder.mCardView.setTitleText(video.getTvShow().getEpisode().getName());

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(video.getTvShow().getEpisode().getAirDate());
                    DateFormat df = DateFormat.getDateInstance();
                    String subtitle = String.format(mContext.getString(R.string.aired), df.format(date));
                    holder.mCardView.setContentText(subtitle);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                url = video.getTvShow().getEpisode().getStillPath();
            }

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .resize(mCardWidth, mCardHeight)
                    .centerCrop()
                    .into(holder);
        }
    }
}
