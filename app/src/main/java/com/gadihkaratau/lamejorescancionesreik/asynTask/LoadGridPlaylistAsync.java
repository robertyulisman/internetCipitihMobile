package com.gadihkaratau.lamejorescancionesreik.asynTask;

import android.database.Cursor;
import android.os.AsyncTask;

import com.gadihkaratau.lamejorescancionesreik.db.MappingHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.NamePlaylistHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.PlaylistSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadGridPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.GridPlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadGridPlaylistAsync extends AsyncTask<Void, Void, ArrayList<GridPlaylist>> {

    private WeakReference<LoadGridPlaylistCallback> loadGridPlaylistCallbackWeakReference;
    private WeakReference<NamePlaylistHelper> namePlaylistHelperWeakReference;
    private WeakReference<PlaylistSongHelper> playlistSongHelperWeakReference;

    public LoadGridPlaylistAsync(LoadGridPlaylistCallback loadGridPlaylistCallback, NamePlaylistHelper namePlaylistHelper, PlaylistSongHelper playlistSongHelper) {
        loadGridPlaylistCallbackWeakReference = new WeakReference<>(loadGridPlaylistCallback);
        namePlaylistHelperWeakReference = new WeakReference<>(namePlaylistHelper);
        playlistSongHelperWeakReference = new WeakReference<>(playlistSongHelper);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadGridPlaylistCallbackWeakReference.get().preExecute();
    }

    @Override
    protected ArrayList<GridPlaylist> doInBackground(Void... voids) {

        ArrayList<GridPlaylist> gridPlaylists = new ArrayList<>();

        Cursor cursor = namePlaylistHelperWeakReference.get().queryAll();
        ArrayList<NamePlaylist> namePlaylists = MappingHelper.mapCursorToArrayListNamePlaylist(cursor);

        for (NamePlaylist namePlaylist : namePlaylists) {
            Cursor cursorGridPlaylist = playlistSongHelperWeakReference.get().queryByIdPlaylist(namePlaylist.getId());
            ArrayList<Song> songs = MappingHelper.mapCursorToArrayListSong(cursorGridPlaylist);
            gridPlaylists.add(new GridPlaylist(namePlaylist, songs));
        }

        return gridPlaylists;
    }

    @Override
    protected void onPostExecute(ArrayList<GridPlaylist> gridPlaylists) {
        super.onPostExecute(gridPlaylists);
        loadGridPlaylistCallbackWeakReference.get().postExecute(gridPlaylists);
    }
}
