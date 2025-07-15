package com.example.vmusic.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "userId",
        childColumns = "userOwnerId",
        onDelete = ForeignKey.CASCADE
))
public class Playlist {

    @PrimaryKey(autoGenerate = true)

    public int playListId;
    public String name;
    public String type;
    public int userOwnerId;

    public Playlist(int playListId, String name, String type, int userOwnerId) {
        this.playListId = playListId;
        this.name = name;
        this.type = type;
        this.userOwnerId = userOwnerId;
    }

    public int getPlayListId() {
        return playListId;
    }

    public void setPlayListId(int playListId) {
        this.playListId = playListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(int userOwnerId) {
        this.userOwnerId = userOwnerId;
    }
}
