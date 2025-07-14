package ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager.widget.ViewPager;

import com.example.vmusic.R;
import com.example.vmusic.ui.adapter.PlaySongPagerAdapter;
import com.example.vmusic.ui.fragment.LyricFragment;

import models.PlayerManager;

public class PlaySongActivity extends AppCompatActivity {
    private TextView tvName, tvSinger;
    private ViewPager viewPager;
    private String imageUrl;
    private ExoPlayer player;
    private ImageButton btnPlay, btnNext, btnPrev;
    private SeekBar seekBar;
    private TextView tvCurrentTime, tvTotalTime;
    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_song);

        // Init UI...
        tvName = findViewById(R.id.tv_name);
        tvSinger = findViewById(R.id.tv_singer);
        viewPager = findViewById(R.id.viewPlayMusic);
        btnPlay = findViewById(R.id.img_btn_play);
        btnNext = findViewById(R.id.img_btn_next);
        btnPrev = findViewById(R.id.img_btn_back);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.txt_timesong);
        tvTotalTime = findViewById(R.id.txt_totaltimesong);

        String name = getIntent().getStringExtra("name");
        String artist = getIntent().getStringExtra("artist");
        imageUrl = getIntent().getStringExtra("image");
        String url = getIntent().getStringExtra("url");
        String lyricUrl = getIntent().getStringExtra("lyric");

        tvName.setText(name);
        tvSinger.setText(artist);

        // Khởi tạo player
        player = PlayerManager.getPlayer(this);

        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Chưa có bài hát nào được chọn", Toast.LENGTH_SHORT).show();
            url = "https://res.cloudinary.com/dkujns7st/video/upload/v1752396062/vmusic/songs/file_kov9c7.mp3";
            imageUrl = "https://res.cloudinary.com/dkujns7st/image/upload/v1752396055/vmusic/cover_art/file_y5lq5t.jpg";
            lyricUrl = "https://res.cloudinary.com/dkujns7st/raw/upload/v1752396065/vmusic/lyrics/file_mjpr3t";
        }

        // Cập nhật ViewPager sau khi đã có player
        PlaySongPagerAdapter adapter = new PlaySongPagerAdapter(getSupportFragmentManager(), imageUrl, player, lyricUrl);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);
        // Setup MediaItem
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        isPlaying = true;
        btnPlay.setImageResource(R.drawable.ic_pause);

        // Play/pause
        btnPlay.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                btnPlay.setImageResource(R.drawable.ic_play);
            } else {
                player.play();
                btnPlay.setImageResource(R.drawable.ic_pause);
            }
        });

        updateSeekBar();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player.getDuration() > 0) {
                    player.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    int duration = (int) player.getDuration();
                    seekBar.setMax(duration);
                    tvTotalTime.setText(formatTime(duration));
                }
            }
        });
    }


    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    int current = (int) player.getCurrentPosition();
                    seekBar.setProgress(current);
                    tvCurrentTime.setText(formatTime(current));
                }
                handler.postDelayed(this, 500);
            }
        }, 0);
    }

    private String formatTime(int millis) {
        int minutes = millis / 60000;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }


}