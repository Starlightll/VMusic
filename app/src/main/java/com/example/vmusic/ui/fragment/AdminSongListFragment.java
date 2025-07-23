package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;

import com.example.vmusic.entity.Song;
import com.example.vmusic.ui.adapter.AdminSongAdapter;
import com.example.vmusic.viewmodel.SongViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class AdminSongListFragment extends Fragment implements AdminSongAdapter.OnSongActionClickListener {

    private SongViewModel songViewModel;
    private AdminSongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        return inflater.inflate(R.layout.fragment_admin_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- KHỞI TẠO CÁC THÀNH PHẦN GIAO DIỆN ---

        // 1. Toolbar và nút Back
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_admin_song_list);
        toolbar.setNavigationOnClickListener(v -> {
            // Dùng NavController để quay lại màn hình Admin Dashboard
            NavHostFragment.findNavController(AdminSongListFragment.this).popBackStack();
        });

        // 2. RecyclerView và Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_admin_songs);
        // Giả sử AdminSongAdapter của bạn đã được cập nhật để không cần Context
        adapter = new AdminSongAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        // 3. Nút Thêm mới (FloatingActionButton)
        FloatingActionButton fab = view.findViewById(R.id.fab_add_song);
        fab.setOnClickListener(v -> {
            // Điều hướng đến SongDetailsFragment ở chế độ "Thêm mới"
            // (Không cần gửi Bundle, nó sẽ tự dùng giá trị mặc định)
            NavHostFragment.findNavController(AdminSongListFragment.this)
                    .navigate(R.id.action_adminSongListFragment_to_songDetailsFragment);
        });


        // --- LẤY DỮ LIỆU TỪ VIEWMODEL ---

        // Khởi tạo ViewModel
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        // Lấy dữ liệu danh sách bài hát và quan sát thay đổi
        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                // Khi có dữ liệu mới, cập nhật cho adapter
                adapter.setSongs(songs);
            }
        });
    }

    // Xử lý sự kiện nhấn nút Sửa
    @Override
    public void onEditClick(Song song) {
        // Tạo một Bundle để đóng gói ID của bài hát
        Bundle bundle = new Bundle();
        bundle.putInt("songId", song.getSongId()); // "songId" là tên của argument trong nav_graph

        // Lấy NavController
        NavController navController = NavHostFragment.findNavController(AdminSongListFragment.this);

        // Điều hướng đến màn hình chi tiết và gửi kèm Bundle
        navController.navigate(R.id.action_adminSongListFragment_to_songDetailsFragment, bundle);
    }

    // Xử lý sự kiện nhấn nút Xóa
    @Override
    public void onDeleteClick(Song song) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài hát '" + song.getName() + "'? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Gọi ViewModel để xóa khỏi database
                    songViewModel.deleteSong(song.getSongId());
                    // Xóa file vật lý khỏi bộ nhớ
                    deleteSongFiles(song);
                    Toast.makeText(getContext(), "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .setIcon(R.drawable.ic_delete) // Icon cho dialog
                .show();
    }

    // Phương thức quan trọng: Xóa file khỏi bộ nhớ
    private void deleteSongFiles(Song song) {
        if (song.getAudioUrl() == null || song.getAudioUrl().isEmpty()) {
            Log.e("DeleteFile", "Audio path is null or empty.");
            return;
        }

        try {
            File audioFile = new File(song.getAudioUrl());
            File songDirectory = audioFile.getParentFile();

            if (songDirectory != null && songDirectory.exists() && songDirectory.isDirectory()) {
                // Xóa tất cả file trong thư mục
                for (File file : songDirectory.listFiles()) {
                    file.delete();
                }
                // Xóa chính thư mục đó
                songDirectory.delete();
                Log.d("DeleteFile", "Successfully deleted folder: " + songDirectory.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e("DeleteFile", "Error deleting song files", e);
        }
    }
}