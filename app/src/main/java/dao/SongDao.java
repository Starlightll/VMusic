package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
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

    @Insert
    void insert(Song song);

    @Update
    void updateSong(Song song);

}
