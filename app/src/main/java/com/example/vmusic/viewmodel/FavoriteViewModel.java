package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.models.PlaylistWithSongs;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.repository.ArtistRepository;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.SongRepository;

import java.util.ArrayList;
import java.util.List;

public class FavoriteViewModel extends ViewModel {
    //TODO: Implement the ViewModel for get favorite songs
    private LiveData<List<SongWithArtists>> favoriteSongs;
    private final PlaylistRepository repo;
    private final SongRepository songRepo;

    public FavoriteViewModel(@NonNull Application application, int userId) {
        super();
        repo = new PlaylistRepository(application);
        songRepo = new SongRepository(application);
        favoriteSongs = repo.getFavoriteSongsByUserId(userId);
    }

    public LiveData<List<SongWithArtists>> getFavoritePlaylist() {
        return favoriteSongs;
    }


}
