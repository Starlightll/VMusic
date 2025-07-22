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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSongGenreCrossRef(SongGenreCrossRef crossRef);
    @Transaction
    default void insertSongWithGenres(Song song, List<Integer> genreIds) {
        long rowId = insert(song);
        int songId = (int) rowId;

        if (songId > 0 && genreIds != null && !genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
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
    @Query("SELECT COUNT(*) FROM SongArtistCrossRef WHERE artistId = :artistId")
    int getSongCountForArtist(int artistId);
    @Insert
    long insert(Song song);

    @Update
    void updateSong(Song song);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSongArtistCrossRef(SongArtistCrossRef crossRef);
    @Delete
    void deleteSong(Song song);

    @Query("DELETE FROM SongGenreCrossRef WHERE songId = :songId")
    void deleteAllGenresForSong(int songId);
    @Transaction
    @Query("SELECT * FROM songs WHERE songId = :songId")
    LiveData<SongWithArtists> getSongWithArtists(int songId);
    @Query("DELETE FROM SongArtistCrossRef WHERE songId = :songId")
    void deleteAllArtistsForSong(int songId);

    @Transaction
    default void updateSongWithGenres(Song song, List<Integer> newGenreIds) {
        updateSong(song);
        deleteAllGenresForSong(song.getSongId());
        if (newGenreIds != null) {
            for (Integer genreId : newGenreIds) {
                SongGenreCrossRef crossRef = new SongGenreCrossRef(song.getSongId(), genreId);
                insertSongGenreCrossRef(crossRef);
            }
        }
    }
    @Transaction
    default void insertSongWithRelationships(Song song, List<Integer> genreIds, List<Integer> artistIds) {
        long songId = insert(song);

        if (genreIds != null) {
            for (Integer genreId : genreIds) {
                insertSongGenreCrossRef(new SongGenreCrossRef((int)songId, genreId));
            }
        }

        if (artistIds != null) {
            for (Integer artistId : artistIds) {
                insertSongArtistCrossRef(new SongArtistCrossRef((int)songId, artistId));
            }
        }
    }

    @Transaction
    default void updateSongWithRelationships(Song song, List<Integer> genreIds, List<Integer> artistIds) {
        updateSong(song);
        int songId = song.getSongId();

        deleteAllGenresForSong(songId);
        deleteAllArtistsForSong(songId);

        if (genreIds != null) {
            for (Integer genreId : genreIds) {
                insertSongGenreCrossRef(new SongGenreCrossRef(songId, genreId));
            }
        }

        if (artistIds != null) {
            for (Integer artistId : artistIds) {
                insertSongArtistCrossRef(new SongArtistCrossRef(songId, artistId));
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM songs")
    List<SongWithArtists> getSongsWithArtists();

    @Transaction
    @Query("SELECT * FROM songs WHERE songId = :songId")
    SongWithArtists getSongWithArtists(long songId);

}