package com.example.vmusic.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.models.ArtistWithSongs;

import java.util.List;

@Dao
public interface ArtistDao {
     @Insert(onConflict = OnConflictStrategy.IGNORE)
     void insertArtist(Artist artist);

     @Query("SELECT * FROM artists WHERE artistId = :artistId")
     Artist getArtistById(int artistId);

     @Query("SELECT * FROM artists")
     List<Artist> getAllArtists();

     @Update
     void updateArtist(Artist artist);

     @Delete
     void deleteArtist(Artist artist);
     @Query("SELECT * FROM artists ORDER BY name ASC")
     LiveData<List<Artist>> getAllArtistsLive();
     @Transaction
     @Query("SELECT * FROM artists WHERE artistId = :artistId")
     LiveData<ArtistWithSongs> getArtistWithSongs(int artistId);
}
