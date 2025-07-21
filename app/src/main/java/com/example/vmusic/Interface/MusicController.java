package com.example.vmusic.Interface;

import androidx.media3.common.MediaItem;

import com.example.vmusic.entity.Song;

import java.util.List;

public interface MusicController {
    void play();
    void pause();
    void seekTo(int positionMs);
    void next();
    void previous();
    void playSong(Song song);
    void setPlaylist(List<Song> songs);
    void setPlaylist(List<Song> songs, int position);
    boolean isPlaying();
    boolean isShuffleEnabled();
    void enableShuffle();
    void disableShuffle();
    void changeRepeatMode();
    int getCurrentPosition();
    MediaItem getCurrentMediaItem();
    int getDuration();
    void toggleShuffle(boolean shuffle);
    void toggleRepeat(boolean repeat);
}
