package models;

import androidx.room.Entity;

@Entity(primaryKeys = {"songId" , "genreId"})
public class SongGenreCrossRef {
    public int songId;
    public int genreId;
    public SongGenreCrossRef(int songId, int genreId) {
        this.songId = songId;
        this.genreId = genreId;
    }
}
