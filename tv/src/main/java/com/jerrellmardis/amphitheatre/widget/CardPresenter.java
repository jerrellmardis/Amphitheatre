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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
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
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.Enums;
import com.jerrellmardis.amphitheatre.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CardPresenter extends Presenter {

    protected final Context mContext;
    protected int mCardHeight = 400;
    protected int mCardWidth = Math.round(mCardHeight * (2 / 3f));
    protected final SharedPreferences mSharedPrefs;

    public CardPresenter(Context context) {
        mContext = context;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        final ImageCardView cardView = new ImageCardView(mContext);

        cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean isFocused) {
                setFocusState(cardView, isFocused, view);
            }
        });

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.fastlane_background));
        return new ViewHolder(cardView);
    }

    private void setFocusState(final ImageCardView cardView, final boolean isFocused, View view) {
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

                        ((TextView)cardView.findViewById(R.id.title_text)).setTextColor(
                                Utils.getPaletteColor(
                                        palette,
                                        mSharedPrefs.getString(Constants.PALETTE_TITLE_SELECTED, ""),
                                        mContext.getResources().getColor(R.color.lb_basic_card_title_text_color))
                        );

                        ((TextView)cardView.findViewById(R.id.content_text)).setTextColor(
                                Utils.getPaletteColor(
                                        palette,
                                        mSharedPrefs.getString(Constants.PALETTE_CONTENT_SELECTED, ""),
                                        mContext.getResources().getColor(R.color.lb_basic_card_content_text_color))
                        );
                    }
                    else {
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

                        ((TextView)cardView.findViewById(R.id.title_text)).setTextColor(
                                Utils.getPaletteColor(
                                        palette,
                                        mSharedPrefs.getString(Constants.PALETTE_TITLE_UNSELECTED, ""),
                                        mContext.getResources().getColor(R.color.lb_basic_card_title_text_color))
                        );

                        ((TextView)cardView.findViewById(R.id.content_text)).setTextColor(
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

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Video video = (Video) item;
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.mCardView.setTitleText(video.getName());
        holder.mCardView.setMainImageDimensions(mCardWidth, mCardHeight);

        if (video.getTvShow() != null && video.getTvShow().getEpisode() != null) {
            holder.mCardView.setContentText(String.format(
                    mContext.getString(R.string.tv_show_card_description),
                    video.getTvShow().getEpisode().getSeasonNumber(),
                    video.getTvShow().getEpisode().getEpisodeNumber()));
        } else if (video.getMovie() != null && video.getMovie().getVoteAverage() != null) {
            holder.mCardView.setContentText(String.format(
                    mContext.getString(R.string.rating_description),
                    video.getMovie().getVoteAverage()));
        } else if (video.getMovie() != null) {
            holder.mCardView.setContentText(String.format(
                    mContext.getString(R.string.rating_description), 0.0d));
        }

        Picasso.with(mContext)
                .load(video.getCardImageUrl())
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

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) { }

    static class ViewHolder extends Presenter.ViewHolder implements Target {

        protected final ImageCardView mCardView;
        private final SharedPreferences mSharedPrefs;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mCardView.getContext());

            checkPrefs();
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable bitmapDrawable = new BitmapDrawable(mCardView.getContext().getResources(), bitmap);
            mCardView.setMainImage(bitmapDrawable);

            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_VISIBLE, "")) == Enums.PalettePresenterType.ALLCARDS) {
                        Utils.animateColorChange(
                                mCardView.findViewById(R.id.info_field),
                                mCardView.getContext().getResources().getColor(R.color.lb_basic_card_info_bg_color),
                                Utils.getPaletteColor(
                                        palette,
                                        mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_UNSELECTED, ""),
                                        mCardView.getContext().getResources().getColor(R.color.lb_basic_card_info_bg_color))
                        );
                    }
                    if (Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_TITLE_VISIBLE, "")) == Enums.PalettePresenterType.ALLCARDS) {
                        ((TextView)mCardView.findViewById(R.id.title_text)).setTextColor(
                                Utils.getPaletteColor(
                                        palette,
                                        mSharedPrefs.getString(Constants.PALETTE_TITLE_UNSELECTED, ""),
                                        mCardView.getContext().getResources().getColor(R.color.lb_basic_card_title_text_color))
                        );
                    }
                    if (Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_CONTENT_VISIBLE, "")) == Enums.PalettePresenterType.ALLCARDS) {
                        ((TextView)mCardView.findViewById(R.id.content_text)).setTextColor(
                                Utils.getPaletteColor(
                                        palette,
                                        mSharedPrefs.getString(Constants.PALETTE_CONTENT_UNSELECTED, ""),
                                        mCardView.getContext().getResources().getColor(R.color.lb_basic_card_content_text_color))
                        );
                    }
                }
            });
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mCardView.setMainImage(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mCardView.setMainImage(placeHolderDrawable);
        }

        private void checkPrefs() {
            SharedPreferences.Editor editor = mSharedPrefs.edit();

            if (!mSharedPrefs.contains(Constants.PALETTE_BACKGROUND_VISIBLE)) {
                editor.putString(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_BACKGROUND_UNSELECTED)) {
                editor.putString(Constants.PALETTE_BACKGROUND_UNSELECTED, "");
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_BACKGROUND_SELECTED)) {
                editor.putString(Constants.PALETTE_BACKGROUND_SELECTED, Enums.PaletteColor.DARKMUTED.name());
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_TITLE_VISIBLE)) {
                editor.putString(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_TITLE_UNSELECTED)) {
                editor.putString(Constants.PALETTE_TITLE_UNSELECTED, "");
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_TITLE_SELECTED)) {
                editor.putString(Constants.PALETTE_TITLE_UNSELECTED, "");
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_CONTENT_VISIBLE)) {
                editor.putString(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_CONTENT_UNSELECTED)) {
                editor.putString(Constants.PALETTE_CONTENT_UNSELECTED, "");
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_CONTENT_SELECTED)) {
                editor.putString(Constants.PALETTE_CONTENT_UNSELECTED, "");
            }

            editor.apply();
        }
    }
}
