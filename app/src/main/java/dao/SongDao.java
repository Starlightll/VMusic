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





    @Insert
    void insert(Song song);

    @Update
    void updateSong(Song song);

    @Delete
    void deleteSong(Song song);

}
