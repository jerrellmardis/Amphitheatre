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

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.dialog.SearchNetworkDialog;
import com.jerrellmardis.amphitheatre.dialog.SearchNetworkDialog.Callbacks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddSourceDialogFragment extends DialogFragment implements Callbacks {

    private OnClickListener mOnClickListener;

    @InjectView(R.id.user) EditText mUserText;
    @InjectView(R.id.password) EditText mPasswordText;
    @InjectView(R.id.path) EditText mPathText;
    @InjectView(R.id.radio_movie) RadioButton mMovieRadioButton;

    private SearchNetworkDialog mSearchNetworkDialog;

    public static AddSourceDialogFragment newInstance(OnClickListener l) {
        AddSourceDialogFragment f = new AddSourceDialogFragment();
        f.setOnClickListener(l);
        return f;
    }

    public interface OnClickListener {
        void onAddClicked(CharSequence user, CharSequence password, CharSequence path, boolean isMovie);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_source_dialog, container);
        ButterKnife.inject(this, view);
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
        if (TextUtils.isEmpty(mPathText.getText())) {
            mPathText.setError(getString(R.string.source_path_required_msg));
            return;
        } else {
            mPathText.setError(null);
        }

        if (mOnClickListener != null) {
            mOnClickListener.onAddClicked(mUserText.getText(), mPasswordText.getText(),
                    mPathText.getText(), mMovieRadioButton.isChecked());
        }
        getDialog().dismiss();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.search)
    public void searchButtonClick() {
        mSearchNetworkDialog = new SearchNetworkDialog(getActivity());
        mSearchNetworkDialog.setCallbacks(this);
        mSearchNetworkDialog.show();
    }

    @Override public void onDeviceSelected(String address) {
        mPathText.setText(address);
        mUserText.requestFocus();
        if (mSearchNetworkDialog != null) {
            mSearchNetworkDialog.dismiss();
            mSearchNetworkDialog = null;
        }
    }
}