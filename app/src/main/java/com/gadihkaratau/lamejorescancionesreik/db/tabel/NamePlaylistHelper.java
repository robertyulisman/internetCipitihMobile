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
import com.gadihkaratau.lamejorescancionesreik.utils.Constant;

public class NamePlaylistHelper {

    private static final String TABLE_NAME = Constant.TABLE_NAME_PLAYLIST;
    private static DatabaseHelper dataBaseHelper;
    private static NamePlaylistHelper INSTANCE;
    private static SQLiteDatabase database;

    public NamePlaylistHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static NamePlaylistHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new NamePlaylistHelper(context);
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
                DatabaseContract.NamePlaylistColumns.ID + " ASC"
        );
    }

    public Cursor queryById(String id) {
        return database.query(
                TABLE_NAME,
                null,
                DatabaseContract.NamePlaylistColumns.ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null
        );
    }

    public long insert(String namePlaylist) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.NamePlaylistColumns.NAME, namePlaylist);
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public int update(String id, String namePlaylist) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.NamePlaylistColumns.NAME, namePlaylist);
        return database.update(TABLE_NAME, contentValues, DatabaseContract.NamePlaylistColumns.ID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        Log.d("Delete id ", id);
        return database.delete(TABLE_NAME, DatabaseContract.NamePlaylistColumns.ID + " = ?", new String[]{id});
    }
}
