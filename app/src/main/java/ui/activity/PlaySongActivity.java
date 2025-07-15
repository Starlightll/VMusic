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

import entity.Song;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_song);

        // Init UI
        tvName = findViewById(R.id.tv_name);
        tvSinger = findViewById(R.id.tv_singer);
        viewPager = findViewById(R.id.viewPlayMusic);
        btnPlay = findViewById(R.id.img_btn_play);
        btnNext = findViewById(R.id.img_btn_next);
        btnPrev = findViewById(R.id.img_btn_back);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.txt_timesong);
        tvTotalTime = findViewById(R.id.txt_totaltimesong);

        player = PlayerManager.getPlayer(this);

        Song song = (Song) getIntent().getSerializableExtra("song");
        if (song == null) {
            Toast.makeText(this, "Chưa có bài hát nào được chọn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(song.getName());
        tvSinger.setText(song.getArtist());
        imageUrl = song.getImage();
        String url = song.getAudioUrl();
        String lyricUrl = song.getUrlLyric();

        // Setup ViewPager
        PlaySongPagerAdapter adapter = new PlaySongPagerAdapter(getSupportFragmentManager(), imageUrl, player, lyricUrl);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        boolean isIdle = player.getPlaybackState() == Player.STATE_IDLE;
        boolean noMedia = player.getMediaItemCount() == 0;

        if (isIdle || noMedia) {
            MediaItem mediaItem = MediaItem.fromUri(url);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        btnPlay.setImageResource(player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

        btnPlay.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                btnPlay.setImageResource(R.drawable.ic_play);
            } else {
                player.play();
                btnPlay.setImageResource(R.drawable.ic_pause);
            }
        });

        // Cập nhật tiến trình phát nhạc
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

        // Khi player READY → cập nhật duration và max
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    long duration = player.getDuration();
                    if (duration > 0) {
                        seekBar.setMax((int) duration);
                        tvTotalTime.setText(formatTime((int) duration));
                    }
                }
            }
        });
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    long current = player.getCurrentPosition();
                    long duration = player.getDuration();

                    if (duration > 0) {
                        seekBar.setMax((int) duration);
                        tvTotalTime.setText(formatTime((int) duration));
                    }

                    seekBar.setProgress((int) current);
                    tvCurrentTime.setText(formatTime((int) current));
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
        handler.removeCallbacksAndMessages(null);
    }
}
