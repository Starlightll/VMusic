package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.models.PlayerManager;
import com.example.vmusic.ui.activity.PlaySongActivity;
import com.example.vmusic.viewmodel.PlayerViewModel;

public class MiniPlayerFragment extends Fragment {

    private Handler handler = new Handler();
    private Runnable updateProgressRunnable;

    private ImageView imgMini, btnPlay;
    private TextView tvTitle, tvArtist;
    private ProgressBar progressBar;
    private View miniPlayerLayout;

    private ExoPlayer player;
    private PlayerViewModel playerViewModel;

    public MiniPlayerFragment() {
    }

    public static MiniPlayerFragment newInstance(String param1, String param2) {
        MiniPlayerFragment fragment = new MiniPlayerFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mini_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        imgMini = view.findViewById(R.id.imgMini);
        tvTitle = view.findViewById(R.id.tvMiniTitle);
        tvArtist = view.findViewById(R.id.tvMiniArtist);
        btnPlay = view.findViewById(R.id.btnPlay);
        progressBar = view.findViewById(R.id.progressBar);
        miniPlayerLayout = view.findViewById(R.id.mini_player);

        // Init player + ViewModel
        player = PlayerManager.getPlayer();
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        // Initial play/pause icon
        btnPlay.setImageResource(player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

        // Player listener - update icon only
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            }
        });

        //load data
        observePlayerViewModel();

        // icon
        btnPlay.setOnClickListener(v -> togglePlayPause());


        miniPlayerLayout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PlaySongActivity.class);
            intent.putExtra("song", playerViewModel.getCurrentSong().getValue());
            startActivity(intent);
        });


        //progress
        startUpdatingProgressBar();
    }

    private void observePlayerViewModel() {
        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                miniPlayerLayout.setVisibility(View.VISIBLE);
                tvTitle.setText(song.getName());
                tvArtist.setText(song.getArtist());
                Glide.with(requireContext()).load(song.getImage()).into(imgMini);
            } else {
                miniPlayerLayout.setVisibility(View.GONE);
            }
        });

        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        });
    }

    private void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            playerViewModel.setIsPlaying(false);
        } else {
            player.play();
            playerViewModel.setIsPlaying(true);
        }
    }

    private void startUpdatingProgressBar() {
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.getDuration() > 0) {
                    progressBar.setMax((int) player.getDuration());
                    progressBar.setProgress((int) player.getCurrentPosition());
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.post(updateProgressRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateProgressRunnable);
    }
}
