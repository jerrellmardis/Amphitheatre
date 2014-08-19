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

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.task.NetworkSearchTask;
import com.jerrellmardis.amphitheatre.task.NetworkSearchTask.OnSharesFoundListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class AddSourceDialogFragment extends DialogFragment implements OnSharesFoundListener {

    private OnClickListener mOnClickListener;
    private NetworkSearchTask mSearchTask;
    private ArrayAdapter<String> mShareAdapter;

    @InjectView(R.id.share_spinner) Spinner mShareSpinner;
    @InjectView(R.id.user) EditText mUserText;
    @InjectView(R.id.password) EditText mPasswordText;
    @InjectView(R.id.path) EditText mPathText;
    @InjectView(R.id.radio_movie) RadioButton mMovieRadioButton;

    public static AddSourceDialogFragment newInstance() {
        return new AddSourceDialogFragment();
    }

    public interface OnClickListener {
        void onAddClicked(CharSequence user, CharSequence password, CharSequence path, boolean isMovie);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnClickListener) {
            mOnClickListener = (OnClickListener) activity;
        } else if (getTargetFragment() instanceof OnClickListener) {
            mOnClickListener = (OnClickListener) getTargetFragment();
        } else {
            throw new ClassCastException("Caller must implement OnClickListener interface.");
        }
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

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShareAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item);
        mShareAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShareAdapter.add(getString(R.string.manually_enter_path));
        mShareSpinner.setAdapter(mShareAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        searchShares();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }
    }

    @Override
    public void onSharesFound(List<String> shares) {
        if (!isResumed()) {
            return;
        }

        mShareAdapter.clear();

        if (shares != null) {
            mShareAdapter.addAll(shares);
        }

        mShareAdapter.add(getString(R.string.manually_enter_path));
        onShareSelected();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cancel_button)
    public void cancelButtonOnclick() {
        getDialog().dismiss();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.add_button)
    public void addButtonOnClick() {
        if (isManualPathSelected() && TextUtils.isEmpty(mPathText.getText())) {
            mPathText.setError(getString(R.string.source_path_required_msg));
            return;
        } else {
            mPathText.setError(null);
        }

        if (mOnClickListener != null) {
            mOnClickListener.onAddClicked(mUserText.getText(), mPasswordText.getText(),
                    getSharePath(), mMovieRadioButton.isChecked());
        }
        getDialog().dismiss();
    }

    @OnItemSelected(R.id.share_spinner)
    public void onShareSelected() {
        if (isManualPathSelected()) {
            mPathText.setVisibility(View.VISIBLE);
            mPathText.requestFocus();
        } else {
            mPathText.setVisibility(View.GONE);
        }
    }

    private void searchShares() {
        if (mSearchTask == null) {
            mSearchTask = new NetworkSearchTask(this);
        }

        mSearchTask.execute();
    }

    /**
     * Returns true if manual entry is selected in the share spinner.
     * @return true if manual entry is selected in the share spinner.
     */
    private boolean isManualPathSelected() {
        String value = (String) mShareSpinner.getSelectedItem();
        return getString(R.string.manually_enter_path).equals(value);
    }

    /**
     * Gets the Samba share path, whether it was manually entered
     * or selected in the spinner.
     * @return The Samba share path.
     */
    private String getSharePath() {
        String value = (String) mShareSpinner.getSelectedItem();
        if (isManualPathSelected()) {
            value = mPathText.getText().toString();
        }
        return value;
    }
}