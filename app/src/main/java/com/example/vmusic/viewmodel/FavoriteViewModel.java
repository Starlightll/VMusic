package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.vmusic.entity.Song;
import com.example.vmusic.models.PlaylistWithSongs;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.repository.PlaylistRepository;

import java.util.List;

public class FavoriteViewModel extends ViewModel {
    //TODO: Implement the ViewModel for get favorite songs
    private LiveData<List<PlaylistWithSongs>> favoritePlaylist;
    private final PlaylistRepository repo;

    public FavoriteViewModel(@NonNull Application application, int userId) {
        super();
        repo = new PlaylistRepository(application);
        favoritePlaylist = repo.getFavoritePlaylistByUserId(userId);
    }

    public LiveData<List<PlaylistWithSongs>> getFavoritePlaylist() {
        return favoritePlaylist;
    }
}
