package com.example.vmusic;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongImageFragment extends Fragment {
    View view;
    CircleImageView circleImageView;
    ObjectAnimator animator;

    public SongImageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_image, container, false);
        circleImageView = view.findViewById(R.id.img_play_music);

        animator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0f, 360f);
        animator.setDuration(10000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

        return view;
    }
}
