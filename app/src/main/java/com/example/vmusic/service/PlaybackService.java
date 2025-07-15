package com.example.vmusic.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerNotificationManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.vmusic.R;

import models.PlayerManager;

@UnstableApi
public class PlaybackService extends Service {

    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 1;

    private ExoPlayer player;
    private PlayerNotificationManager notificationManager;
    private MediaSessionCompat mediaSession;

    private String songTitle = "";
    private String songArtist = "";
    private String songImage = "";

    @Override
    public void onCreate() {
        super.onCreate();

        player = PlayerManager.getPlayer();

        mediaSession = new MediaSessionCompat(this, "PlaybackService");

        notificationManager = new PlayerNotificationManager.Builder(this, NOTIFICATION_ID, CHANNEL_ID)
                .setMediaDescriptionAdapter(new DescriptionAdapter())
                .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
                .setSmallIconResourceId(R.drawable.ic_music_note)
                .build();
        notificationManager.setUseNextAction(false);
        notificationManager.setUsePreviousAction(false);
        notificationManager.setUseRewindAction(false);
        notificationManager.setUseFastForwardAction(false);
        notificationManager.setUseStopAction(false);

        notificationManager.setPlayer(player);
        notificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        createNotificationChannel();


        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Intent intent = new Intent("PLAYER_STATE_CHANGED");
                intent.putExtra("isPlaying", isPlaying);
                sendBroadcast(intent);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        songTitle = intent.getStringExtra("name");
        songArtist = intent.getStringExtra("artist");
        songImage = intent.getStringExtra("image");

        if (url != null) {
            MediaItem mediaItem = MediaItem.fromUri(url);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (player != null) {
            player.stop();
        }
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
    @Override
    public void onDestroy() {
        notificationManager.setPlayer(null);
        mediaSession.release();
        player.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return songTitle != null ? songTitle : "Đang phát nhạc";
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return songArtist;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            if (songImage != null && !songImage.isEmpty()) {
                Glide.with(PlaybackService.this)
                        .asBitmap()
                        .load(songImage)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                callback.onBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
            return null;
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            // Mở app khi click notification
            Intent intent = new Intent(PlaybackService.this, ui.activity.PlaySongActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return PendingIntent.getActivity(PlaybackService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Phát nhạc",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
