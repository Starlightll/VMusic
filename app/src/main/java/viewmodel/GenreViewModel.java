package viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.Genre;
import repository.GenreRepository;

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