package models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jspecify.annotations.NonNull;
import entity.Playlist;
import java.util.List;

import entity.Song;
import repository.PlaylistRepository;
import repository.SongRepository;

public class LibraryViewModel extends AndroidViewModel {
    private SongRepository repository;
    private final PlaylistRepository playlistRepository;
    private LiveData<List<Song>> allSongs;
    private final LiveData<List<Playlist>> allPlaylists;
    public LibraryViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        playlistRepository = new PlaylistRepository(application);
        allSongs = repository.getAllSongs();
        allPlaylists = playlistRepository.getAllPlaylists();
    }

    public LiveData<List<Song>> getAllSongs() {
        return allSongs;
    }
    public LiveData<List<Playlist>> getAllPlaylists() {
        return allPlaylists;
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
}
