package com.example.vmusic;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vmusic.database.GenreSeeder;
import com.example.vmusic.database.UserSeeder;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.viewmodel.SongViewModel;

public class MainActivity extends AppCompatActivity {
    public SongViewModel songViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        GenreSeeder.seedGenres(this);
        UserSeeder.seedUsers(this);
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            // Navigate to the main screen
            if(session.getRole()=="admin") {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    NavController navController = navHostFragment.getNavController();
                    navController.navigate(R.id.action_loginFragment_to_adminDashboardFragment);
                }
            } else {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    NavController navController = navHostFragment.getNavController();
                    navController.navigate(R.id.action_loginFragment_to_mainFragment);
                }
            }
        }

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

    }

    public void switchToAuthNavGraph() {
        NavHostFragment navHostFragment = NavHostFragment.create(R.navigation.nav_graph);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, navHostFragment)
                .commit();
    }
}