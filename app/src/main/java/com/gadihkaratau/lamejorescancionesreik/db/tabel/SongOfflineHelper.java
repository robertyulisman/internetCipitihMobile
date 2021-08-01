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

public class SongOfflineHelper {

    private static final String TABLE_NAME = Constant.TABLE_SONG_OFFLINE;
    private static DatabaseHelper dataBaseHelper;
    private static SongOfflineHelper INSTANCE;
    private static SQLiteDatabase database;

    private SongOfflineHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static SongOfflineHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new SongOfflineHelper(context);
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

    public long insert(ContentValues values) {
        return database.insert(TABLE_NAME, null, values);
    }

    public int update(String id, ContentValues values) {
        return database.update(TABLE_NAME, values, DatabaseContract.SongColumns.ID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        Log.d("Delete id ", id);
        return database.delete(TABLE_NAME, DatabaseContract.SongColumns.ID + " = ?", new String[]{id});
    }


}
