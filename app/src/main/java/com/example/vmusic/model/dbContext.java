package com.example.vmusic.model;

import com.example.vmusic.R;

import java.util.ArrayList;
import java.util.List;

public class dbContext {
    public List<Song> getSongs(int number) {
        List<Song> songList = new ArrayList<>();
        for(int i = 0; i < number; i++) {
            songList.add(new Song("Song " + (i + 1), new Artist("Artist " + (i + 1), R.drawable.nhac), R.drawable.circle_background, R.drawable.ic_launcher_foreground));
        }
        return songList;
    }

    public List<Playlist> getPlaylists(int number) {
        List<Playlist> playlists = new ArrayList<>();
        for(int i = 0; i < number; i++) {
            playlists.add(new Playlist("Playlist " + (i + 1), "Description " + (i + 1), R.drawable.ic_playlist_placeholder));
        }
        return playlists;
    }
}
