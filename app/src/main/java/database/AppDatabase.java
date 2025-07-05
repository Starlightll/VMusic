package database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import dao.GenreDao;
import dao.PlaylistDao;
import dao.SongDao;
import dao.UserDao;
import entity.Genre;
import entity.Playlist;
import entity.Song;
import entity.User;
import models.GenreWithSongs;
import models.PlaylistSongCrossRef;
import models.SongGenreCrossRef;

@Database(entities = {Song.class , Playlist.class , Genre.class , User.class, PlaylistSongCrossRef.class , SongGenreCrossRef.class} , version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "vmusic_database")
                            .fallbackToDestructiveMigration() // xóa db khi nâng version, chỉ dùng khi dev
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract UserDao userDao();
    public abstract SongDao songDao();

    public abstract GenreDao genreDao();

    public abstract PlaylistDao playlistDao();



}
