package com.gadihkaratau.lamejorescancionesreik.interfaces;

import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

public interface OnPopMenuSongPlaylistListener {
    void onRemoveFromPlaylist(Song song);

    void onAddToFavorite(Song song);
}
