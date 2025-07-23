package com.example.vmusic.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.vmusic.entity.User;
import com.example.vmusic.repository.UserRepository;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_AVATAR = "avatar_url";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(User user) {
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_USERNAME, user.getUserName());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_USER_AVATAR, user.getAvatarUrl());
        editor.apply();
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public boolean isLoggedIn() {
        return getEmail() != null;
    }

    public String getUserAvatar() {
        return prefs.getString(KEY_USER_AVATAR, null);
    }


    public void logout() {
        editor.clear();
        editor.apply();
    }
}
