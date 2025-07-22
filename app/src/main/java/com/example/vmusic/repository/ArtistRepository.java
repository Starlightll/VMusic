package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.vmusic.dao.ArtistDao;
import com.example.vmusic.dao.SongDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.models.ArtistWithSongs;

import java.util.List;

public class ArtistRepository {
    private ArtistDao artistDao;
    private SongDao songDao;
    private LiveData<List<Artist>> allArtists;

    public ArtistRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        artistDao = db.artistDao();
        allArtists = artistDao.getAllArtistsLive();
        songDao = db.songDao();
    }

    public LiveData<List<Artist>> getAllArtists() {
        return allArtists;
    }

    public void insert(Artist artist) {
        AppDatabase.databaseWriteExecutor.execute(() -> artistDao.insertArtist(artist));
    }

    public void update(Artist artist) {
        AppDatabase.databaseWriteExecutor.execute(() -> artistDao.updateArtist(artist));
    }

    public void delete(Artist artist) {
        AppDatabase.databaseWriteExecutor.execute(() -> artistDao.deleteArtist(artist));
    }
    public LiveData<ArtistWithSongs> getArtistWithSongs(int artistId) {
        return artistDao.getArtistWithSongs(artistId);
    }
    public int getSongCountForArtist(int artistId) {
        return songDao.getSongCountForArtist(artistId);
    }
    public Artist getArtistById(int artistId) {
        return artistDao.getArtistById(artistId);
    }
}
