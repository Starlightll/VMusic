package com.example.vmusic.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;

@Entity(
        primaryKeys = {"playListId", "songId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Playlist.class,
                        parentColumns = "playListId",
                        childColumns = "playListId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Song.class,
                        parentColumns = "songId",
                        childColumns = "songId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class PlaylistSongCrossRef {
    public int playListId;
    public int songId;
}