package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.SongRepository;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {
    private LiveData<List<SongWithArtists>> playlistSongs;
    private final PlaylistRepository repo;
    private final SongRepository songRepo;

    public PlaylistViewModel(@NonNull Application application, int playlistId) {
        super(application);
        repo = new PlaylistRepository(application);
        songRepo = new SongRepository(application);
        playlistSongs = repo.getPlaylistSongs(playlistId);
    }

    public LiveData<List<SongWithArtists>> getPlaylistSongs() {
        return playlistSongs;
    }

    public LiveData<List<SongWithArtists>> getPlaylistSongs(int playlistId) {
        playlistSongs = repo.getPlaylistSongs(playlistId);
        return playlistSongs;
    }
}
