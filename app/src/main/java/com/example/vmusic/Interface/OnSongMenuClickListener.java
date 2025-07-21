package com.example.vmusic.Interface;

import android.view.View;

import com.example.vmusic.entity.Song;

public interface OnSongMenuClickListener {
    void onAddToFavorite(Song song);
    void onAddToPlaylist(Song song);
}
