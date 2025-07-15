package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.vmusic.dao.PlaylistDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Playlist;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistRepository {
    private PlaylistDao playlistDao;
    private final ExecutorService executorService;

    public PlaylistRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        playlistDao = db.playlistDao();
        executorService = Executors.newFixedThreadPool(4);
    }
    public LiveData<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylistLive();
    }

    // ✅ Thêm playlist
    public void insert(Playlist playlist) {
        executorService.execute(() -> playlistDao.Insert(playlist));
    }

    // ✅ Cập nhật playlist
    public void update(Playlist playlist) {
        executorService.execute(() -> playlistDao.Update(playlist));
    }

    // ✅ Xoá playlist
    public void delete(Playlist playlist) {
        executorService.execute(() -> playlistDao.Delete(playlist));
    }
}
