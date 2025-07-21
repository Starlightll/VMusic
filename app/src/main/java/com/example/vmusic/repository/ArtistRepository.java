package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.vmusic.dao.ArtistDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.models.ArtistWithSongs;

import java.util.List;

public class ArtistRepository {
    private ArtistDao artistDao;
    private LiveData<List<Artist>> allArtists;

    public ArtistRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        artistDao = db.artistDao(); // Bạn sẽ cần tạo phương thức này trong AppDatabase
        allArtists = artistDao.getAllArtistsLive();
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
}
