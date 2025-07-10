package com.example.vmusic.helper;

import android.content.Context;
import android.content.SharedPreferences;

import entity.User;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(User user) {
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_USERNAME, user.getUserName());
        editor.putString(KEY_ROLE, user.getRole());
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

    public boolean isLoggedIn() {
        return getEmail() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
