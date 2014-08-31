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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
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
                final View infoPanel = view.findViewById(R.id.info_field);
                final TextView subtitleField = (TextView)view.findViewById(R.id.content_text);
                final TextView titleField = (TextView)view.findViewById(R.id.title_text);
                final Drawable mainImage = ((ImageView)view.findViewById(R.id.main_image)).getDrawable();

                final Enums.PalettePresenterType palettePresenterType = Enums.PalettePresenterType.valueOf(
                        mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, ""));

                if (isFocused) {
                    ((TextView)cardView.findViewById(R.id.title_text)).setMaxLines(4);
                    FrameLayout.LayoutParams infoLayout = new FrameLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    infoPanel.setLayoutParams(infoLayout);
                    RelativeLayout.LayoutParams contentLayout = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    contentLayout.addRule(RelativeLayout.BELOW, R.id.title_text);
                    subtitleField.setLayoutParams(contentLayout);
                }
                else {
                    ((TextView)cardView.findViewById(R.id.title_text)).setMaxLines(1);
                }

                if (mainImage != null) {
                    Palette.generateAsync(((BitmapDrawable) mainImage).getBitmap(), new Palette.PaletteAsyncListener() {

                        @Override
                        public void onGenerated(Palette palette) {
                        Utils.setTextColor(
                                titleField,
                                subtitleField,
                                Enums.PaletteTextType.valueOf(mSharedPrefs.getString(Constants.PALETTE_TEXT_VISIBILITY, "")),
                                palette,
                                isFocused,
                                mContext
                        );
                        Utils.setInfoPanelColor(
                                infoPanel,
                                Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, "")),
                                palette,
                                isFocused,
                                mContext
                        );
                        }

//                            switch (palettePresenterType) {
//                                case ALL:
//                                    if (isFocused) {
//                                        if (paletteLightItem != null) {

//                                            if (paletteItem != null) {
//                                                contentField.setTextColor(paletteItem.getRgb());
//                                            }
//                                            if (paletteDarkItem != null) {
//                                                titleField.setTextColor(paletteDarkItem.getRgb());
//                                            }
//                                        }
//                                    } else {
//                                        if (paletteDarkItem != null) {

//                                            if (paletteItem != null) {
//                                                contentField.setTextColor(paletteItem.getRgb());
//                                            }
//                                            if (paletteLightItem != null) {
//                                                titleField.setTextColor(paletteLightItem.getRgb());
//                                            }
//                                        }
//                                    }
//                                    break;
//                                case FOCUSED:
//                                    if (isFocused) {
//                                        if (paletteDarkItem != null) {

//                                            if (paletteItem != null) {
//                                                contentField.setTextColor(paletteItem.getRgb());
//                                            }
//                                            if (paletteLightItem != null) {
//                                                titleField.setTextColor(paletteLightItem.getRgb());
//                                            }
//                                        }
//                                    } else {
//                                        if (paletteDarkItem != null) {

//                                            contentField.setTextColor(Color.argb(255, 255, 255, 255));
//                                            titleField.setTextColor(Color.argb(255, 255, 255, 255));
//                                        }
//                                    }
//                                    break;
//                            }
//                        }
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

            CheckPrefs();
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Drawable bitmapDrawable = new BitmapDrawable(mCardView.getContext().getResources(), bitmap);
            mCardView.setMainImage(bitmapDrawable);

            if (Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, "")) == Enums.PalettePresenterType.ALL) {
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Utils.setTextColor(
                                (TextView)mCardView.findViewById(R.id.title_text),
                                (TextView)mCardView.findViewById(R.id.content_text),
                                Enums.PaletteTextType.valueOf(mSharedPrefs.getString(Constants.PALETTE_TEXT_VISIBILITY, "")),
                                palette,
                                false,
                                mCardView.getContext()
                        );

                        Utils.setInfoPanelColor(
                                mCardView.findViewById(R.id.info_field),
                                Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, "")),
                                palette,
                                false,
                                mCardView.getContext()
                        );
                    }
                });
            }
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
            if (!mSharedPrefs.contains(Constants.PALETTE_VISIBILITY)) {
                mSharedPrefs.edit().putString(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.ALL.name()).apply();
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_VIBRANCY)) {
                mSharedPrefs.edit().putString(Constants.PALETTE_VIBRANCY, Enums.PaletteType.MUTED.name()).apply();
            }
            if (!mSharedPrefs.contains(Constants.PALETTE_TEXT_VISIBILITY)) {
                mSharedPrefs.edit().putString(Constants.PALETTE_TEXT_VISIBILITY, Enums.PaletteTextType.NONE.name()).apply();
            }
        }
    }
}
