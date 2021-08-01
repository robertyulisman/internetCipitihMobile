package com.gadihkaratau.lamejorescancionesreik.interfaces;

import com.gadihkaratau.lamejorescancionesreik.models.playlist.GridPlaylist;

import java.util.ArrayList;

public interface LoadGridPlaylistCallback {
    void preExecute();

    void postExecute(ArrayList<GridPlaylist> gridPlaylists);
}
