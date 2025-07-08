package com.example.vmusic;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import dao.SongDao;
import database.AppDatabase;
import entity.Song;
import ui.fragment.HomePageMusicFragment;
import viewmodel.SongViewModel;

public class MainActivity extends AppCompatActivity {
    public SongViewModel songViewModel;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);


        //change fragment

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new HomePageMusicFragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                fragment = new HomePageMusicFragment();
            } else if (itemId == R.id.navigation_search) {
                // fragment = new SearchFragment();
            } else if (itemId == R.id.navigation_library) {
                // fragment = new LibraryFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}