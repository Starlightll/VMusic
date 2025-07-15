package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entity.Genre;

@Dao
public interface GenreDao {

    @Query("SELECT * FROM genres")
    LiveData<List<Genre>> getAllGenreLive();

    @Insert
    void Insert(Genre genre);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Genre... genres);

    @Update
    void Update(Genre genre);
    @Query("SELECT COUNT(*) FROM genres")
    int countGenres();
    @Delete
    void Delete(Genre genre);
    @Query("SELECT * FROM genres")
    List<Genre> getAllGenres();

    @Query("DELETE FROM genres")
    void deleteAll();
}
