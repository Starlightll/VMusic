package database;
import android.content.Context;
import java.util.concurrent.ExecutorService;
import dao.GenreDao;
import entity.Genre;

public class GenreSeeder {
    public static void seedGenres(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        GenreDao dao = db.genreDao();
        ExecutorService executor = AppDatabase.databaseWriteExecutor;

        executor.execute(() -> {
            if (dao.countGenres() == 0) {
                dao.insertAll(getInitialGenres());
            }
        });
    }

    private static Genre[] getInitialGenres() {
        return new Genre[]{
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

    private static Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.name = name;
        return genre;
    }
}
