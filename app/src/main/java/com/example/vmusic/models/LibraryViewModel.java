package com.example.vmusic.models;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.repository.GenreRepository;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.SongRepository;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private SongRepository repository;
    private final PlaylistRepository playlistRepository;
    private final GenreRepository genreRepository;
    private LiveData<List<Song>> allSongs;
    private final LiveData<List<Playlist>> allPlaylists;
    public LibraryViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        playlistRepository = new PlaylistRepository(application);
        genreRepository = new GenreRepository(application);
        allSongs = repository.getAllSongs();
        allPlaylists = playlistRepository.getAllPlaylists();
    }

    public LiveData<List<Song>> getAllSongs() {
        return allSongs;
    }
    public LiveData<List<Playlist>> getAllPlaylists() {
        return allPlaylists;
    }
    public LiveData<List<Playlist>> getPlaylistsByUser(int userId) {
        return playlistRepository.getPlaylistsByUser(userId);
    }
    public LiveData<List<Genre>> getAllGenres() {
        return genreRepository.getAllGenreLive();
    }

    public void insertPlaylist(Playlist playlist) {
        playlistRepository.insert(playlist);
    }

    public void updatePlaylist(Playlist playlist) {
        playlistRepository.update(playlist);
    }

    public void deletePlaylist(Playlist playlist) {
        playlistRepository.delete(playlist);
    }
    public void addToFavorite(int songId,int userId) {
        playlistRepository.addToFavorite(songId,userId);
    }
    public void removeFromFavorite(int songId, int userId) {
        playlistRepository.removeFromFavorite(songId, userId);
    }
    public void addSongToPlaylist(int songId, int playlistId) {
        playlistRepository.addSongToPlaylist(songId, playlistId);


    }
}
