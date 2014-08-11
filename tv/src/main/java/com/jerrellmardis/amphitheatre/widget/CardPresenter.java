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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.Video;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CardPresenter extends Presenter {

    protected final Context mContext;
    protected int mCardHeight = 400;
    protected int mCardWidth = Math.round(mCardHeight * (2 / 3f));

    public CardPresenter(Context context) {
        mContext = context;
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
        Video video = (Video) item;
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.mCardView.setTitleText(video.getName());
        holder.mCardView.setMainImageDimensions(mCardWidth, mCardHeight);

        if (video.getTvShow() != null && video.getTvShow().getVoteAverage() != null) {
            holder.mCardView.setContentText(String.format(
                    mContext.getString(R.string.rating_description),
                    video.getTvShow().getVoteAverage()));
        } else if (video.getMovie() != null && video.getMovie().getVoteAverage() != null) {
            holder.mCardView.setContentText(String.format(
                    mContext.getString(R.string.rating_description),
                    video.getMovie().getVoteAverage()));
        } else {
            holder.mCardView.setContentText(String.format(
                    mContext.getString(R.string.rating_description), 0.0d));
        }

        Picasso.with(mContext)
                .load(video.getCardImageUrl())
                .placeholder(R.drawable.placeholder)
                .resize(mCardWidth, mCardHeight)
                .centerCrop()
                .into(holder);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) { }

    static class ViewHolder extends Presenter.ViewHolder implements Target {

        protected final ImageCardView mCardView;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable bitmapDrawable = new BitmapDrawable(mCardView.getContext().getResources(), bitmap);
            mCardView.setMainImage(bitmapDrawable);
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
    }
}
