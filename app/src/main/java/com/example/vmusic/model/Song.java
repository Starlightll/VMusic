package com.example.vmusic.model;

public class Song {
    private String title;
    private String artist;
    private int imageResId;

    public Song(String title, String artist, int imageResId) {
        this.title = title;
        this.artist = artist;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
