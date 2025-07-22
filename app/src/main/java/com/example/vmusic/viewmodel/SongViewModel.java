package com.example.vmusic.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Song;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.models.SongWithGenres;
import com.example.vmusic.models.SongWithPlaylists;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.SongRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SongViewModel extends AndroidViewModel{
    private SongRepository repository;
    private PlaylistRepository playlistRepository;
    private List<Song> allSongs;
    private final MutableLiveData<Integer> favoriteChangedSongId = new MutableLiveData<>();
    private SongWithPlaylists songWithPlaylists;
    public LiveData<Integer> getFavoriteChangedSongId() {
        return favoriteChangedSongId;
    }

    private MutableLiveData<List<SongWithArtists>> searchResults = new MutableLiveData<>();
    private MutableLiveData<List<SongWithArtists>> selectedSongs = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> currentSongFavorite = new MutableLiveData<>();
    public LiveData<Boolean> getCurrentSongFavorite() {
        return currentSongFavorite;
    }

    PlayerViewModel playerViewModel = new PlayerViewModel(getApplication());

    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public SongViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        playlistRepository = new PlaylistRepository(application);
    }

    public LiveData<List<SongWithArtists>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<SongWithArtists>> getSelectedSongs() {
        return selectedSongs;
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
        return repository.searchSongs(query);
    }

    public void searchSongsWithArtists(String query) {
        repository.searchSongsWithArtists(query).observeForever(songs -> searchResults.setValue(songs));
    }
    public LiveData<List<Song>> getSongsByArtistId(int artistId) {
        return repository.getSongsByArtistId(artistId);
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

    public void addToFavorite(int songId, int userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            playlistRepository.addToFavorite(songId, userId);

            List<Integer> currentIds = favoriteSongIds.getValue();
            if (currentIds == null) currentIds = new ArrayList<>();
            if (!currentIds.contains(songId)) {
                currentIds = new ArrayList<>(currentIds);
                currentIds.add(songId);
                favoriteSongIds.postValue(currentIds);
            }
        });
    }

    public void removeFromFavorite(int songId, int userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            playlistRepository.removeFromFavorite(songId, userId);

            List<Integer> currentIds = favoriteSongIds.getValue();
            if (currentIds != null && currentIds.contains(songId)) {
                currentIds = new ArrayList<>(currentIds);
                currentIds.remove((Integer) songId);
                favoriteSongIds.postValue(currentIds);
            }
        });
    }

    private final MutableLiveData<List<Integer>> favoriteSongIds = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Integer>> getFavoriteSongIds() {
        return favoriteSongIds;
    }

    public void loadFavoriteSongs(int userId) {
        playlistRepository.getFavoriteSongIds(userId, result -> {
            favoriteSongIds.postValue(result);
        });
    }
    public LiveData<SongWithArtists> getSongWithArtists(int songId) {
        return repository.getSongWithArtists(songId);
    }
    public void insertSongWithRelationships(Song song, List<Integer> genreIds, List<Integer> artistIds) {
        repository.insertSongWithRelationships(song, genreIds, artistIds);
    }
    public void updateSongWithRelationships(Song song, List<Integer> genreIds, List<Integer> artistIds) {
        repository.updateSongWithRelationships(song, genreIds, artistIds);
    }

    public void searchSongsWithArtistsByQuery(String query) {
        LiveData<List<SongWithArtists>> searchLiveData = repository.searchSongsWithArtists(query);
        searchLiveData.observeForever(newSongs -> {
            searchResults.setValue(newSongs);
            searchLiveData.removeObserver(this::searchSongsWithArtistsByQuery);
        });
    }

    private void searchSongsWithArtistsByQuery(List<SongWithArtists> songWithArtists) {
        searchResults.setValue(songWithArtists);
    }

    public void addSongToSelection(SongWithArtists song) {
        List<SongWithArtists> currentSelection = selectedSongs.getValue();
        if (currentSelection != null && !currentSelection.contains(song)) {
            currentSelection.add(song);
            selectedSongs.setValue(currentSelection); // Kích hoạt LiveData
        }
    }

    public void removeSongFromSelection(SongWithArtists song) {
        List<SongWithArtists> currentSelection = selectedSongs.getValue();
        if (currentSelection != null) {
            currentSelection.remove(song);
            selectedSongs.setValue(currentSelection); // Kích hoạt LiveData
        }
    }

    public void addSongsToPlaylist(int playlistId, List<SongWithArtists> songsToAdd) {
        for (SongWithArtists song : songsToAdd) {
            System.out.println("Adding song " + song.song.getName() + " to playlist " + playlistId);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                playlistRepository.addSongToPlaylist(song.song.getSongId(), playlistId);
            });
        }
        selectedSongs.setValue(new ArrayList<>());
    }
}
