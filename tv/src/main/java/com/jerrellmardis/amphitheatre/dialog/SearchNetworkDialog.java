package com.jerrellmardis.amphitheatre.dialog;

import com.jerrellmardis.amphitheatre.R;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jcifs.smb.SmbFile;

/**
 * Presents a list of devices available on the network.
 *
 * This is a lame ping based thing right now since I'm not
 * sure that we can actually reliably query available smb
 * shares.
 *
 * Created by rharter on 8/7/14.
 */
public class SearchNetworkDialog extends Dialog {
    private static final String TAG = SearchNetworkDialog.class.getSimpleName();

    private static final int SMB_PORT = 445;

    public interface Callbacks {
        void onDeviceSelected(String address);
    }

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private Callbacks mCallbacks;

    public SearchNetworkDialog(Context context) {
        super(context);

        setTitle(R.string.search_network_title);

        mListView = new ListView(context);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i,
                    long l) {
                if (mCallbacks != null) {
                    mCallbacks.onDeviceSelected(mAdapter.getItem(i));
                }
            }
        });
        setContentView(mListView);
    }

    @Override public void show() {
        super.show();

        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        mListView.setAdapter(mAdapter);

        new SearchNetworkTask().execute();
    }

    public void setCallbacks(Callbacks cb) {
        mCallbacks = cb;
    }

    /**
     * Searches the network looking for devices listening
     * on the SMB port 445.
     */
    class SearchNetworkTask extends AsyncTask<String, String, Void> {

        @Override protected Void doInBackground(String... strings) {
            try {
                SmbFile[] domains = new SmbFile("smb://").listFiles();
                for (SmbFile d : domains) {
                    SmbFile[] servers = new SmbFile(d.getPath()).listFiles();
                    if (servers == null || servers.length <= 0) {
                        continue;
                    }

                    for (SmbFile server : servers) {
                        SmbFile[] shares = new SmbFile(server.getPath()).listFiles();
                        if (shares == null || shares.length <= 0) {
                            continue;
                        }

                        for (SmbFile share : shares) {
                            // Skip hidden shares
                            if (share.getPath().endsWith("$/")) {
                                continue;
                            }
                            publishProgress(share.getPath());
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override protected void onProgressUpdate(String... values) {
            if (values.length < 1) {
                return;
            }

            String target = values[0];
            mAdapter.add(target);
        }
    }
}
