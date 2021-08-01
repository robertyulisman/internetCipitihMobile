package com.gadihkaratau.lamejorescancionesreik.interfaces;

import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.util.ArrayList;

public interface LoadSongAsynCallback {
    void preExecute();

    void postExecute(ArrayList<Song> songArrayList);
}
