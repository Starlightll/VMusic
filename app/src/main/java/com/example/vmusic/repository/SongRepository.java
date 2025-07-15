package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.vmusic.dao.SongDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.Song;
import com.example.vmusic.models.SongWithGenres;

import java.util.List;

public class SongRepository {
    private SongDao songDao;

    public SongRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        songDao = db.songDao();
    }


    public void insert(Song song) {
        new Thread(() -> songDao.insert(song)).start(); // dùng Thread để tránh main thread
    }
    public void insertSongWithGenres(final Song song, final List<Integer> genreIds) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            songDao.insertSongWithGenres(song, genreIds);
        });
    }
    public LiveData<List<Song>> getAllSongs() {
        return songDao.getAllSongsLive();
    }
    public LiveData<List<Song>> searchSongsByName(String query) {
        return songDao.searchSongsByName(query);
    }

    public LiveData<SongWithGenres> getSongWithGenres(int id){
        return songDao.getSongWithGenreLive(id);
    }
    public void delete(int songId) {
        new Thread(() -> {
            Song song = songDao.getSongById2(songId);
            if (song != null) {
                songDao.deleteSong(song);
            }
        }).start();
    }


    public LiveData<Song> getSong(int id){
        return songDao.getSongById(id);
    }

    //increase listen count
    public void increaseListenCount(Song song) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            song.setListenCounts(song.getListenCounts() + 1);
            songDao.updateSong(song);
        });
    }

    public LiveData<List<Song>> getPopularSongs() {
        return songDao.getPopularSongs();
    }

    public LiveData<List<Song>> getRecentSongs() {
        return songDao.getRecentSongs();
    }

    public LiveData<List<Song>> getSongsByIds(List<Integer> songIds) {
        return songDao.getSongsByIds(songIds);
    }




}
