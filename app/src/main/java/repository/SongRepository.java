package repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import dao.PlaylistDao;
import dao.SongDao;
import database.AppDatabase;
import entity.Song;
import models.SongWithGenres;
import models.SongWithPlaylists;

public class SongRepository {
    private SongDao songDao;

    public SongRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        songDao = db.songDao();
    }


    public void insert(Song song) {
        new Thread(() -> songDao.insert(song)).start(); // dùng Thread để tránh main thread
    }

    public LiveData<List<Song>> getAllSongs() {
        // Gọi trong thread khác, không phải trong hàm trực tiếp của MainActivity
        return songDao.getAllSongsLive();
    }

    public LiveData<SongWithGenres> getSongWithGenres(int id){
        return songDao.getSongWithGenreLive(id);
    }

}
