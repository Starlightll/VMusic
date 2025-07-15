package com.example.vmusic.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vmusic.entity.Song;

public class PlayerViewModel extends ViewModel {
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
    }

    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public void setIsPlaying(boolean playing) {
        isPlaying.setValue(playing);
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }


}
