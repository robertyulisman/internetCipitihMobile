package com.gadihkaratau.lamejorescancionesreik.db;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<Song> mapCursorToArrayListSong(Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SongColumns.ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SongColumns.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SongColumns.ARTIST));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SongColumns.IMAGE));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SongColumns.URL));

                songs.add(new Song(id, title, artist, image, url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static ArrayList<Song> mapCursorSongStorage(Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String image = cursor.getString(3);
                image = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"), image).toString();
                String url = cursor.getString(4);
                url = Uri.parse("file:///" + url).toString();

                songs.add(new Song(id, title, artist, image, url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static ArrayList<NamePlaylist> mapCursorToArrayListNamePlaylist(Cursor cursor) {
        ArrayList<NamePlaylist> namePlaylists = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NamePlaylistColumns.ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NamePlaylistColumns.NAME));
                Log.e("name", id);
                namePlaylists.add(new NamePlaylist(id, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return namePlaylists;
    }


}
