package com.example.vmusic.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.ui.activity.ArtistDetailsActivity;
import com.example.vmusic.ui.adapter.AdminArtistAdapter;
import com.example.vmusic.viewmodel.ArtistViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class AdminArtistListFragment extends Fragment implements AdminArtistAdapter.OnArtistActionClickListener {

    private ArtistViewModel artistViewModel;
    private AdminArtistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_artist_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Xử lý Toolbar ---
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_admin_artist_list);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // --- Xử lý RecyclerView ---
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_admin_artists);
        adapter = new AdminArtistAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        // --- Khởi tạo ViewModel và quan sát dữ liệu ---
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        artistViewModel.getAllArtists().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                adapter.setArtists(artists);
            }
        });

        // --- XỬ LÝ NÚT THÊM MỚI (ĐÃ SỬA LẠI CHO ĐÚNG) ---
        // 1. Tìm đúng FloatingActionButton bằng ID của nó
        FloatingActionButton fabAddArtist = view.findViewById(R.id.fab_add_artist);

        // 2. Gán sự kiện click
        fabAddArtist.setOnClickListener(v -> {
            // 3. Sử dụng Intent để mở ArtistDetailsActivity
            Intent intent = new Intent(getActivity(), ArtistDetailsActivity.class);
            startActivity(intent);
        });
    }

    // --- Các phương thức xử lý Edit và Delete vẫn giữ nguyên ---
    @Override
    public void onEditClick(Artist artist) {
        Intent intent = new Intent(getActivity(), ArtistDetailsActivity.class);
        intent.putExtra("ARTIST_TO_EDIT", artist);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Artist artist) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nghệ sĩ '" + artist.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    artistViewModel.delete(artist);
                    deleteArtistFiles(artist);
                    Toast.makeText(getContext(), "Đã xóa nghệ sĩ", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    private void deleteArtistFiles(Artist artist) {
        if (artist.getImage() == null || artist.getImage().isEmpty()) return;
        try {
            File imageFile = new File(artist.getImage());
            File artistDirectory = imageFile.getParentFile();
            if (artistDirectory != null && artistDirectory.exists()) {
                artistDirectory.delete(); // Xóa cả thư mục chứa ảnh
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}