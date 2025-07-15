package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.example.vmusic.dao.PlaylistDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Playlist;

public class PlaylistRepository {
    private PlaylistDao playlistDao;

    public PlaylistRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        playlistDao = db.playlistDao();
    }
    public LiveData<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylistLive();
    }

    // Thêm playlist
    public void insert(Playlist playlist) {
        new Thread(() -> {
            playlistDao.Insert(playlist);
        }).start();
    }

    // Cập nhật playlist
    public void update(Playlist playlist) {
        new Thread(() -> {
            playlistDao.Update(playlist);
        }).start();
    }

    // Xoá playlist
    public void delete(Playlist playlist) {
        new Thread(() -> {
            playlistDao.Delete(playlist);
        }).start();
    }
}
