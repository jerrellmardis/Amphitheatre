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
import android.support.v17.leanback.widget.Presenter;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Video;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SeasonCardPresenter extends CardPresenter {

    public SeasonCardPresenter(Context context) {
        super(context);
        mCardHeight = 338;
        mCardWidth = 600;
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Video video = (Video) item;
        ViewHolder holder = (ViewHolder) viewHolder;

        String url;
        if (video.getTvShow() != null && video.getTvShow().getEpisode() != null) {
            holder.mCardView.setTitleText(video.getTvShow().getEpisode().getName());
            url = video.getTvShow().getEpisode().getStillPath();

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(video.getTvShow().getEpisode().getAirDate());
                DateFormat df = DateFormat.getDateInstance();
                String subtitle = String.format(mContext.getString(R.string.aired), df.format(date));
                holder.mCardView.setContentText(subtitle);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.mCardView.setTitleText(video.getName());
            url = video.getBackgroundImageUrl();
        }

        holder.mCardView.setMainImageDimensions(mCardWidth, mCardHeight);

        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .resize(mCardWidth, mCardHeight)
                .centerCrop()
                .into(holder);
    }
}
