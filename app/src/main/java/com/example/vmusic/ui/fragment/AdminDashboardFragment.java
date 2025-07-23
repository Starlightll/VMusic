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
import com.example.vmusic.helper.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo NavController một lần để tái sử dụng
        NavController navController = NavHostFragment.findNavController(this);

        // Ánh xạ các View
        MaterialCardView manageSongsCard = view.findViewById(R.id.card_manage_songs);
        MaterialCardView manageArtistsCard = view.findViewById(R.id.card_manage_artists);
        // MaterialCardView manageUsersCard = view.findViewById(R.id.card_manage_users); // Sẽ thêm sau
        Button logoutButton = view.findViewById(R.id.btnLogout);

        // Gán sự kiện click
        manageSongsCard.setOnClickListener(v ->
                navController.navigate(R.id.action_adminDashboardFragment_to_adminSongListFragment)
        );

        manageArtistsCard.setOnClickListener(v ->
                navController.navigate(R.id.action_adminDashboardFragment_to_adminArtistListFragment)
        );

        // manageUsersCard.setOnClickListener(v ->
        //     navController.navigate(R.id.action_adminDashboardFragment_to_adminUserListFragment)
        // );

        logoutButton.setOnClickListener(v -> {
            // Xử lý đăng xuất
            SessionManager session = new SessionManager(requireContext());
            session.logout();

            // Quay về màn hình bắt đầu của đồ thị điều hướng chính (là LoginFragment)
            navController.navigate(R.id.nav_graph);
        });
    }
}