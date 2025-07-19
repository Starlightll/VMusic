package com.example.vmusic.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vmusic.entity.Artist;

import java.util.List;

@Dao
public interface ArtistDao {
     @Insert
     void insertArtist(Artist artist);

     @Query("SELECT * FROM artists WHERE artistId = :artistId")
     Artist getArtistById(int artistId);

     @Query("SELECT * FROM artists")
     List<Artist> getAllArtists();

     @Update
     void updateArtist(Artist artist);

     @Delete
     void deleteArtist(Artist artist);
}
