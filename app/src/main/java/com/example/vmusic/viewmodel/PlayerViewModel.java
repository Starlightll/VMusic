package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;

import com.example.vmusic.Interface.MusicController;
import com.example.vmusic.entity.Song;
import com.example.vmusic.service.MusicServiceConnectionManager;

import java.util.List;

public class PlayerViewModel extends AndroidViewModel {
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private MusicController musicController;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        MusicServiceConnectionManager.getInstance().bind(application.getApplicationContext(), controller -> {
            this.musicController = controller;
            isPlaying.setValue(controller.isPlaying());
        });
    }

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
    }

    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public MediaItem getCurrentMediaItem() {
        return musicController != null ? musicController.getCurrentMediaItem() : null;
    }

    public void playSong(Song song) {
        if (musicController != null) {
            musicController.playSong(song);
            currentSong.setValue(song);
            isPlaying.setValue(true);
        }
    }

    public void setIsPlaying(boolean playing) {
        isPlaying.setValue(playing);
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public void togglePlayPause() {
        if (musicController == null) return;
        if (musicController.isPlaying()) {
            musicController.pause();
            isPlaying.setValue(false);
        } else {
            musicController.play();
            isPlaying.setValue(true);
        }
    }

    public void toggleShuffle() {
        if (musicController != null) {
            if(musicController.isShuffleEnabled()) {
                musicController.disableShuffle();
            } else {
                musicController.enableShuffle();
            }
        }
    }

    public void changeRepeatMode() {
        if (musicController != null) {
            musicController.changeRepeatMode();
        }
    }

    public void play() {
        if (musicController != null) {
            musicController.play();
            isPlaying.setValue(true);
        }
    }

    public void next() {
        if (musicController != null) {
            musicController.next();
        }
    }

    public void previous() {
        if (musicController != null) {
            musicController.previous();
        }
    }

    public void seekTo(int ms) {
        if (musicController != null) {
            musicController.seekTo(ms);
        }
    }

    public void setPlaylist(List<Song> songs) {
        if (musicController != null) {
            musicController.setPlaylist(songs);
        }
    }

    public void setPlaylist(List<Song> songs, int position) {
        if (musicController != null) {
            musicController.setPlaylist(songs, position);
        }
    }

    public int getCurrentPosition() {
        return musicController != null ? musicController.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return musicController != null ? musicController.getDuration() : 0;
    }


}
