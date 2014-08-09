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
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Video;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    private Context mContext;

    public DetailsDescriptionPresenter(Context ctx) {
        super();
        mContext = ctx;
    }

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;

        if (video != null) {
            viewHolder.getTitle().setText(video.getName());
            viewHolder.getBody().setText(video.getOverview());

            if (video.getMovie() != null) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(video.getMovie().getReleaseDate());
                    DateFormat df = DateFormat.getDateInstance();
                    String subtitle = String.format(mContext.getString(R.string.released), df.format(date));
                    viewHolder.getSubtitle().setText(subtitle);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
