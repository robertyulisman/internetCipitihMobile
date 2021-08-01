package com.gadihkaratau.lamejorescancionesreik.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.adapters.RecyclerViewSongOfflineAdapter;
import com.gadihkaratau.lamejorescancionesreik.application.MyApplication;
import com.gadihkaratau.lamejorescancionesreik.db.MappingHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.SongOfflineHelper;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.utils.DownloadTracker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

interface LoadSongOfflineCallback {
    void preExecute();

    void postExecute(ArrayList<Song> songArrayList);
}

/**
 * A simple {@link Fragment} subclass.
 */
public class OfflineFragment extends Fragment implements LoadSongOfflineCallback {

    public static String BROADCASTING_OFFLINE_KEY = "broadCastingOfflineKey";
    public static String ACTION_KEY = "actionKey";
    public static String REFRESH_OFFLINE_ACTION = "refreshOfflineAction";
    public static String URL_KEY = "refreshOfflineAction";
    private String TAG = OfflineFragment.class.getSimpleName();
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerViewSongOfflineAdapter listMusicAdapter;
    private OfflineFragmentListener offlineFragmentListener;
    private DownloadTracker downloadTracker;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("offline Fragment", "menerima broadcast");
            String action = intent.getStringExtra(ACTION_KEY);
            if (action == REFRESH_OFFLINE_ACTION) {
                loadSongOffline();
            }
        }
    };


    public OfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(BROADCASTING_OFFLINE_KEY));

//        SongOfflineHelper.getInstance(getContext()).open();

        MyApplication application = (MyApplication) Objects.requireNonNull(getActivity()).getApplication();
        downloadTracker = application.getDownloadTracker();

        progressBar = view.findViewById(R.id.progressBar);

        listMusicAdapter = new RecyclerViewSongOfflineAdapter();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listMusicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        listMusicAdapter.setOnClickAdapterListener(new RecyclerViewSongOfflineAdapter.onClickAdapterListener() {
            @Override
            public void onAdapterClicked(int index, Song song) {
                offlineFragmentListener.onAdapterClicked(index, song);
            }

            @Override
            public void onRemoveFromOfflineClicked(Song song) {
                downloadTracker.removeDownload(song.getUrl());
//                SongOfflineHelper.getInstance(getContext()).deleteById(song.getId());
                listMusicAdapter.removeSong(song);
            }
        });

        loadSongOffline();
    }

    private void loadSongOffline() {
//        new LoadSongOfflineAsync(SongOfflineHelper.getInstance(getContext()), this).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        SongOfflineHelper.getInstance(getContext()).close();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Song> songArrayList) {
        progressBar.setVisibility(View.INVISIBLE);
        if (songArrayList.size() > 0)
            listMusicAdapter.setSongArrayList(songArrayList);
    }

    public void setOfflineFragmentListener(OfflineFragmentListener offlineFragmentListener) {
        this.offlineFragmentListener = offlineFragmentListener;
    }

    public RecyclerViewSongOfflineAdapter getListMusicAdapter() {
        return listMusicAdapter;
    }

    public interface OfflineFragmentListener {
        void onAdapterClicked(int index, Song Song);
    }

    private static class LoadSongOfflineAsync extends AsyncTask<Void, Void, ArrayList<Song>> {
        private final WeakReference<SongOfflineHelper> offlineHelperWeakReference;
        private final WeakReference<LoadSongOfflineCallback> offlineCallbackWeakReference;

        private LoadSongOfflineAsync(SongOfflineHelper songOfflineHelper, LoadSongOfflineCallback callback) {
            this.offlineHelperWeakReference = new WeakReference<>(songOfflineHelper);
            this.offlineCallbackWeakReference = new WeakReference<>(callback);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            offlineCallbackWeakReference.get().preExecute();
        }

        @Override
        protected ArrayList<Song> doInBackground(Void... voids) {
            Cursor cursor = offlineHelperWeakReference.get().queryAll();
            return MappingHelper.mapCursorToArrayListSong(cursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            super.onPostExecute(songs);
            offlineCallbackWeakReference.get().postExecute(songs);
        }
    }

}
