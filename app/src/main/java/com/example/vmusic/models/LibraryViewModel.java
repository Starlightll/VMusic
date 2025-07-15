package com.example.vmusic.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jspecify.annotations.NonNull;

import java.util.List;

import com.example.vmusic.entity.Song;
import com.example.vmusic.repository.SongRepository;

public class LibraryViewModel extends AndroidViewModel {
    private SongRepository repository;
    private LiveData<List<Song>> allSongs;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        allSongs = repository.getAllSongs();
    }

    public LiveData<List<Song>> getAllSongs() {
        return allSongs;
    }
}
