package com.example.vmusic.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Song;

import java.util.List;

public class GenreWithSongs {
    @Embedded
    public Genre genre;
    @Relation(
            parentColumn = "genreId",
            entityColumn = "songId",
            associateBy = @Junction(SongGenreCrossRef.class)
    )
    public List<Song> songs;
}
