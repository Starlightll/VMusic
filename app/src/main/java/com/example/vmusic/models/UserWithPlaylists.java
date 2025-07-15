package com.example.vmusic.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.User;

import java.util.List;

public class UserWithPlaylists {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "userId",
            entityColumn = "userOwnerId"
    )
    public List<Playlist> playlists;
}
