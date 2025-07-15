package com.example.vmusic.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class RecentlyPlayedManager {
    private static final String PREF_NAME = "recently_played_songs";
    private static final int MAX_RECENT = 10;

    private SharedPreferences sharedPreferences;
    private String key;

    public RecentlyPlayedManager(Context context, int userId) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        key = "song_ids_" + userId;
    }

    public void addSongId(int songId) {
        List<Integer> current = getSongIds();


        current.remove(Integer.valueOf(songId));

        current.add(0, songId);


        if (current.size() > MAX_RECENT) {
            current = current.subList(0, MAX_RECENT);
        }


        StringBuilder sb = new StringBuilder();
        for (int id : current) {
            sb.append(id).append(",");
        }

        sharedPreferences.edit()
                .putString(key, sb.toString())
                .apply();
    }

    public List<Integer> getSongIds() {
        String idsString = sharedPreferences.getString(key, "");
        List<Integer> result = new ArrayList<>();

        if (!idsString.isEmpty()) {
            String[] idStrings = idsString.split(",");
            for (String id : idStrings) {
                try {
                    result.add(Integer.parseInt(id));
                } catch (NumberFormatException ignored) {}
            }
        }

        return result;
    }
}
