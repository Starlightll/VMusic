package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.entity.Song;
import com.example.vmusic.models.SongWithGenres;
import com.example.vmusic.models.SongWithPlaylists;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.SongRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class SongViewModel extends AndroidViewModel{
    private SongRepository repository;
    private PlaylistRepository playlistRepository;
    private List<Song> allSongs;

    private SongWithPlaylists songWithPlaylists;

    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }
    public SongViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        playlistRepository = new PlaylistRepository(application);
    }

    public LiveData<List<Song>> getAllSongs() {
        return repository.getAllSongs();
    }

    public void insert(Song song) {
        repository.insert(song);
    }
    public void insertSongWithGenres(Song song, List<Integer> genreIds) {
        repository.insertSongWithGenres(song, genreIds);
    }
    public LiveData<SongWithGenres> getSongWithGenre(int id){
        return  repository.getSongWithGenres(id);
    }
    public void deleteSong(int id) {
        repository.delete(id);
    }

    public LiveData<List<Song>> searchSongs(String query) {
        return repository.searchSongsByName(query);
    }

    public LiveData<Song> getSong(int id){
        return repository.getSong(id);
    }

    public LiveData<List<Song>> getPopularSongs() {
        return repository.getPopularSongs();
    }
    public LiveData<List<Song>> getRecentSongs() {
        return repository.getRecentSongs();
    }
    public LiveData<List<Song>> getSongsByIds(List<Integer> songIds) {
        return repository.getSongsByIds(songIds);
    }


    public void increaseListenCount(Song song) {
        repository.increaseListenCount(song);
    }
    public void updateSongWithGenres(Song song, List<Integer> genreIds) {
        repository.updateSongWithGenres(song, genreIds);
    }
    public void checkIfFavorite(int songId, int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean result = playlistRepository.isFavorite(songId, userId);
            isFavorite.postValue(result);
        });
    }
    public void addToFavorite(int songId) {
        playlistRepository.addToFavorite(songId);
        isFavorite.setValue(true);
    }

    public void removeFromFavorite(int songId, int userId) {
        playlistRepository.removeFromFavorite(songId, userId);
    }

}
