package com.jerrellmardis.amphitheatre.task;

import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Searches the network looking for valid Samba shares.
 */
public class NetworkSearchTask extends AsyncTask<Void, Void, List<String>> {

    private static final String TAG = NetworkSearchTask.class.getSimpleName();

    public interface OnSharesFoundListener {
        void onSharesFound(List<String> shares);
    }

    private OnSharesFoundListener mListener;

    public NetworkSearchTask(OnSharesFoundListener listener) {
        mListener = listener;
    }

    @Override protected List<String> doInBackground(Void... voids) {
        List<String> publicShares = new ArrayList<String>();

        SmbFile[] domains = new SmbFile[0];
        try {
            domains = new SmbFile("smb://").listFiles();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid URL.", e);
        } catch (SmbException e) {
            Log.e(TAG, "Failed to search network for shares.", e);
        }

        // No domains found on network.
        if (domains.length == 0) {
            return null;
        }

        for (SmbFile d : domains) {
            if (isCancelled()) {
                return null;
            }

            SmbFile[] servers = new SmbFile[0];
            try {
                servers = new SmbFile(d.getPath()).listFiles();
            } catch (SmbException e) {
                Log.e(TAG, "Invalid URL.", e);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Failed to search domain " + d + " for servers.", e);
            }

            if (servers == null || servers.length <= 0) {
                continue;
            }

            for (SmbFile server : servers) {
                if (isCancelled()) {
                    return null;
                }

                SmbFile[] shares = new SmbFile[0];
                try {
                    shares = new SmbFile(server.getPath()).listFiles();
                } catch (SmbException e) {
                    Log.e(TAG, "Invalid URL.", e);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Failed to search server " + server + " for shares.", e);
                }

                if (shares == null || shares.length <= 0) {
                    continue;
                }

                for (SmbFile share : shares) {
                    // Skip hidden shares
                    if (share.getPath().endsWith("$/")) {
                        continue;
                    }
                    publicShares.add(share.getPath());
                }
            }
        }

        return publicShares;
    }

    @Override protected void onPostExecute(List<String> strings) {
        if (!isCancelled() && mListener != null) {
            mListener.onSharesFound(strings);
        }
    }
}
