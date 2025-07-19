package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.vmusic.dao.PlaylistDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.models.PlaylistSongCrossRef;

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
    public LiveData<List<Playlist>> getPlaylistsByUser(int userId) {
        return playlistDao.getAllPlaylistsByUser(userId);
    }

    public void addSongToPlaylist(int songId, int playlistId) {
        executorService.execute(() -> {
            if (playlistDao.isSongInPlaylist(playlistId, songId) == 0) {
                PlaylistSongCrossRef crossRef = new PlaylistSongCrossRef();
                crossRef.playListId = playlistId;
                crossRef.songId = songId;
                playlistDao.insertSongToPlaylist(crossRef);
            }
        });
    }
    public void addToFavorite(int songId) {
        executorService.execute(() -> {
            Playlist favorite = playlistDao.getPlaylistByType("Favorite");

            if (favorite == null) {
                Playlist newFavorite = new Playlist(0, "Yêu thích", "Favorite", 1); // userId = 1
                playlistDao.Insert(newFavorite);
                favorite = playlistDao.getPlaylistByType("Favorite");
            }

            if (playlistDao.isSongInPlaylist(favorite.playListId, songId) == 0) {
                PlaylistSongCrossRef crossRef = new PlaylistSongCrossRef();
                crossRef.playListId = favorite.playListId;
                crossRef.songId = songId;
                playlistDao.insertSongToPlaylist(crossRef);
            }
        });
    }
}