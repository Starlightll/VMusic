package com.example.vmusic;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.vmusic.adapter.SongImageAdapter;

public class PlaySongActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_song);
        viewPager = findViewById(R.id.viewPlayMusic);
        SongImageAdapter adapter = new SongImageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
}