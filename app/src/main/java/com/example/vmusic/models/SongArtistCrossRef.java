package com.example.vmusic.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Song;

@Entity(
        primaryKeys = {"songId", "artistId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Song.class,
                        parentColumns = "songId",
                        childColumns = "songId",
                        onDelete = CASCADE // Tự động xóa khi bài hát bị xóa
                ),
                @ForeignKey(
                        entity = Artist.class,
                        parentColumns = "artistId",
                        childColumns = "artistId",
                        onDelete = CASCADE // Tự động xóa khi nghệ sĩ bị xóa
                )
        }
)
public class SongArtistCrossRef {
    public int songId;
    public int artistId;

    public SongArtistCrossRef(int songId, int artistId) {
        this.songId = songId;
        this.artistId = artistId;
    }
}