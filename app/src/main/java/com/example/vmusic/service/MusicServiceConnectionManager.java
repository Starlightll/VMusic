package com.example.vmusic.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;

import com.example.vmusic.Interface.MusicController;

public class MusicServiceConnectionManager {
    private static MusicServiceConnectionManager instance;
    private MusicController musicController;
    private boolean isBound = false;

    public interface OnServiceConnectedCallback {
        void onConnected(MusicController controller);
    }

    private MusicServiceConnectionManager() {}

    public static synchronized MusicServiceConnectionManager getInstance() {
        if (instance == null) {
            instance = new MusicServiceConnectionManager();
        }
        return instance;
    }

    @OptIn(markerClass = UnstableApi.class)
    public void bind(Context context, OnServiceConnectedCallback callback) {
        if (isBound) {
            callback.onConnected(musicController);
            return;
        }

        Intent intent = new Intent(context, PlaybackService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlaybackService.LocalBinder binder = (PlaybackService.LocalBinder) service;
                musicController = binder.getService();
                isBound = true;
                callback.onConnected(musicController);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
                musicController = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public MusicController getController() {
        return musicController;
    }

    public boolean isBound() {
        return isBound;
    }
}
