package viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.Song;
import models.SongWithGenres;
import models.SongWithPlaylists;
import repository.SongRepository;

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


}
