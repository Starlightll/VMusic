package com.example.vmusic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.vmusic.ListPlaySongFragment;
import com.example.vmusic.SongImageFragment;

public class SongImageAdapter extends FragmentPagerAdapter {

    public SongImageAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) return new SongImageFragment();
        else return new ListPlaySongFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
