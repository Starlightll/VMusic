package com.example.vmusic.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.viewmodel.PlayerViewModel;

import models.PlayerManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MiniPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiniPlayerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler handler = new Handler();
    private Runnable updateProgressRunnable;
    private BroadcastReceiver playbackStateReceiver;

    public MiniPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MiniPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MiniPlayerFragment newInstance(String param1, String param2) {
        MiniPlayerFragment fragment = new MiniPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mini_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgMini = view.findViewById(R.id.imgMini);
        TextView tvTitle = view.findViewById(R.id.tvMiniTitle);
        TextView tvArtist = view.findViewById(R.id.tvMiniArtist);
        ImageView btnPlay = view.findViewById(R.id.btnPlay);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        View miniPlayerLayout = view.findViewById(R.id.mini_player); // ID ngoài cùng của layout MiniPlayer
        ExoPlayer player = PlayerManager.getPlayer();
        PlayerViewModel playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        btnPlay.setImageResource(player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            }
        });

        // Quan sát bài hát hiện tại
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

        // Quan sát trạng thái phát
        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        });

        // Sự kiện nút Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                playerViewModel.setIsPlaying(false);
            } else {
                player.play();
                playerViewModel.setIsPlaying(true);
            }
        });

        // Cập nhật tiến trình
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                ExoPlayer player = PlayerManager.getPlayer();
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
        handler.removeCallbacks(updateProgressRunnable); // Ngừng cập nhật
    }
}