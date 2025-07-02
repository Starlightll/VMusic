package com.example.vmusic.model;

import java.util.List;

public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private Enum role;
    private List<Playlist> playlists;
}
