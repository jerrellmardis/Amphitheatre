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

package com.jerrellmardis.amphitheatre.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.jerrellmardis.amphitheatre.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddSourceDialogFragment extends DialogFragment {

    private static final String TITLE = "title";

    private OnClickListener mOnClickListener;

    @InjectView(R.id.title) TextView mTitle;
    @InjectView(R.id.user) TextView mUser;
    @InjectView(R.id.password) TextView mPassword;
    @InjectView(R.id.path) TextView mPath;

    public static AddSourceDialogFragment newInstance(String title, OnClickListener l) {
        AddSourceDialogFragment f = new AddSourceDialogFragment();

        f.setOnClickListener(l);

        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        f.setArguments(bundle);

        return f;
    }

    public interface OnClickListener {
        void onAddClicked(CharSequence user, CharSequence password, CharSequence path);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_source_dialog, container);
        ButterKnife.inject(this, view);
        mTitle.setText(getArguments().getString(TITLE));
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cancel_button)
    public void cancelButtonOnclick() {
        getDialog().dismiss();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.add_button)
    public void addButtonOnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onAddClicked(mUser.getText(), mPassword.getText(), mPath.getText());
        }
        getDialog().dismiss();
    }
}