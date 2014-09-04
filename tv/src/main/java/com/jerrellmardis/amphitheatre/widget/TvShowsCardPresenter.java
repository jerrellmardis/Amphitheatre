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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.Utils;
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
        final ImageCardView cardView = new ImageCardView(mContext);

        cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean isFocused) {
                final Drawable mainImage = ((ImageView)view.findViewById(R.id.main_image)).getDrawable();

                if (isFocused) {
                    ((TextView)cardView.findViewById(R.id.title_text)).setMaxLines(4);
                }
                else {
                    ((TextView)cardView.findViewById(R.id.title_text)).setMaxLines(1);
                }

                if (mainImage != null) {
                    Palette.generateAsync(((BitmapDrawable) mainImage).getBitmap(), new Palette.PaletteAsyncListener() {

                        @Override
                        public void onGenerated(Palette palette) {

                            if (isFocused) {
                                Utils.animateColorChange(
                                        cardView.findViewById(R.id.info_field),
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_UNSELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color)),
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_SELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color))
                                );

                                ((TextView) cardView.findViewById(R.id.title_text)).setTextColor(
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_TITLE_SELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_title_text_color))
                                );

                                ((TextView) cardView.findViewById(R.id.content_text)).setTextColor(
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_CONTENT_SELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_content_text_color))
                                );
                            } else {
                                Utils.animateColorChange(
                                        cardView.findViewById(R.id.info_field),
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_SELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color)),
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_UNSELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color))
                                );

                                ((TextView) cardView.findViewById(R.id.title_text)).setTextColor(
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_TITLE_UNSELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_title_text_color))
                                );

                                ((TextView) cardView.findViewById(R.id.content_text)).setTextColor(
                                        Utils.getPaletteColor(
                                                palette,
                                                mSharedPrefs.getString(Constants.PALETTE_CONTENT_UNSELECTED, ""),
                                                mContext.getResources().getColor(R.color.lb_basic_card_content_text_color))
                                );
                            }
                        }
                    });
                }
            }
        });

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

            //rebuild the layout for the info area so it expands when we set the maxlines higher
            FrameLayout.LayoutParams infoLayout = new FrameLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            holder.mCardView.findViewById(R.id.info_field).setLayoutParams(infoLayout);
            RelativeLayout.LayoutParams contentLayout = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            contentLayout.addRule(RelativeLayout.BELOW, R.id.title_text);
            holder.mCardView.findViewById(R.id.content_text).setLayoutParams(contentLayout);

            // set color to standard for when the user scrolls and the view is reused
            Utils.animateColorChange(
                    holder.mCardView.findViewById(R.id.info_field),
                    holder.mCardView.findViewById(R.id.info_field).getDrawingCacheBackgroundColor(),
                    mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color)
            );
        }
    }
}
