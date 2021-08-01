package com.gadihkaratau.lamejorescancionesreik.db.tabel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gadihkaratau.lamejorescancionesreik.db.DatabaseContract;
import com.gadihkaratau.lamejorescancionesreik.db.DatabaseHelper;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.utils.Constant;

public class FavoriteSongHelper {

    private static final String TABLE_NAME = Constant.TABLE_SONG_FAVORITS;
    private static DatabaseHelper dataBaseHelper;
    private static FavoriteSongHelper INSTANCE;
    private static SQLiteDatabase database;

    public FavoriteSongHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static FavoriteSongHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new FavoriteSongHelper(context);
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
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
                DatabaseContract.SongColumns.ID + " ASC"
        );
    }

    public Cursor queryById(String id) {
        return database.query(
                TABLE_NAME,
                null,
                DatabaseContract.SongColumns.ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null
        );
    }

    public long insert(Song song) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.SongColumns.ID, song.getId());
        contentValues.put(DatabaseContract.SongColumns.TITLE, song.getTitle());
        contentValues.put(DatabaseContract.SongColumns.ARTIST, song.getArtist());
        contentValues.put(DatabaseContract.SongColumns.IMAGE, song.getImg());
        contentValues.put(DatabaseContract.SongColumns.URL, song.getUrl());
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public boolean isExist(String id) {
        boolean result = false;
        Cursor cursor = queryById(id);
        if (cursor.getCount() > 0)
            result = true;
        cursor.close();
        return result;
    }

    public int update(String id, Song song) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.SongColumns.TITLE, song.getTitle());
        contentValues.put(DatabaseContract.SongColumns.ARTIST, song.getArtist());
        contentValues.put(DatabaseContract.SongColumns.IMAGE, song.getImg());
        contentValues.put(DatabaseContract.SongColumns.URL, song.getUrl());
        return database.update(TABLE_NAME, contentValues, DatabaseContract.SongColumns.ID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(TABLE_NAME, DatabaseContract.SongColumns.ID + " = ?", new String[]{id});
    }

}
