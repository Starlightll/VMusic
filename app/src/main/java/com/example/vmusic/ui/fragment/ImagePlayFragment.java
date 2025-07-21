package com.example.vmusic.ui.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.viewmodel.PlayerViewModel;

import de.hdodenhof.circleimageview.CircleImageView;


public class ImagePlayFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "imageUrl";
    private String imageUrl;
    private PlayerViewModel playerViewModel;

    private ObjectAnimator discAnimator;
    private CircleImageView imageView;

    public static ImagePlayFragment newInstance(String imageUrl) {
        ImagePlayFragment fragment = new ImagePlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_play, container, false);

        imageView = view.findViewById(R.id.img_play_music);

        // Load ảnh ban đầu
        if (imageUrl != null) {
            Glide.with(requireContext()).load(imageUrl).into(imageView);
        }

        discAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        discAnimator.setDuration(20000);
        discAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        discAnimator.setInterpolator(new LinearInterpolator());

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null && song.getImage() != null) {
                imageUrl = song.getImage();
                Glide.with(requireContext()).load(imageUrl).into(imageView);
            }
        });

        playerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            if (isPlaying != null && isPlaying) {
                if (discAnimator != null && !discAnimator.isRunning()) {
                    discAnimator.start();
                } else if (discAnimator != null && discAnimator.isPaused()) {
                    discAnimator.resume();
                }
            } else {
                if (discAnimator != null && discAnimator.isRunning()) {
                    discAnimator.pause();
                }
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (discAnimator != null) {
            discAnimator.cancel();
        }
    }
}
