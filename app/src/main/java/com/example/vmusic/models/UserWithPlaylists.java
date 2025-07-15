package com.example.vmusic.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.User;

public class UserWithPlaylists {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "userId",
            entityColumn = "userOwnerId"
    )
    public List<Playlist> playlists;
}
