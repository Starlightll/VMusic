package com.example.vmusic.models;

import android.content.Context;

import androidx.media3.exoplayer.ExoPlayer;

public class PlayerManager {
    private static ExoPlayer player;

    public static void init(Context context) {
        if (player == null) {
            player = new ExoPlayer.Builder(context).build();
        }
    }

    public static ExoPlayer getPlayer() {
        return player;
    }

    public static void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public static ExoPlayer getPlayer(Context context) {
        if (player == null) {
            player = new ExoPlayer.Builder(context.getApplicationContext()).build();
        }
        return player;
    }
}
