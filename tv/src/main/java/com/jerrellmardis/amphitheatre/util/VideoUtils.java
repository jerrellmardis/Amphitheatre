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

package com.jerrellmardis.amphitheatre.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.jerrellmardis.amphitheatre.model.Video;
import com.jerrellmardis.amphitheatre.server.Streamer;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * Created by Jerrell Mardis on 8/4/14.
 */
public class VideoUtils {

    private static final int NOT_FOUND = -1;
    private static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    public static void playVideo(WeakReference<Activity> ref, final Video video) {
        final Activity activity = ref.get();

        if (activity != null) {
            final Streamer streamer = Streamer.getInstance();
            streamer.setOnStreamListener(new Streamer.OnStreamListener() {
                @Override
                public void onStream(int percentStreamed) {
                    // FIXME Ideally, the watch status should only get set once the server has streamed a certain % of the video.
                    // Unfortunately a partial stream is only set when a user has requested to play a partially watched video.
                }

                @Override
                public void onPlay() {
                    video.setWatched(true);

                    List<Video> videos = Select
                            .from(Video.class)
                            .where(Condition.prop("video_url").eq(video.getVideoUrl()))
                            .list();

                    if (!videos.isEmpty()) {
                        Video vid = videos.get(0);
                        if (!vid.isWatched()) {
                            vid.setWatched(true);
                            vid.save();
                        }
                    }
                }
            });

            new Thread() {
                public void run() {
                    try {
                        SecurePreferences preferences = new SecurePreferences(activity.getApplicationContext());

                        String user = preferences.getString(Constants.PREFS_USER_KEY, "");
                        String pass = preferences.getString(Constants.PREFS_PASSWORD_KEY, "");
                        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", user, pass);
                        SmbFile file = new SmbFile(video.getVideoUrl(), auth);
                        streamer.setStreamSrc(file, null);

                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Uri uri = Uri.parse(Streamer.URL + Uri.fromFile(new File(Uri.parse(video.getVideoUrl()).getPath())).getEncodedPath());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setDataAndType(uri, VideoUtils.getMimeType(video.getVideoUrl(), true));
                                    activity.startActivity(i);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public static boolean isVideoFile(String s) {
        String[] fileTypes = new String[]{".3gp", ".aaf.", "mp4", ".ts", ".webm", ".m4v", ".mkv", ".divx", ".xvid", ".rec", ".avi", ".flv", ".f4v", ".moi", ".mpeg", ".mpg", /*".mts", ".m2ts",*/ ".ogv", ".rm", ".rmvb", ".mov", ".wmv", ".iso", ".vob", ".ifo", ".wtv", ".pyv", ".ogm", ".img"};
        int count = fileTypes.length;
        for (int i = 0; i < count; i++)
            if (s.endsWith(fileTypes[i]))
                return true;
        return false;
    }

    public static String getMimeType(String filepath, boolean useWildcard) {
        if (useWildcard)
            return "video/*";

        HashMap<String, String> mimeTypes = new HashMap<String, String>();
        mimeTypes.put("3gp", "video/3gpp");
        mimeTypes.put("aaf", "application/octet-stream");
        mimeTypes.put("mp4", "video/mp4");
        mimeTypes.put("ts", "video/mp2t");
        mimeTypes.put("webm", "video/webm");
        mimeTypes.put("m4v", "video/x-m4v");
        mimeTypes.put("mkv", "video/x-matroska");
        mimeTypes.put("divx", "video/x-divx");
        mimeTypes.put("xvid", "video/x-xvid");
        mimeTypes.put("rec", "application/octet-stream");
        mimeTypes.put("avi", "video/avi");
        mimeTypes.put("flv", "video/x-flv");
        mimeTypes.put("f4v", "video/x-f4v");
        mimeTypes.put("moi", "application/octet-stream");
        mimeTypes.put("mpeg", "video/mpeg");
        mimeTypes.put("mpg", "video/mpeg");
        mimeTypes.put("mts", "video/mts");
        mimeTypes.put("m2ts", "video/mp2t");
        mimeTypes.put("ogv", "video/ogg");
        mimeTypes.put("rm", "application/vnd.rn-realmedia");
        mimeTypes.put("rmvb", "application/vnd.rn-realmedia-vbr");
        mimeTypes.put("mov", "video/quicktime");
        mimeTypes.put("wmv", "video/x-ms-wmv");
        mimeTypes.put("iso", "application/octet-stream");
        mimeTypes.put("vob", "video/dvd");
        mimeTypes.put("ifo", "application/octet-stream");
        mimeTypes.put("wtv", "video/wtv");
        mimeTypes.put("pyv", "video/vnd.ms-playready.media.pyv");
        mimeTypes.put("ogm", "video/ogg");
        mimeTypes.put("img", "application/octet-stream");

        String mime = mimeTypes.get(getExtension(filepath));
        if (mime == null)
            return "video/*";
        return mime;
    }

    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    public static Intent getVideoIntent(Video video) {
        return getVideoIntent(video.getVideoUrl().replace("smb", "http"), "video/*", video);
    }

    public static Intent getVideoIntent(String fileUrl, String mimeType, Video video) {
        if (fileUrl.startsWith("http")) {
            return getVideoIntent(Uri.parse(fileUrl), mimeType, video);
        }

        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setDataAndType(Uri.fromFile(new File(fileUrl)), mimeType);
        videoIntent.putExtras(getVideoIntentBundle(video));

        return videoIntent;
    }

    public static Intent getVideoIntent(Uri file, String mimeType, Video video) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setDataAndType(file, mimeType);
        videoIntent.putExtras(getVideoIntentBundle(video));

        return videoIntent;
    }

    private static Bundle getVideoIntentBundle(Video video) {
        Bundle b = new Bundle();

        String title = video.getName();

        if (video.getMovie() != null) {
            b.putString("plot", video.getMovie().getOverview());
            b.putString("date", video.getMovie().getReleaseDate());
            b.putString("cover", video.getCardImageUrl());
        } else if (video.getTvShow() != null) {
            b.putString("plot", video.getTvShow().getOverview());
            b.putString("date", video.getTvShow().getFirstAirDate());
            b.putString("cover", video.getCardImageUrl());
        }

        b.putString("title", title);
        b.putString("forcename", title);
        b.putBoolean("forcedirect", true);

        return b;
    }

    public static List<SmbFile> getFilesFromDir(String path, NtlmPasswordAuthentication auth) throws Exception {
        List<SmbFile> results = new ArrayList<SmbFile>();
        Set<SmbFile> seen = new LinkedHashSet<SmbFile>();
        Deque<SmbFile> queue = new ArrayDeque<SmbFile>();

        SmbFile baseDir = new SmbFile(path, auth);
        queue.add(baseDir);

        while (!queue.isEmpty()) {
            SmbFile file = queue.removeFirst();
            seen.add(file);

            if (file.isDirectory()) {
                Set<SmbFile> smbFiles = new LinkedHashSet<SmbFile>();
                Collections.addAll(smbFiles, file.listFiles());

                for (SmbFile child : smbFiles) {
                    if (!seen.contains(child)) {
                        queue.add(child);
                    }
                }
            } else if (VideoUtils.isVideoFile(file.getName())) {
                results.add(file);
            }
        }

        return results;
    }
}
