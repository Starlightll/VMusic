package models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import entity.Playlist;
import entity.Song;

public class SongWithPlaylists {
    @Embedded
    public Song song;
    @Relation(
            parentColumn = "songId",
            entityColumn = "playListId",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )
    public List<Playlist> playlists;
}
