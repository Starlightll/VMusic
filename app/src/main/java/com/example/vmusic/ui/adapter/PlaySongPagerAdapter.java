package com.example.vmusic.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.vmusic.ui.fragment.ImagePlayFragment;
import com.example.vmusic.ui.fragment.ListSongFragment;
import com.example.vmusic.ui.fragment.LyricFragment;

public class PlaySongPagerAdapter extends FragmentPagerAdapter {

    private final String imageUrl;

    public PlaySongPagerAdapter(@NonNull FragmentManager fm, String imageUrl) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LyricFragment();
            case 1:
                return ImagePlayFragment.newInstance(imageUrl);
            case 2:
                return new ListSongFragment();
            default:
                return new Fragment();
        }
    }


    @Override
    public int getCount() {
        return 3;
    }
}



