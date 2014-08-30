/*
 * Copyright (C) 2014 Jerrell Mardis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jerrellmardis.amphitheatre.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.model.GridGenre;

public class GridItemPresenter extends Presenter {

    private Context mContext;

    public GridItemPresenter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(240, 240));
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor(mContext.getResources().getColor(R.color.primary));
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        if (item instanceof GridGenre) {
            ((TextView) viewHolder.view).setText(((GridGenre) item).getTitle());
        } else {
            ((TextView) viewHolder.view).setText((String) item);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) { }
}