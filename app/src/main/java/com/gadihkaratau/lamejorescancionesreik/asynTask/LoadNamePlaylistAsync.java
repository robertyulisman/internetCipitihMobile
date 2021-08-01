package com.gadihkaratau.lamejorescancionesreik.asynTask;

import android.database.Cursor;
import android.os.AsyncTask;

import com.gadihkaratau.lamejorescancionesreik.db.MappingHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.NamePlaylistHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadNamePlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadNamePlaylistAsync extends AsyncTask<Void, Void, ArrayList<NamePlaylist>> {

    private WeakReference<LoadNamePlaylistCallback> loadNamePlaylistCallbackWeakReference;
    private WeakReference<NamePlaylistHelper> namePlaylistHelperWeakReference;

    public LoadNamePlaylistAsync(LoadNamePlaylistCallback loadNamePlaylistCallback, NamePlaylistHelper namePlaylistHelper) {
        loadNamePlaylistCallbackWeakReference = new WeakReference<>(loadNamePlaylistCallback);
        namePlaylistHelperWeakReference = new WeakReference<>(namePlaylistHelper);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadNamePlaylistCallbackWeakReference.get().preExecute();
    }

    @Override
    protected ArrayList<NamePlaylist> doInBackground(Void... voids) {
        Cursor cursor = namePlaylistHelperWeakReference.get().queryAll();
        return MappingHelper.mapCursorToArrayListNamePlaylist(cursor);
    }

    @Override
    protected void onPostExecute(ArrayList<NamePlaylist> namePlaylists) {
        super.onPostExecute(namePlaylists);
        loadNamePlaylistCallbackWeakReference.get().postExecute(namePlaylists);
    }
}
