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

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.Enums;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Method;

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
        ImageCardView cardView = new ImageCardView(mContext);

        cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean isFocused) {
                final View infoField = view.findViewById(R.id.info_field);
                final Drawable mainImage = ((ImageView)view.findViewById(R.id.main_image)).getDrawable();
                final Enums.PalettePresenterType palettePresenterType = Enums.PalettePresenterType.valueOf(
                        mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, ""));

                if (mainImage != null) {
                    Palette.generateAsync(((BitmapDrawable) mainImage).getBitmap(), new Palette.PaletteAsyncListener() {

                        @Override
                        public void onGenerated(Palette palette) {
                            PaletteItem paletteDarkItem = null;
                            PaletteItem paletteLightItem = null;
                            PaletteItem paletteItem = null;

                            switch (Enums.PaletteType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VIBRANCY, ""))) {
                                case VIBRANT:
                                    paletteDarkItem = palette.getDarkVibrantColor();
                                    paletteLightItem = palette.getLightVibrantColor();
                                    paletteItem = palette.getVibrantColor();
                                    break;
                                case MUTED:
                                    paletteDarkItem = palette.getDarkMutedColor();
                                    paletteLightItem = palette.getLightMutedColor();
                                    paletteItem = palette.getMutedColor();
                                    break;
                            }


                            switch (palettePresenterType) {
                                case ALL:
                                    if (isFocused) {
                                        if (paletteItem != null) {
                                            animateColorChange(
                                                    infoField,
                                                    (paletteDarkItem != null) ?
                                                            paletteDarkItem.getRgb() :
                                                            mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color),
                                                    paletteItem.getRgb()
                                            );
                                        }
                                    } else {
                                        if (paletteDarkItem != null) {
                                            animateColorChange(
                                                    infoField,
                                                    (paletteItem != null) ?
                                                            paletteItem.getRgb() :
                                                            mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color),
                                                    paletteDarkItem.getRgb()
                                            );
                                        }
                                    }
                                    break;
                                case FOCUSED:
                                    if (isFocused) {
                                        if (paletteDarkItem != null) {
                                            animateColorChange(
                                                    infoField,
                                                    mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color),
                                                    paletteItem.getRgb()
                                            );
                                        }
                                    } else {
                                        if (paletteDarkItem != null) {
                                            animateColorChange(
                                                    infoField,
                                                    paletteDarkItem.getRgb(),
                                                    mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color)
                                            );
                                        }
                                    }
                                    break;
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

    protected static void animateColorChange(final View view, int colorFrom, int colorTo) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer)animator.getAnimatedValue());
            }
        });
        valueAnimator.start();
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

        // set color to standard for when the user scrolls and the view is reused
        animateColorChange(
                holder.mCardView.findViewById(R.id.info_field),
                holder.mCardView.findViewById(R.id.info_field).getDrawingCacheBackgroundColor(),
                mContext.getResources().getColor(R.color.lb_basic_card_info_bg_color)
        );
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) { }

    static class ViewHolder extends Presenter.ViewHolder implements Target {

        protected final ImageCardView mCardView;
        private final SharedPreferences mSharedPreferences;


        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mCardView.getContext());

            CheckPrefs();
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Drawable bitmapDrawable = new BitmapDrawable(mCardView.getContext().getResources(), bitmap);
            mCardView.setMainImage(bitmapDrawable);

            if (Enums.PalettePresenterType.valueOf(mSharedPreferences.getString(Constants.PALETTE_VISIBILITY, "")) == Enums.PalettePresenterType.ALL) {
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        PaletteItem paletteItem = null;

                        switch (Enums.PaletteType.valueOf(mSharedPreferences.getString(Constants.PALETTE_VIBRANCY, ""))) {
                            case MUTED:
                                paletteItem = palette.getDarkMutedColor();
                                break;
                            case VIBRANT:
                                paletteItem = palette.getDarkVibrantColor();
                                break;
                        }

                        if (paletteItem != null) {
                            animateColorChange(
                                    mCardView.findViewById(R.id.info_field),
                                    mCardView.getContext().getResources().getColor(R.color.lb_basic_card_info_bg_color),
                                    paletteItem.getRgb()
                            );
                        }
                    }
                });
            }
            // TODO cross-fade from placeholder. Picasso should provide a way to do this.
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mCardView.setMainImage(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mCardView.setMainImage(placeHolderDrawable);
        }

        private void CheckPrefs() {
            if (!mSharedPreferences.contains(Constants.PALETTE_VISIBILITY)) {
                mSharedPreferences.edit().putString(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.ALL.name()).apply();
            }
            if (!mSharedPreferences.contains(Constants.PALETTE_VIBRANCY)) {
                mSharedPreferences.edit().putString(Constants.PALETTE_VIBRANCY, Enums.PaletteType.MUTED.name()).apply();
            }
        }
    }
}
