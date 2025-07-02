package com.example.vmusic.model;

import java.util.List;

public class Song {
    private String name;
    private Artist artist;
    private String imageUrl;
    private String url;
    private List<Genres> genres;

    // --- Trường mới ---
    private String language;
    private String releaseDate;
    private String primaryGenre;
    private String secondaryGenre;
    private boolean isExplicit;
    private boolean isPreviouslyReleased;
    private String mainArtist;  // để lưu chuỗi artist nếu không dùng object

    // --- Constructor gốc (nếu bạn cần) ---
    public Song(String name, Artist artist, String imageUrl, String url) {
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.url = url;
    }

    public Song() {} // Constructor rỗng bắt buộc nếu dùng SQLite hoặc Firebase

    // --- GETTER & SETTER cho các trường gốc ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public List<Genres> getGenres() { return genres; }
    public void setGenres(List<Genres> genres) { this.genres = genres; }

    // --- GETTER & SETTER cho metadata ---
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getPrimaryGenre() { return primaryGenre; }
    public void setPrimaryGenre(String primaryGenre) { this.primaryGenre = primaryGenre; }

    public String getSecondaryGenre() { return secondaryGenre; }
    public void setSecondaryGenre(String secondaryGenre) { this.secondaryGenre = secondaryGenre; }

    public boolean isExplicit() { return isExplicit; }
    public void setExplicit(boolean explicit) { isExplicit = explicit; }

    public boolean isPreviouslyReleased() { return isPreviouslyReleased; }
    public void setPreviouslyReleased(boolean previouslyReleased) { isPreviouslyReleased = previouslyReleased; }

    public String getMainArtist() { return mainArtist; }
    public void setMainArtist(String mainArtist) { this.mainArtist = mainArtist; }
}