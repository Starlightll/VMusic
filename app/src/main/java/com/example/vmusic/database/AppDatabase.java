package com.example.vmusic.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.vmusic.dao.ArtistDao;
import com.example.vmusic.dao.GenreDao;
import com.example.vmusic.dao.PlaylistDao;
import com.example.vmusic.dao.SongDao;
import com.example.vmusic.dao.UserDao;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.entity.User;
import com.example.vmusic.models.PlaylistSongCrossRef;
import com.example.vmusic.models.SongArtistCrossRef;
import com.example.vmusic.models.SongGenreCrossRef;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Song.class,
        Playlist.class,
        Artist.class,
        Genre.class,
        User.class,
        PlaylistSongCrossRef.class,
        SongGenreCrossRef.class,
        SongArtistCrossRef.class},
        version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "vmusic_database")
                            .fallbackToDestructiveMigration() // xóa db khi nâng version, chỉ dùng khi dev
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // === ĐỊNH NGHĨA CALLBACK Ở ĐÂY ===
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Dùng một thread riêng để insert dữ liệu, tránh block UI thread
            Executors.newSingleThreadExecutor().execute(() -> {
                // Lấy instance của Dao và insert dữ liệu mẫu
                GenreDao dao = INSTANCE.genreDao();

                dao.insertAll(getInitialGenres());
            });
        }
    };


    // === HÀM TẠO RA DỮ LIỆU MẪU ===
    private static Genre[] getInitialGenres() {
        return new Genre[] {
                createGenre("Pop"),
                createGenre("Rock"),
                createGenre("Hip-Hop / Rap"),
                createGenre("Jazz"),
                createGenre("Electronic Dance Music (EDM)"),
                createGenre("R&B / Soul"),
                createGenre("Country"),
                createGenre("Classical"),
                createGenre("V-Pop"),
                createGenre("Ballad"),
                createGenre("Indie"),
                createGenre("Metal"),
                createGenre("Reggae"),
                createGenre("Blues")
        };
    }

    // === HÀM TRỢ GIÚP ĐỂ TẠO ĐỐI TƯỢNG GENRE ===
    private static Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.name = name;
        return genre;
    }


    // Khai báo các abstract Dao của bạn
    public abstract UserDao userDao();
    public abstract SongDao songDao();
    public abstract ArtistDao artistDao();
    public abstract GenreDao genreDao();
    public abstract PlaylistDao playlistDao();
}