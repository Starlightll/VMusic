package com.example.vmusic.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.vmusic.dao.PlaylistDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.models.PlaylistSongCrossRef;
import com.example.vmusic.models.PlaylistWithSongs;
import com.example.vmusic.models.SongWithArtists;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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

    public long insertPlaylist(Playlist playlist) {
        return playlistDao.insert(playlist);
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
    public void addToFavorite(int songId,int userId) {
        executorService.execute(() -> {
            Playlist favorite = playlistDao.getPlaylistByTypeAndUser("Favorite", userId);

            if (favorite == null) {
                Playlist newFavorite = new Playlist(0, "Yêu thích", "Favorite", userId); // userId = 1
                playlistDao.Insert(newFavorite);
                favorite = playlistDao.getPlaylistByTypeAndUser("Favorite", userId);
            }

            if (playlistDao.isSongInPlaylist(favorite.playListId, songId) == 0) {
                PlaylistSongCrossRef crossRef = new PlaylistSongCrossRef();
                crossRef.playListId = favorite.playListId;
                crossRef.songId = songId;
                playlistDao.insertSongToPlaylist(crossRef);
            }
        });
    }
    public boolean isFavorite(int songId, int userId) {
        return playlistDao.isFavorite(songId, userId);
    }

    public void removeFromFavorite(int songId, int userId) {
        executorService.execute(() -> {
            Playlist favorite = playlistDao.getPlaylistByTypeAndUser("Favorite", userId);
            if (favorite != null) {
                playlistDao.deleteSongFromPlaylist(favorite.playListId, songId);
            }
        });
    }
    public void removeFromPlaylist(int songId, int userId) {
        executorService.execute(() -> {
            Playlist favorite = playlistDao.getPlaylistByTypeAndUser("playlist", userId);
            if (favorite != null) {
                playlistDao.deleteSongFromPlaylist(favorite.playListId, songId);
            }
        });
    }
    public void getFavoriteSongIds(int userId, Consumer<List<Integer>> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Integer> result = playlistDao.getFavoriteSongIds(userId);
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.accept(result);
            });
        });
    }
    
    public LiveData<List<SongWithArtists>> getFavoriteSongsByUserId(int userId) {
        //TODO: Get favorite songs by userId
        return playlistDao.getSongsInFavoritePlaylist(userId);
    }

    public LiveData<Boolean> isSongFavorite(int songId, int userId) {
        return playlistDao.isSongFavorite(songId, userId);
    }

    public LiveData<Boolean> isSongInPlaylistLive( int songId , int playlistId) {
        return playlistDao.isSongInPlaylistLive(songId , playlistId );
    }
}