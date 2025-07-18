package com.example.vmusic.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "playlist",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Playlist {

    @PrimaryKey(autoGenerate = true)
    public int playListId;

    public String name;
    public String type;

    public int userId;
    public int userOwnerId;

    public Playlist(int playListId, String name, String type, int userId) {
        this.playListId = playListId;
        this.name = name;
        this.type = type;
        this.userId = userId; // lưu userId
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

