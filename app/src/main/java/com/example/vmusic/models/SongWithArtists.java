package com.example.vmusic.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Song;

import java.util.List;

public class SongWithArtists {

    @Embedded
    public Song song;

    @Relation(
            parentColumn = "songId",
            entityColumn = "artistId",
            associateBy = @Junction(SongArtistCrossRef.class)
    )

    public List<Artist> artists;
}
