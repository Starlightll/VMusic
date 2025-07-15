package com.example.vmusic.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;

public class PlaylistWithSongs {
    @Embedded
    public Playlist playlist;
    @Relation(
            parentColumn = "playListId",
            entityColumn = "songId",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )
    public List<Song> songs;
}
