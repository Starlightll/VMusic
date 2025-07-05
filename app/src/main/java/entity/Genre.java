package entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "genres")
public class Genre {
    @PrimaryKey(autoGenerate = true)
    public int genreId;
    public String name;
}
