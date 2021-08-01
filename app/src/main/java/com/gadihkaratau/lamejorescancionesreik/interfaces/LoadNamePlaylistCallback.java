package com.gadihkaratau.lamejorescancionesreik.interfaces;

import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;

import java.util.ArrayList;

public interface LoadNamePlaylistCallback {
    void preExecute();

    void postExecute(ArrayList<NamePlaylist> namePlaylists);
}
