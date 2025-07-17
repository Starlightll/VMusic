package com.example.vmusic.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.vmusic.entity.Playlist;
import com.example.vmusic.models.PlaylistSongCrossRef;
import com.example.vmusic.models.PlaylistWithSongs;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Query("SELECT * FROM playlist")
    LiveData<List<Playlist>> getAllPlaylistLive();

    @Insert
    void Insert(Playlist playlist);

    @Update
    void Update(Playlist playlist);

    @Delete
    void Delete(Playlist playlist);
    @Insert
    void insertSongToPlaylist(PlaylistSongCrossRef crossRef);


    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :playlistId")
    LiveData<PlaylistWithSongs> getPlaylistWithSongs(int playlistId);

    // ✅ Lấy playlist theo type
    @Query("SELECT * FROM playlist WHERE type = :type LIMIT 1")
    Playlist getPlaylistByType(String type);

    // ✅ Kiểm tra nếu 1 bài hát đã có trong playlist
    @Query("SELECT COUNT(*) FROM playlistsongcrossref WHERE playListId = :playlistId AND songId = :songId")
    int isSongInPlaylist(int playlistId, int songId);
}
