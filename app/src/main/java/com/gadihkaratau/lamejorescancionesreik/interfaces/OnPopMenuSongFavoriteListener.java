package com.gadihkaratau.lamejorescancionesreik.interfaces;

import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

public interface OnPopMenuSongFavoriteListener {
    void onRemoveFromFavorite(Song song);

    void onAddToPlaylist(Song song);
}
