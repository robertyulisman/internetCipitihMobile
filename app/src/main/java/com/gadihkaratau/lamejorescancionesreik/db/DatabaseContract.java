package com.gadihkaratau.lamejorescancionesreik.db;

import android.provider.BaseColumns;

import com.gadihkaratau.lamejorescancionesreik.utils.Constant;


public class DatabaseContract {

    public static final String TABLE_SONG_OFFLINE_NAME = Constant.TABLE_SONG_OFFLINE;
    public static final String TABLE_NAME_PLAYLIST = Constant.TABLE_NAME_PLAYLIST;
    public static final String TABLE_PLAYLIST_SONG = Constant.TABLE_SONG_PLAYLIST;
    public static final String TABLE_SONG_FAVORITES = Constant.TABLE_SONG_FAVORITS;

    public static final class SongColumns implements BaseColumns {
        public static String ID = "id";
        public static String TITLE = "title";
        public static String ARTIST = "artist";
        public static String IMAGE = "image";
        public static String URL = "url";
    }

    public static final class NamePlaylistColumns implements BaseColumns {
        public static String ID = "id";
        public static String NAME = "name";
    }

    public static final class SongPlaylistColumns implements BaseColumns {
        public static String ID = "id";
        public static String ID_SONG = "idSong";
        public static String ID_PLAYLIST = "idPlaylist";
        public static String TITLE = "title";
        public static String ARTIST = "artist";
        public static String IMAGE = "image";
        public static String URL = "url";
    }
}
