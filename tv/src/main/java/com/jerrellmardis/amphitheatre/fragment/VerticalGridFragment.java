package com.jerrellmardis.amphitheatre.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.jerrellmardis.amphitheatre.util.BlurTransform;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.PicassoBackgroundManagerTarget;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;
import com.jerrellmardis.amphitheatre.widget.TvShowsCardPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class VerticalGridFragment extends android.support.v17.leanback.app.VerticalGridFragment {

    private static final int NUM_COLUMNS = 5;

    private final Handler mHandler = new Handler();

    private String mBackgroundImageUrl;
    private Drawable mDefaultBackground;
    private Timer mBackgroundTimer;
    private Transformation mBlurTransformation;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurTransformation = new BlurTransform(getActivity());
        prepareBackgroundManager();
        setupFragment();
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();

        String selectedGenre = getActivity().getIntent().getStringExtra(Constants.GENRE);
        boolean isMovie = getActivity().getIntent().getBooleanExtra(Constants.IS_VIDEO, true);
        ArrayObjectAdapter adapter;

        if (isMovie) {
            gridPresenter.setNumberOfColumns(NUM_COLUMNS);
            setGridPresenter(gridPresenter);
            adapter = new ArrayObjectAdapter(new CardPresenter(getActivity()));

            String sql = "SELECT V.* FROM MOVIE M, VIDEO V WHERE V.MOVIE = M.ID " +
                    "AND M.FLATTENED_GENRES LIKE '%" + selectedGenre + "%' ORDER BY NAME";
            List<Video> videos = Video.findWithQuery(Video.class, sql);

            setTitle(selectedGenre);

            adapter.addAll(0, videos);
        } else {
            gridPresenter.setNumberOfColumns(NUM_COLUMNS - 2);
            setGridPresenter(gridPresenter);
            adapter = new ArrayObjectAdapter(new TvShowsCardPresenter(getActivity()));

            String sql = "SELECT V.* FROM TV_SHOW T, VIDEO V WHERE V.TV_SHOW = T.ID " +
                    "AND T.FLATTENED_GENRES LIKE '%" + selectedGenre + "%' ORDER BY NAME";
            List<Video> tvshows = Video.findWithQuery(Video.class, sql);

            setTitle(selectedGenre);

            Map<String, VideoGroup> tvShowsMap = new TreeMap<String, VideoGroup>();
            for (Video video : tvshows) {
                if (tvShowsMap.containsKey(video.getName())) {
                    VideoGroup group = tvShowsMap.get(video.getName());
                    if (TextUtils.isEmpty(group.getVideo().getCardImageUrl())) {
                        group.getVideo().setCardImageUrl(video.getCardImageUrl());
                    }

                    group.increment();
                } else {
                    VideoGroup vg = new VideoGroup(video);
                    tvShowsMap.put(video.getName(), vg);
                }
            }

            if (tvShowsMap.size() > 0) {
                adapter.addAll(0, tvShowsMap.values());
            }
        }

        setAdapter(adapter);

        setOnItemClickedListener(new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof VideoGroup) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.IS_VIDEO, false);
                    intent.putExtra(Constants.VIDEO_GROUP, (VideoGroup) item);
                    startActivity(intent);
                } else if (((Video) item).isMatched()) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.IS_VIDEO, true);
                    intent.putExtra(Constants.VIDEO, (Video) item);
                    startActivity(intent);
                }
            }
        });

        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(Object item, Row row) {
                if (item instanceof Video) {
                    try {
                        mBackgroundImageUrl = ((Video) item).getBackgroundImageUrl();
                        startBackgroundTimer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item instanceof VideoGroup) {
                    try {
                        mBackgroundImageUrl = ((VideoGroup) item).getVideo().getBackgroundImageUrl();
                        startBackgroundTimer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void prepareBackgroundManager() {
        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.amphitheatre);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        Picasso.with(getActivity())
                .load(R.drawable.amphitheatre)
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .skipMemoryCache()
                .into(mBackgroundTarget);
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), 300);
    }

    private void updateBackground(String url) {
        Picasso.with(getActivity())
                .load(url)
                .transform(mBlurTransformation)
                .placeholder(R.drawable.placeholder)
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .skipMemoryCache()
                .into(mBackgroundTarget);
    }

    private void clearBackground() {
        BackgroundManager.getInstance(getActivity()).setDrawable(mDefaultBackground);
    }

    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundImageUrl != null) {
                        updateBackground(mBackgroundImageUrl);
                    } else {
                        clearBackground();
                    }
                }
            });
        }
    }
}