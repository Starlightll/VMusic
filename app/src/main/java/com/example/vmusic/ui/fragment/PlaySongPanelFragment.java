package com.example.vmusic.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentPlaySongPanelBinding;
import com.example.vmusic.entity.Song;
import com.example.vmusic.entity.User;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.PlayerManager;
import com.example.vmusic.ui.adapter.PlaySongPagerAdapter;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaySongPanelFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PlaySongPanelFragment extends Fragment {

    private FragmentPlaySongPanelBinding binding;
    private ExoPlayer player;
    private Handler handler = new Handler(Looper.getMainLooper());
    private PlayerViewModel playerViewModel;
    private SongViewModel songViewModel;
    private boolean isShuffle = false;
    private int repeatMode = 0;
    private int songId=0;
    private ObjectAnimator discAnimator;

    public static PlaySongPanelFragment newInstance(Song song) {
        PlaySongPanelFragment fragment = new PlaySongPanelFragment();
        Bundle args = new Bundle();
        //args.putSerializable("song", song);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaySongPanelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaySongPanelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        player = PlayerManager.getPlayer(requireContext());
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                playerViewModel.setIsPlaying(isPlaying);
            }
        });
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        observeViewModel();
        setupSeekBar();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    long duration = player.getDuration();
                    if (duration > 0) {
                        binding.seekBar.setMax((int) duration);
                        binding.txtTotaltimesong.setText(formatTime((int) duration));
                    }
                }
            }
        });
    }

    private void observeViewModel() {
        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song == null) return;

            binding.tvName.setText(song.getName());
            binding.tvSinger.setText(song.getArtist());

            PlaySongPagerAdapter adapter = new PlaySongPagerAdapter(
                    getChildFragmentManager(),
                    song.getImage(),
                    player,
                    song.getUrlLyric()
            );
            binding.viewPlayMusic.setAdapter(adapter);
            binding.viewPlayMusic.setCurrentItem(1, false);

            if (player.getPlaybackState() == Player.STATE_IDLE || player.getMediaItemCount() == 0) {
                MediaItem mediaItem = MediaItem.fromUri(song.getAudioUrl());
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
                playerViewModel.setIsPlaying(true);
            }
            songId= song.getSongId();
        });

        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), playing -> {
            if (playing != null) {
                binding.imgBtnPlay.setImageResource(playing ? R.drawable.pause_ic : R.drawable.play_ic);
//                if (playing) {
//                    if (discAnimator == null || !discAnimator.isRunning()) {
//                        startDiscAnimation();
//                    } else {
//                        resumeDiscAnimation();
//                    }
//                } else {
//                    pauseDiscAnimation();
//                }
            }
        });

        binding.imgBtnPlay.setOnClickListener(v -> {
            playerViewModel.togglePlayPause();
        });

        binding.imgBtnNext.setOnClickListener(v -> playerViewModel.next());
        binding.imgBtnBack.setOnClickListener(v -> playerViewModel.previous());
        binding.imgBtnSuffle.setOnClickListener(v -> {
            playerViewModel.toggleShuffle();

            isShuffle = !isShuffle;

            if (isShuffle) {
                binding.imgBtnSuffle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_green));
            } else {
                binding.imgBtnSuffle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
            }
        });

        binding.imgBtnRepeat.setOnClickListener(v -> {
            playerViewModel.changeRepeatMode();

            repeatMode = (repeatMode + 1) % 3;

            switch (repeatMode) {
                case 0:
                    binding.imgBtnRepeat.setImageResource(R.drawable.ic_replay);
                    binding.imgBtnRepeat.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                    break;
                case 1:
                    binding.imgBtnRepeat.setImageResource(R.drawable.repeat_1);
                    binding.imgBtnRepeat.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_green));
                    break;
                case 2:
                    binding.imgBtnRepeat.setImageResource(R.drawable.ic_replay);
                    binding.imgBtnRepeat.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_green));
                    break;
            }
        });


        SessionManager session = new SessionManager(requireContext());
        int userId = session.getUserId();

        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                songViewModel.checkIfFavorite(song.songId, userId);
            }
        });
        songViewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFav -> {
            if (isFav != null && isFav) {
                binding.imgBtnFavorite.setImageResource(R.drawable.ic_favorite_full);
            } else {
                binding.imgBtnFavorite.setImageResource(R.drawable.ic_favorite);
            }
        });

        binding.imgBtnFavorite.setOnClickListener(v -> {
            if (userId == 0) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
                return;
            }
            Song currentSong = playerViewModel.getCurrentSong().getValue();
            if (currentSong != null) {
                if (songViewModel.getIsFavorite().getValue() != null && songViewModel.getIsFavorite().getValue()) {
                    songViewModel.removeFromFavorite(currentSong.getSongId(), userId);
                } else {
                    songViewModel.addToFavorite(currentSong.getSongId(), userId);
                }
                songViewModel.checkIfFavorite(currentSong.getSongId(), userId);
            }

        });


    }

    private void setupSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    long current = player.getCurrentPosition();
                    long duration = player.getDuration();

                    if (duration > 0) {
                        binding.seekBar.setMax((int) duration);
                        binding.txtTotaltimesong.setText(formatTime((int) duration));
                    }

                    binding.seekBar.setProgress((int) current);
                    binding.txtTimesong.setText(formatTime((int) current));
                }
                handler.postDelayed(this, 500);
            }
        }, 0);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player.getDuration() > 0) {
                    player.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String formatTime(int millis) {
        int minutes = millis / 60000;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }


//    private void startDiscAnimation() {
//        if (discAnimator == null) {
//            discAnimator = ObjectAnimator.ofFloat(binding.viewPlayMusic, "rotation", 0f, 360f);
//            discAnimator.setDuration(230);
//            discAnimator.setInterpolator(new LinearInterpolator());
//            discAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            discAnimator.setRepeatMode(ValueAnimator.RESTART);
//        }
//        discAnimator.start();
//    }

//    private void pauseDiscAnimation() {
//        if (discAnimator != null && discAnimator.isRunning()) {
//            discAnimator.pause();
//        }
//    }
//
//    private void resumeDiscAnimation() {
//        if (discAnimator != null && discAnimator.isPaused()) {
//            discAnimator.resume();
//        }
//    }
}