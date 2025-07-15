package models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import entity.Genre;
import entity.Song;

public class SongWithGenres {
    @Embedded
    public Song song;
    @Relation(
            parentColumn = "songId",
            entityColumn = "genreId",
            associateBy = @Junction(SongGenreCrossRef.class)
    )
    public List<Genre> genres;
}
