package com.example.vmusic.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Song;

import java.util.List;

public class ArtistWithSongs {
    @Embedded
    public Artist artist;

    @Relation(
            parentColumn = "artistId",
            entityColumn = "songId",
            associateBy = @Junction(SongArtistCrossRef.class)
    )
    public List<Song> songs;
}