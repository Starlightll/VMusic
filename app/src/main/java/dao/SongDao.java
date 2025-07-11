package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import entity.Song;
import models.SongGenreCrossRef;
import models.SongWithGenres;
import models.SongWithPlaylists;

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
    @Query("SELECT * FROM songs WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Song>> searchSongsByName(String query);

    @Insert
    long insert(Song song);

    @Update
    void updateSong(Song song);

    @Delete
    void deleteSong(Song song);

}
