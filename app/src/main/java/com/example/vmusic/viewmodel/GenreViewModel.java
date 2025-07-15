package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vmusic.entity.Genre;
import com.example.vmusic.repository.GenreRepository;

import java.util.List;

public class GenreViewModel extends AndroidViewModel {
    private GenreRepository repository;
    private LiveData<List<Genre>> allGenres;

    public GenreViewModel(@NonNull Application application) {
        super(application);
        repository = new GenreRepository(application);
        allGenres = repository.getAllGenreLive();
    }

    public LiveData<List<Genre>> getAllGenres() {
        return allGenres;
    }
}