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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;

import com.example.vmusic.entity.Song;
import com.example.vmusic.ui.activity.SongDetailsActivity;
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
        // === THÊM ĐOẠN CODE MỚI ĐỂ XỬ LÝ TOOLBAR ===
        // 1. Tìm Toolbar trong layout
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_admin_song_list);

        // 2. Gán sự kiện click cho nút điều hướng (mũi tên back)
        toolbar.setNavigationOnClickListener(v -> {
            // 3. Dùng NavController để quay lại màn hình trước đó trong back stack
            //    Đây chính là màn hình Admin Dashboard.
            NavHostFragment.findNavController(AdminSongListFragment.this).popBackStack();
        });
        // === KẾT THÚC CODE MỚI ===
        // Khởi tạo RecyclerView và Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_admin_songs);
        adapter = new AdminSongAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        // Khởi tạo ViewModel
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        // Lấy dữ liệu và quan sát thay đổi
        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            // Khi có dữ liệu mới, cập nhật cho adapter
            adapter.setSongs(songs);
        });

        // Nút Thêm mới (Create)
        FloatingActionButton fab = view.findViewById(R.id.fab_add_song);
        fab.setOnClickListener(v -> {
            // Mở màn hình SongDetailsActivity để thêm mới
            // Không truyền ID, để SongDetailsActivity biết đây là chế độ tạo mới
            Intent intent = new Intent(getActivity(), SongDetailsActivity.class);
            startActivity(intent);
        });
    }

    // Xử lý sự kiện nhấn nút Sửa
    @Override
    public void onEditClick(Song song) {
        // Mở màn hình SongDetailsActivity và truyền ID bài hát để sửa
        Intent intent = new Intent(getActivity(), SongDetailsActivity.class);
        intent.putExtra("SONG_ID_TO_EDIT", song.getSongId());
        startActivity(intent);
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