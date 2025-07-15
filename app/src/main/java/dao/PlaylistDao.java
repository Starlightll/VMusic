package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entity.Genre;
import entity.Playlist;

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

}
