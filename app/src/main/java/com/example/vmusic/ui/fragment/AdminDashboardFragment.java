package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vmusic.MainActivity;
import com.example.vmusic.R;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Liên kết với file layout đã tạo
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm CardView "Quản lý bài hát"
        MaterialCardView manageSongsCard = view.findViewById(R.id.card_manage_songs);

        // Gán sự kiện click
        manageSongsCard.setOnClickListener(v -> {
            // Khi nhấn vào, điều hướng đến màn hình CRUD
            // Chúng ta sẽ tạo action này ở bước tiếp theo
            NavHostFragment.findNavController(AdminDashboardFragment.this)
                    .navigate(R.id.action_adminDashboardFragment_to_adminSongListFragment);

        });
        MaterialCardView manageArtistsCard = view.findViewById(R.id.card_manage_artists);
        manageArtistsCard.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_adminDashboardFragment_to_adminArtistListFragment);
        });

        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).switchToAuthNavGraph();
            }
        });
    }
}
