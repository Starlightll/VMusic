package com.example.vmusic.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private  int userId;
    private  String passwordHash;
    private String userName;
    private  String email;
    private  String role;
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public User() {
    }

    public User(int userId, String passwordHash, String userName, String email, String role) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public User(int userId, String passwordHash, String userName, String email, String role, String avatarUrl) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
