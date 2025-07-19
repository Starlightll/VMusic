package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vmusic.entity.Song;
import com.example.vmusic.models.SongWithGenres;
import com.example.vmusic.models.SongWithPlaylists;
import com.example.vmusic.repository.SongRepository;

import java.util.List;

public class SongViewModel extends AndroidViewModel{
    private SongRepository repository;
    private List<Song> allSongs;

    private SongWithPlaylists songWithPlaylists;
    public SongViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);

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
        return repository.searchSongs(query); // gọi hàm mới hỗ trợ name + artist
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
    public LiveData<List<Song>> getSongsByGenreId(int genreId) {
        return repository.getSongsByGenreId(genreId);
    }

    public void increaseListenCount(Song song) {
        repository.increaseListenCount(song);
    }
    public void updateSongWithGenres(Song song, List<Integer> genreIds) {
        repository.updateSongWithGenres(song, genreIds);
    }

}
