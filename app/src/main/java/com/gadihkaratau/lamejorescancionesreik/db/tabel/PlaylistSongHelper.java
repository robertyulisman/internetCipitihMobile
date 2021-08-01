package com.gadihkaratau.lamejorescancionesreik.db.tabel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gadihkaratau.lamejorescancionesreik.db.DatabaseContract;
import com.gadihkaratau.lamejorescancionesreik.db.DatabaseHelper;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.utils.Constant;

public class PlaylistSongHelper {

    private static final String TABLE_NAME = Constant.TABLE_SONG_PLAYLIST;
    private static DatabaseHelper dataBaseHelper;
    private static PlaylistSongHelper INSTANCE;
    private static SQLiteDatabase database;

    public PlaylistSongHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static PlaylistSongHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new PlaylistSongHelper(context);
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
        Log.d("SongOfflineHelper", "open");
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen())
            database.close();
    }

    public Cursor queryAll() {
        return database.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DatabaseContract.SongPlaylistColumns.ID + " ASC"
        );
    }

    public Cursor queryByIdPlaylist(String idPlaylist) {
        return database.query(
                TABLE_NAME,
                null,
                DatabaseContract.SongPlaylistColumns.ID_PLAYLIST + " = ?",
                new String[]{idPlaylist},
                null,
                null,
                null,
                null
        );
    }

    public long insert(Song song, String idPlaylist) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.SongPlaylistColumns.ID_SONG, song.getId());
        contentValues.put(DatabaseContract.SongPlaylistColumns.ID_PLAYLIST, idPlaylist);
        contentValues.put(DatabaseContract.SongPlaylistColumns.TITLE, song.getTitle());
        contentValues.put(DatabaseContract.SongPlaylistColumns.ARTIST, song.getArtist());
        contentValues.put(DatabaseContract.SongPlaylistColumns.IMAGE, song.getImg());
        contentValues.put(DatabaseContract.SongPlaylistColumns.URL, song.getUrl());
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor queryByIdSong(String idSong) {
        return database.query(
                TABLE_NAME,
                null,
                DatabaseContract.SongPlaylistColumns.ID_SONG + " = ?",
                new String[]{idSong},
                null,
                null,
                null,
                null
        );
    }

    public boolean isExist(String idPlaylist, String idSong) {
        boolean result = false;
        Cursor cursor = database.rawQuery("Select idPlaylist, idSong from " + TABLE_NAME + " Where idPlaylist = ? AND idSong = ?", new String[]{idPlaylist, idSong});
        if (cursor.getCount() > 0)
            result = true;
        cursor.close();
        return result;
    }

    public int update(String id, ContentValues values) {
        return database.update(TABLE_NAME, values, DatabaseContract.SongPlaylistColumns.ID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(TABLE_NAME, DatabaseContract.SongPlaylistColumns.ID + " = ?", new String[]{id});
    }
}
