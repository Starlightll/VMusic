package com.example.vmusic.Interface;

import java.util.List;

public interface MusicController {
    void play();
    void pause();
    void seekTo(int positionMs);
    void next();
    void previous();
    void setPlaylist(List<String> paths);
    boolean isPlaying();
    boolean isShuffleEnabled();
    void enableShuffle();
    void disableShuffle();
    void changeRepeatMode();
    int getCurrentPosition();
    int getDuration();
    void toggleShuffle(boolean shuffle);
    void toggleRepeat(boolean repeat);
}
