package entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {
    @PrimaryKey(autoGenerate = true)
    public int songId;
    public String name;
    public String artist;
    public String image;
    public int listenCounts;
    public String audioUrl;
    public  String urlLyric;

    public Song() {
    }

    public Song(int songId, String name, String artist, String image, int listenCounts, String audioUrl, String urlLyric) {
        this.songId = songId;
        this.name = name;
        this.artist = artist;
        this.image = image;
        this.listenCounts = listenCounts;
        this.audioUrl = audioUrl;
        this.urlLyric = urlLyric;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getUrlLyric() {
        return urlLyric;
    }

    public void setUrlLyric(String urlLyric) {
        this.urlLyric = urlLyric;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", image='" + image + '\'' +
                ", listenCounts=" + listenCounts +
                ", audioUrl='" + audioUrl + '\'' +
                ", urlLyric='" + urlLyric + '\'' +
                '}';
    }
}
