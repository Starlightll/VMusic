package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.repository.ArtistRepository;

import java.util.List;

public class ArtistViewModel extends AndroidViewModel {
    private ArtistRepository repository;
    private LiveData<List<Artist>> allArtists;

    public ArtistViewModel(@NonNull Application application) {
        super(application);
        repository = new ArtistRepository(application);
        allArtists = repository.getAllArtists();
    }

    public LiveData<List<Artist>> getAllArtists() {
        return allArtists;
    }

    public void insert(Artist artist) {
        repository.insert(artist);
    }

    public void update(Artist artist) {
        repository.update(artist);
    }

    public void delete(Artist artist) {
        repository.delete(artist);
    }
}
