package com.example.vmusic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import dao.SongDao;
import database.AppDatabase;
import entity.Song;
import ui.fragment.BlankFragment;
import ui.fragment.HomePageMusicFragment;
import viewmodel.SongViewModel;

public class MainActivity extends AppCompatActivity {
    public SongViewModel songViewModel;
    private ExoPlayer exoPlayer;
    private View miniPlayer;
    private ImageView imgMini, btnPlay;
    private TextView tvMiniTitle, tvMiniArtist;
    private boolean isPlaying = false;

    private ProgressBar progressBar;
    private final android.os.Handler handler = new android.os.Handler();
    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);


        //change fragment

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new HomePageMusicFragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                fragment = new HomePageMusicFragment();
            } else if (itemId == R.id.navigation_search) {
                 //fragment = new BlankFragment();
            } else if (itemId == R.id.navigation_library) {
                // fragment = new LibraryFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });


        miniPlayer = findViewById(R.id.mini_player);
        imgMini = miniPlayer.findViewById(R.id.imgMini);
        btnPlay = miniPlayer.findViewById(R.id.btnPlay);
        tvMiniTitle = miniPlayer.findViewById(R.id.tvMiniTitle);
        tvMiniArtist = miniPlayer.findViewById(R.id.tvMiniArtist);

        exoPlayer = new ExoPlayer.Builder(this).build();

        btnPlay.setOnClickListener(v -> togglePlayPause());

        progressBar = miniPlayer.findViewById(R.id.progressBar);


    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }



    private void togglePlayPause() {
        if (exoPlayer != null) {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_play);
                stopProgressUpdater();
            } else {
                exoPlayer.play();
                btnPlay.setImageResource(R.drawable.ic_pause);
                startProgressUpdater();
            }
        }
    }


    public void showMiniPlayer(Song song) {
        miniPlayer.setVisibility(View.VISIBLE);
        tvMiniTitle.setText(song.getName());
        tvMiniArtist.setText(song.getArtist());
        Glide.with(this).load(song.getImage()).into(imgMini);

        MediaItem mediaItem = MediaItem.fromUri(song.getAudioUrl());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        btnPlay.setImageResource(R.drawable.ic_pause);
        isPlaying = true;

        startProgressUpdater();
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null) {
                long duration = exoPlayer.getDuration();
                long currentPosition = exoPlayer.getCurrentPosition();

                if (duration > 0) {
                    int progress = (int) ((currentPosition * 5000) / duration);
                    progressBar.setProgress(progress);
                }


                handler.postDelayed(this, 10);
            }
        }
    };


    private void startProgressUpdater() {
        progressBar.setProgress(0);
        handler.post(updateProgressAction);
    }
    private void stopProgressUpdater() {
        handler.removeCallbacks(updateProgressAction);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        handler.removeCallbacks(updateProgressAction);
    }


}