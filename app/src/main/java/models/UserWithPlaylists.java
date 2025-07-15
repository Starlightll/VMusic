package models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import entity.Playlist;
import entity.User;

public class UserWithPlaylists {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "userId",
            entityColumn = "userOwnerId"
    )
    public List<Playlist> playlists;
}
