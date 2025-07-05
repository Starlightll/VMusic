package models;

import androidx.room.Entity;

@Entity(primaryKeys = {"songId" , "genreId"})
public class SongGenreCrossRef {
    public int songId;
    public int genreId;
}
