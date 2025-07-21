package com.example.vmusic.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.vmusic.entity.Song;
import com.example.vmusic.models.SongArtistCrossRef;
import com.example.vmusic.models.SongGenreCrossRef;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.models.SongWithGenres;

import java.util.List;

@Dao
public interface SongDao {
    @Query("SELECT * FROM songs")
    LiveData<List<Song>> getAllSongsLive();

    @Transaction
    @Query("SELECT * FROM songs WHERE songId = :id")
    LiveData<SongWithGenres> getSongWithGenreLive(int id);


    @Query("SELECT * FROM songs WHERE songId = :songid")
    LiveData<Song> getSongById(int songid);

    @Transaction
    @Query("SELECT s.* FROM songs s " +
            "INNER JOIN SongGenreCrossRef sg ON s.songId = sg.songId " +
            "WHERE sg.genreId = :genreId")
    LiveData<List<Song>> getSongsByGenreId(int genreId);

    @Query("SELECT * FROM songs WHERE songId = :id")
    Song getSongById2(int id);


    @Transaction
    @Query("SELECT s.* FROM songs s " +
            "INNER JOIN SongArtistCrossRef sa ON s.songId = sa.songId " +
            "WHERE sa.artistId = :artistId")
    LiveData<List<Song>> getSongsByArtistId(int artistId);


    // Phương thức insert một mối quan hệ
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSongGenreCrossRef(SongGenreCrossRef crossRef);
    @Transaction
    default void insertSongWithGenres(Song song, List<Integer> genreIds) {
        // 1. Insert bài hát và lấy về rowId (kiểu long)
        long rowId = insert(song);

        // Chuyển đổi rowId thành songId kiểu int
        int songId = (int) rowId;

        // 2. Kiểm tra xem bài hát đã được insert thành công chưa
        if (songId > 0 && genreIds != null && !genreIds.isEmpty()) {
            // 3. Lặp qua danh sách các genre ID đã chọn
            for (Integer genreId : genreIds) {
                // Tạo một đối tượng quan hệ với songId kiểu int
                SongGenreCrossRef crossRef = new SongGenreCrossRef(songId, genreId);
                insertSongGenreCrossRef(crossRef);
            }
        }
    }


    @Query("SELECT * FROM songs ORDER BY listenCounts DESC LIMIT 10")
    LiveData<List<Song>> getPopularSongs();
    @Query("SELECT * FROM songs WHERE name LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    LiveData<List<Song>> searchSongs(String query);

    @Query("SELECT * FROM songs ORDER BY songId DESC LIMIT 20")
    LiveData<List<Song>> getRecentSongs();

    @Query("SELECT * FROM songs WHERE songId IN (:songIds)")
    LiveData<List<Song>> getSongsByIds(List<Integer> songIds);

    @Insert
    long insert(Song song);

    @Update
    void updateSong(Song song);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSongArtistCrossRef(SongArtistCrossRef crossRef);
    @Delete
    void deleteSong(Song song);
    // PHƯƠNG THỨC MỚI: Xóa các quan hệ genre của một bài hát
    @Query("DELETE FROM SongGenreCrossRef WHERE songId = :songId")
    void deleteAllGenresForSong(int songId);
    @Transaction
    @Query("SELECT * FROM songs WHERE songId = :songId")
    LiveData<SongWithArtists> getSongWithArtists(int songId);
    @Query("DELETE FROM SongArtistCrossRef WHERE songId = :songId")
    void deleteAllArtistsForSong(int songId);
    // PHƯƠNG THỨC MỚI QUAN TRỌNG: Gộp tất cả hành động update vào một transaction
    @Transaction
    default void updateSongWithGenres(Song song, List<Integer> newGenreIds) {
        // 1. Cập nhật thông tin cơ bản của bài hát
        updateSong(song);

        // 2. Xóa hết các quan hệ genre cũ
        deleteAllGenresForSong(song.getSongId());

        // 3. Thêm các quan hệ genre mới
        if (newGenreIds != null) {
            for (Integer genreId : newGenreIds) {
                SongGenreCrossRef crossRef = new SongGenreCrossRef(song.getSongId(), genreId);
                insertSongGenreCrossRef(crossRef);
            }
        }
    }
    @Transaction
    default void insertSongWithRelationships(Song song, List<Integer> genreIds, List<Integer> artistIds) {
        // 1. Chèn bài hát và lấy về ID của nó
        long songId = insert(song); // Giả sử bạn có phương thức insert(Song) trả về long

        // 2. Chèn các mối quan hệ với Genre
        if (genreIds != null) {
            for (Integer genreId : genreIds) {
                // Giả sử bạn có phương thức insertSongGenreCrossRef
                insertSongGenreCrossRef(new SongGenreCrossRef((int)songId, genreId));
            }
        }

        // 3. Chèn các mối quan hệ với Artist
        if (artistIds != null) {
            for (Integer artistId : artistIds) {
                // Giả sử bạn có phương thức insertSongArtistCrossRef
                insertSongArtistCrossRef(new SongArtistCrossRef((int)songId, artistId));
            }
        }
    }

    @Transaction
    default void updateSongWithRelationships(Song song, List<Integer> genreIds, List<Integer> artistIds) {
        // 1. Cập nhật thông tin bài hát
        updateSong(song); // Giả sử bạn có phương thức updateSong(Song)
        int songId = song.getSongId();

        // 2. Xóa tất cả các mối quan hệ cũ
        deleteAllGenresForSong(songId);
        deleteAllArtistsForSong(songId);

        // 3. Chèn lại các mối quan hệ với Genre
        if (genreIds != null) {
            for (Integer genreId : genreIds) {
                insertSongGenreCrossRef(new SongGenreCrossRef(songId, genreId));
            }
        }

        // 4. Chèn lại các mối quan hệ với Artist
        if (artistIds != null) {
            for (Integer artistId : artistIds) {
                insertSongArtistCrossRef(new SongArtistCrossRef(songId, artistId));
            }
        }
    }
}