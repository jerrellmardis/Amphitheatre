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

package com.jerrellmardis.amphitheatre.activity;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.fragment.AddSourceDialogFragment;
import com.jerrellmardis.amphitheatre.model.Movie;
import com.jerrellmardis.amphitheatre.model.Source;
import com.jerrellmardis.amphitheatre.task.GetFilesTask;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.SecurePreferences;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends Activity implements AddSourceDialogFragment.OnClickListener {

    @InjectView(R.id.welcome_content) View mWelcomeContent;
    @InjectView(R.id.progress_bar_content) View mProgressBarContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        if (Movie.count(Movie.class, null, null) > 0) {
            launchBrowseFragment();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.add_source_button)
    public void addSourceButtonOnClick() {
        FragmentManager fm = getFragmentManager();
        AddSourceDialogFragment addSourceDialog =
                AddSourceDialogFragment.newInstance(getString(R.string.add_source), this);
        addSourceDialog.show(fm, AddSourceDialogFragment.class.getSimpleName());
    }

    @Override
    public void onAddClicked(CharSequence user, CharSequence password, final CharSequence path) {
        mWelcomeContent.setVisibility(View.GONE);
        mProgressBarContent.setVisibility(View.VISIBLE);

        new GetFilesTask(user.toString(), password.toString(), path.toString(),
                new GetFilesTask.OnTaskCompletedListener() {

                    @Override
                    public void onTaskCompleted() {
                        Source source = new Source();
                        source.setSource(path.toString());
                        source.save();

                        launchBrowseFragment();
                    }

                    @Override
                    public void onTaskFailed() {
                        mWelcomeContent.setVisibility(View.VISIBLE);
                        mProgressBarContent.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, getString(R.string.update_failed),
                                Toast.LENGTH_LONG).show();
                    }
                }).execute();

        SecurePreferences securePreferences = new SecurePreferences(this.getApplicationContext());
        securePreferences.edit().putString(Constants.PREFS_USER_KEY, user.toString()).apply();
        securePreferences.edit().putString(Constants.PREFS_PASSWORD_KEY, password.toString()).apply();
    }

    public void launchBrowseFragment() {
        startActivity(new Intent(this, BrowseActivity.class));
        finish();
    }
}