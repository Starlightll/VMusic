package repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import dao.GenreDao;
import dao.PlaylistDao;
import database.AppDatabase;
import entity.Genre;
import entity.Playlist;

public class GenreRepository {

    private GenreDao genreDao;

    public GenreRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        genreDao = db.genreDao();
    }
    public LiveData<List<Genre>> getAllGenreLive() {
        return genreDao.getAllGenreLive();
    }

    // Thêm playlist
    public void insert(Genre genre) {
        new Thread(() -> {
            genreDao.Insert(genre);
        }).start();
    }

    // Cập nhật playlist
    public void update(Genre genre) {
        new Thread(() -> {
            genreDao.Update(genre);
        }).start();
    }

    // Xoá playlist
    public void delete(Genre genre) {
        new Thread(() -> {
            genreDao.Delete(genre);
        }).start();
    }

}
