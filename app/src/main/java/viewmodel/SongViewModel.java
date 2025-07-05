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

    public LiveData<SongWithGenres> getSongWithGenre(int id){
        return  repository.getSongWithGenres(id);
    }

}
