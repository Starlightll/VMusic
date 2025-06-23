package com.example.vmusic.model;

import java.util.List;

public class Song {
    private String name;
    private Artist artist;
    private int imageUrl;
    private int url;
    private List<Genres> genres;

    public Song(String name, Artist artist, int imageUrl, int url) {
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }
}
