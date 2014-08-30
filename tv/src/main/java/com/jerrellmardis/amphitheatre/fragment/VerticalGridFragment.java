package com.jerrellmardis.amphitheatre.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.text.TextUtils;
import android.util.Log;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.activity.DetailsActivity;
import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.model.VideoGroup;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.widget.CardPresenter;
import com.jerrellmardis.amphitheatre.widget.SortedObjectAdapter;
import com.jerrellmardis.amphitheatre.widget.TvShowsCardPresenter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VerticalGridFragment  extends android.support.v17.leanback.app.VerticalGridFragment {
    private static final String TAG = "VerticalGridFragment";

    private static final int NUM_COLUMNS = 5;

    private SortedObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setupFragment();
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();


        Comparator<Video> videoNameComparator = new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                if (o2.getName() == null) {
                    return (o1.getName() == null) ? 0 : -1;
                }
                if (o1.getName() == null) {
                    return 1;
                }
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        };

        String selectedGenre = getActivity().getIntent().getStringExtra(Constants.GENRE);
        boolean isMovie = getActivity().getIntent().getBooleanExtra(Constants.IS_VIDEO, true);

        if(isMovie) {
            gridPresenter.setNumberOfColumns(NUM_COLUMNS);
            setGridPresenter(gridPresenter);
            mAdapter = new SortedObjectAdapter(
                    videoNameComparator, new CardPresenter(getActivity()));

            List<Video> videos = Video.findWithQuery(Video.class,"SELECT V.* FROM MOVIE M, VIDEO V WHERE V.MOVIE = M.ID " +
                     "AND M.FLATTENED_GENRES LIKE '%" + selectedGenre + "%' ORDER BY NAME");

            setTitle(selectedGenre + " " + getString(R.string.movies) + " ("+videos.size()+")");

            /*
            Collections.sort(videos, new Comparator<Video>() {
                @Override
                public int compare(Video o1, Video o2) {
                    return Long.valueOf(o2.getCreated()).compareTo(o1.getCreated());
                }
            });
            */

            for (Video video : videos) {
                mAdapter.add(video);
            }
        }
        else {
            gridPresenter.setNumberOfColumns(NUM_COLUMNS-2);
            setGridPresenter(gridPresenter);
            mAdapter = new SortedObjectAdapter(
                    videoNameComparator, new TvShowsCardPresenter(getActivity()));

            List<Video> tvshows = Video.findWithQuery(Video.class,"SELECT V.* FROM TV_SHOW T, VIDEO V WHERE V.TV_SHOW = V.ID " +
               "AND T.FLATTENED_GENRES LIKE '%" + selectedGenre + "%' ORDER BY NAME");
            setTitle(selectedGenre + " " + getString(R.string.tv_shows) + " ("+tvshows.size()+")");

            Map<String, VideoGroup> tvShowsMap = new TreeMap<String, VideoGroup>();
            for(Video video : tvshows) {
                if(tvShowsMap.containsKey(video.getName())) {
                    VideoGroup group = tvShowsMap.get(video.getName());
                    if (TextUtils.isEmpty(group.getVideo().getCardImageUrl())) {
                        group.getVideo().setCardImageUrl(video.getCardImageUrl());
                    }

                    group.increment();
                }
                else {
                    VideoGroup vg = new VideoGroup(video);
                    tvShowsMap.put(video.getName(), vg);
                }
            }

            if(tvShowsMap.size() > 0) {
                for(VideoGroup videoGroup : tvShowsMap.values()) {
                    mAdapter.add(videoGroup);
                }
            }
        }

        setAdapter(mAdapter);

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
            public void onItemSelected(Object o, Row row) {

            }
        });

    }


}
