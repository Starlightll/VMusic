package com.example.vmusic;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.helper.ViewModelProviderHelper;
import com.example.vmusic.viewmodel.PlayerViewModel;

import viewmodel.SongViewModel;

public class MainActivity extends AppCompatActivity {
    public SongViewModel songViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            // Navigate to the main screen
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_loginFragment_to_mainFragment);
            }
        }

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

    }
}