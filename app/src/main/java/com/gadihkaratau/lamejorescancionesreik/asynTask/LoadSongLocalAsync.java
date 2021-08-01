package com.gadihkaratau.lamejorescancionesreik.asynTask;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.gadihkaratau.lamejorescancionesreik.db.MappingHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadSongAsynCallback;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadSongLocalAsync extends AsyncTask<Void, Void, ArrayList<Song>> {

    private WeakReference<Context> contextWeakReference;
    private WeakReference<LoadSongAsynCallback> callbackWeakReference;

    public LoadSongLocalAsync(Context context, LoadSongAsynCallback loadSongLocalCallback) {
        contextWeakReference = new WeakReference<>(context);
        callbackWeakReference = new WeakReference<>(loadSongLocalCallback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callbackWeakReference.get().preExecute();
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... voids) {
        ContentResolver contentResolver = contextWeakReference.get().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA,
        };
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, projection, selection, null, sortOrder);
        return MappingHelper.mapCursorSongStorage(cursor);
    }

    @Override
    protected void onPostExecute(ArrayList<Song> songs) {
        super.onPostExecute(songs);
        callbackWeakReference.get().postExecute(songs);
    }
}
