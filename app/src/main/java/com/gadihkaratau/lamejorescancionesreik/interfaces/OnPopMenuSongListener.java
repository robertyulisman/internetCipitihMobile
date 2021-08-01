package com.gadihkaratau.lamejorescancionesreik.interfaces;

import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

public interface OnPopMenuSongListener {
    void onAddToPlaylist(Song song);

    void onAddToFavorite(Song song);

    void onDownload(Song song);
}
