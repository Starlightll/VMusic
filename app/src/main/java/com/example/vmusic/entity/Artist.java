package com.example.vmusic.entity;


import androidx.room.Entity;

@Entity(tableName = "artists")
public class Artist {
    private int artistId;
    private String name;
    private String image;
    private int listenCounts;

    public Artist() {
    }

    public Artist(int artistId, String name, String image, int listenCounts) {
        this.artistId = artistId;
        this.name = name;
        this.image = image;
        this.listenCounts = listenCounts;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getListenCounts() {
        return listenCounts;
    }

    public void setListenCounts(int listenCounts) {
        this.listenCounts = listenCounts;
    }
}
