package com.gadihkaratau.lamejorescancionesreik.asynTask;

import android.database.Cursor;
import android.os.AsyncTask;

import com.gadihkaratau.lamejorescancionesreik.db.MappingHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.FavoriteSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadSongAsynCallback;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadSongFavoriteAsyn extends AsyncTask<Void, Void, ArrayList<Song>> {

    private WeakReference<FavoriteSongHelper> favoriteSongHelperWeakReference;
    private WeakReference<LoadSongAsynCallback> loadSongAsynCallbackWeakReference;

    public LoadSongFavoriteAsyn(FavoriteSongHelper favoriteSongHelper, LoadSongAsynCallback loadSongAsynCallback) {
        favoriteSongHelperWeakReference = new WeakReference<>(favoriteSongHelper);
        loadSongAsynCallbackWeakReference = new WeakReference<>(loadSongAsynCallback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadSongAsynCallbackWeakReference.get().preExecute();
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... voids) {
        Cursor cursor = favoriteSongHelperWeakReference.get().queryAll();
        return MappingHelper.mapCursorToArrayListSong(cursor);
    }

    @Override
    protected void onPostExecute(ArrayList<Song> songArrayList) {
        super.onPostExecute(songArrayList);
        loadSongAsynCallbackWeakReference.get().postExecute(songArrayList);
    }
}
