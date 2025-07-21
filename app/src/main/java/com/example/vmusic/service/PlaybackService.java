package com.example.vmusic.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerNotificationManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.vmusic.Interface.MusicController;
import com.example.vmusic.MainActivity;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.ViewModelProviderHelper;
import com.example.vmusic.models.PlayerManager;
import com.example.vmusic.viewmodel.PlayerViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UnstableApi
public class PlaybackService extends Service implements MusicController {

    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 1;

    private ExoPlayer player;
    private PlayerNotificationManager notificationManager;
    private MediaSessionCompat mediaSession;

    private String songTitle = "";
    private String songArtist = "";
    private String songImage = "";
    private PlayerViewModel playerViewModel;

    private ArrayList<Song> currentSongList = new ArrayList<>();
    private List<MediaItem> mediaItems = new ArrayList<>();
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playerViewModel = ViewModelProviderHelper.getPlayerViewModel();
        player = PlayerManager.getPlayer();
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                true
        );

        mediaSession = new MediaSessionCompat(this, "PlaybackService");

        notificationManager = new PlayerNotificationManager.Builder(this, NOTIFICATION_ID, CHANNEL_ID)
                .setMediaDescriptionAdapter(new DescriptionAdapter())
                .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
                .setSmallIconResourceId(R.drawable.ic_music_note)
                .build();
        notificationManager.setUseNextAction(true);
        notificationManager.setUsePreviousAction(true);
        notificationManager.setUseRewindAction(false);
        notificationManager.setUseFastForwardAction(false);
        notificationManager.setUseStopAction(false);

        mediaItems = new ArrayList<>();

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

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if (mediaItem != null) {
                    Bundle extras = mediaItem.mediaMetadata.extras;
                    if (extras != null) {
                        Song song = (Song) extras.getSerializable("song");
                        if (song != null) {
                            updateCurrentSongInViewModel(song);
                        }
                    }
                } else {
                    songTitle = "";
                    songArtist = "";
                    songImage = "";
                }
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void updateCurrentSongInViewModel(Song song) {
            songTitle = song.getName();
            songArtist = song.getArtist();
            songImage = song.getImage();
            PlayerViewModel viewModel = ViewModelProviderHelper.getPlayerViewModel();
            if (viewModel != null) {
                viewModel.setCurrentSong(song);
                viewModel.setIsPlaying(player.isPlaying());
            }
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



    @Override
    public void play() {
        player.play();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int milliseconds) {
        player.seekTo(milliseconds);

    }

    @Override
    public void next() {
        player.seekToNext();
    }

    @Override
    public void previous() {
        player.seekToPrevious();
    }

    @Override
    public void playSong(Song song) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(song.getAudioUrl())));
        Bundle customExtras = new Bundle();
        customExtras.putString("lyric_url", song.getUrlLyric());
        customExtras.putInt("song_id", song.getSongId());
        customExtras.putSerializable("song", song);
        // Thêm metadata cho bài hát
        mediaItem = mediaItem.buildUpon()
                .setMediaMetadata(
                        new androidx.media3.common.MediaMetadata.Builder()
                                .setTitle(song.getName())
                                .setArtist(song.getArtist())
                                .setArtworkUri(Uri.parse(song.getImage()))
                                .setExtras(customExtras)
                                .build()
                )
                .build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    public void setPlaylist(List<Song> songs) {
        mediaItems.clear();
        currentSongList.clear();
        currentSongList.addAll(songs);
        for (Song song : songs) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(song.getAudioUrl())));
            Bundle customExtras = new Bundle();
            customExtras.putString("lyric_url", song.getUrlLyric());
            customExtras.putInt("song_id", song.getSongId());
            customExtras.putSerializable("song", song);
            // Thêm metadata cho bài hát
            mediaItem = mediaItem.buildUpon()
                    .setMediaMetadata(
                            new androidx.media3.common.MediaMetadata.Builder()
                                    .setTitle(song.getName())
                                    .setArtist(song.getArtist())
                                    .setArtworkUri(Uri.parse(song.getImage()))
                                    .setExtras(customExtras)
                                    .build()
                    )
                    .build();
            mediaItems.add(mediaItem);
        }
        player.setMediaItems(mediaItems);
        player.prepare();
    }

    @Override
    public void setPlaylist(List<Song> songs, int position) {
        mediaItems.clear();
        currentSongList.clear();
        currentSongList.addAll(songs);
        for (Song song : songs) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(song.getAudioUrl())));
            Bundle customExtras = new Bundle();
            customExtras.putString("lyric_url", song.getUrlLyric());
            customExtras.putInt("song_id", song.getSongId());
            customExtras.putSerializable("song", song);
            // Thêm metadata cho bài hát
            mediaItem = mediaItem.buildUpon()
                    .setMediaMetadata(
                            new androidx.media3.common.MediaMetadata.Builder()
                                    .setTitle(song.getName())
                                    .setArtist(song.getArtist())
                                    .setArtworkUri(Uri.parse(song.getImage()))
                                    .setExtras(customExtras)
                                    .build()
                    )
                    .build();
            mediaItems.add(mediaItem);
        }
        player.setMediaItems(mediaItems);
        player.prepare();
        player.seekTo(position, 0);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public boolean isShuffleEnabled() {
        return player.getShuffleModeEnabled();
    }

    @Override
    public void enableShuffle() {
        player.setShuffleModeEnabled(true);
    }

    @Override
    public void disableShuffle() {
        player.setShuffleModeEnabled(false);
    }

    @Override
    public void changeRepeatMode() {
        int currentMode = player.getRepeatMode();
        if (currentMode == Player.REPEAT_MODE_OFF) {
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
        } else if (currentMode == Player.REPEAT_MODE_ONE) {
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        } else {
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
        }
    }

    @Override
    public int getCurrentPosition() {
        return (int) player.getCurrentPosition();
    }

    @Override
    public MediaItem getCurrentMediaItem() {
        if (player.getCurrentMediaItem() != null) {
            return player.getCurrentMediaItem();
        } else {
            return null;
        }
    }

    @Override
    public int getDuration() {
        return (int) player.getDuration();
    }

    @Override
    public void toggleShuffle(boolean shuffle) {
        if (shuffle) {
            player.setShuffleModeEnabled(true);
        } else {
            player.setShuffleModeEnabled(false);
        }
    }

    @Override
    public void toggleRepeat(boolean repeat) {
        if (repeat) {
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
        } else {
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
        }

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
            Intent intent = new Intent(PlaybackService.this, MainActivity.class);
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
