package com.example.vmusic.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.viewmodel.PlayerViewModel;

import de.hdodenhof.circleimageview.CircleImageView;


public class ImagePlayFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "imageUrl";
    private String imageUrl;
    private PlayerViewModel playerViewModel;

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

        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {

            if (song != null && song.getImage() != null) {
                imageUrl = song.getImage();
                CircleImageView imageView = view.findViewById(R.id.img_play_music);
                Glide.with(requireContext()).load(imageUrl).into(imageView);
            }
        });
        return view;
    }
}
