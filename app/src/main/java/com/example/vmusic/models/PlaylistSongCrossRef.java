package com.example.vmusic.models;

import androidx.room.Entity;

@Entity(primaryKeys = {"songId" , "playListId"})
public class PlaylistSongCrossRef {
    public int songId;
    public int playListId;
}
