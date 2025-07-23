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
import com.example.vmusic.models.SongWithArtists;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Query("SELECT * FROM playlists")
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
    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    LiveData<PlaylistWithSongs> getPlaylistWithSongs(int playlistId);
    @Query("SELECT * FROM playlists WHERE type != 'system'")
    LiveData<List<Playlist>> getAllUserPlaylists();
    // ✅ Lấy playlist theo type
    @Query("SELECT * FROM playlists WHERE type = :type LIMIT 1")
    Playlist getPlaylistByType(String type);

    // ✅ Kiểm tra nếu 1 bài hát đã có trong playlist
    @Query("SELECT COUNT(*) FROM playlistsongcrossref WHERE playListId = :playlistId AND songId = :songId")
    int isSongInPlaylist(int playlistId, int songId);
    @Query("SELECT * FROM playlists WHERE type = :type AND userOwnerId = :userOwnerId LIMIT 1")
    Playlist getPlaylistByTypeAndUser(String type, int userOwnerId);

    @Query("SELECT * FROM playlists WHERE type = :type AND userOwnerId = :userOwnerId")
    LiveData<List<PlaylistWithSongs>> getAllPlaylistsWithSongsByTypeAndUser(String type, int userOwnerId);


    @Query("SELECT * FROM playlists WHERE userOwnerId = :userOwnerId")
    LiveData<List<Playlist>> getAllPlaylistsByUser(int userOwnerId);


    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM PlaylistSongCrossRef ps " +
            "INNER JOIN playlists p ON p.playListId = ps.playListId " +
            "WHERE ps.songId = :songId AND p.userOwnerId = :userId AND p.type = 'Favorite'" +
            ")")
    LiveData<Boolean> isFavorite(int songId, int userId);
    //Delete song from playlist
    @Query("DELETE FROM PlaylistSongCrossRef WHERE playListId = :playlistId AND songId = :songId")
    void deleteSongFromPlaylist(int playlistId, int songId);

    @Query("SELECT ps.songId " +
            "FROM PlaylistSongCrossRef ps " +
            "INNER JOIN playlists p ON p.playListId = ps.playListId " +
            "WHERE p.userOwnerId = :userId AND p.type = 'Favorite'")
    List<Integer> getFavoriteSongIds(int userId);
    @Insert
    long insert(Playlist playlist);



    @Query("SELECT *" +
            "FROM songs s " +
            "INNER JOIN PlaylistSongCrossRef ps ON s.songId = ps.songId " +
            "INNER JOIN playlists p ON ps.playListId = p.playListId " +
            "INNER JOIN SongArtistCrossRef sa ON s.songId = sa.songId " +
            "INNER JOIN artists a ON sa.artistId = a.artistId " +
            "WHERE p.userOwnerId = :userId AND p.type = 'Favorite' " +
            "GROUP BY s.songId")
    LiveData<List<SongWithArtists>> getSongsInFavoritePlaylist(int userId);

    @Query("SELECT * FROM songs s " +
            "INNER JOIN PlaylistSongCrossRef ps ON s.songId = ps.songId " +
            "INNER JOIN playlists p ON ps.playListId = p.playListId " +
            "INNER JOIN SongArtistCrossRef sa ON s.songId = sa.songId " +
            "INNER JOIN artists a ON sa.artistId = a.artistId " +
            "WHERE p.playListId = :playlistId AND p.type == 'playlist' ")
    LiveData<List<SongWithArtists>> getSongsInPlaylist(int playlistId);


    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM PlaylistSongCrossRef ps " +
            "INNER JOIN playlists p ON p.playListId = ps.playListId " +
            "WHERE ps.songId = :songId AND p.userOwnerId = :userId AND p.type = 'Favorite'" +
            ")")
    LiveData<Boolean> isSongFavorite(int songId, int userId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM PlaylistSongCrossRef WHERE playListId = :playlistId AND songId = :songId)")
    LiveData<Boolean> isSongInPlaylistLive(int songId , int playlistId);

}
