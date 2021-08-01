package com.gadihkaratau.lamejorescancionesreik.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gadihkaratau.lamejorescancionesreik.utils.Constant;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_TABLE_SONG_OFFLINE = String.format(
            "CREATE TABLE %s" +
                    "(%s INTEGER NOT NULL PRIMARY KEY," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_SONG_OFFLINE_NAME,
            DatabaseContract.SongColumns.ID,
            DatabaseContract.SongColumns.TITLE,
            DatabaseContract.SongColumns.ARTIST,
            DatabaseContract.SongColumns.IMAGE,
            DatabaseContract.SongColumns.URL
    );

    private static final String SQL_CREATE_TABLE_SONG_FAVORITE = String.format(
            "CREATE TABLE %s" +
                    "(%s TEXT NOT NULL PRIMARY KEY," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_SONG_FAVORITES,
            DatabaseContract.SongColumns.ID,
            DatabaseContract.SongColumns.TITLE,
            DatabaseContract.SongColumns.ARTIST,
            DatabaseContract.SongColumns.IMAGE,
            DatabaseContract.SongColumns.URL
    );

    private static final String SQL_CREATE_TABLE_NAME_PLAYLIST = String.format(
            "CREATE TABLE %s" +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME_PLAYLIST,
            DatabaseContract.NamePlaylistColumns.ID,
            DatabaseContract.NamePlaylistColumns.NAME
    );
    private static final String SQL_CREATE_TABLE_PLAYLIST_SONG = String.format(
            "CREATE TABLE %s" +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_PLAYLIST_SONG,
            DatabaseContract.SongPlaylistColumns.ID,
            DatabaseContract.SongPlaylistColumns.ID_SONG,
            DatabaseContract.SongPlaylistColumns.ID_PLAYLIST,
            DatabaseContract.SongPlaylistColumns.TITLE,
            DatabaseContract.SongPlaylistColumns.ARTIST,
            DatabaseContract.SongPlaylistColumns.IMAGE,
            DatabaseContract.SongPlaylistColumns.URL
    );
    private static String DATABASE_NAME = Constant.DATABASE_NAME;
    private static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SONG_OFFLINE);
        db.execSQL(SQL_CREATE_TABLE_SONG_FAVORITE);
        db.execSQL(SQL_CREATE_TABLE_NAME_PLAYLIST);
        db.execSQL(SQL_CREATE_TABLE_PLAYLIST_SONG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_SONG_OFFLINE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_SONG_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_PLAYLIST_SONG);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
