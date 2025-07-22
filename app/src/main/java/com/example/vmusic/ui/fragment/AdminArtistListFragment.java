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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.ui.adapter.AdminArtistAdapter;
import com.example.vmusic.viewmodel.ArtistViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class AdminArtistListFragment extends Fragment implements AdminArtistAdapter.OnArtistActionClickListener {

    private ArtistViewModel artistViewModel;
    private AdminArtistAdapter adapter;
    private Artist artistToDelete = null;
    private String artistNameToDelete = "";
    private NavController navController; // Khai báo NavController để tái sử dụng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_artist_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo NavController một lần
        navController = NavHostFragment.findNavController(this);

        // Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_admin_artist_list);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_admin_artists);
        // Giả sử constructor của Adapter là (Context, OnArtistActionsListener)
        adapter = new AdminArtistAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        // ViewModel
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        artistViewModel.getAllArtists().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                adapter.setArtists(artists);
            }
        });

        // Nút Thêm mới (FAB)
        FloatingActionButton fabAddArtist = view.findViewById(R.id.fab_add_artist);
        fabAddArtist.setOnClickListener(v -> {
            navController.navigate(R.id.action_adminArtistListFragment_to_artistDetailsFragment);
        });

        // <<--- ĐÂY LÀ PHIÊN BẢN OBSERVER AN TOÀN HƠN ---
        artistViewModel.getCanDeleteArtist().observe(getViewLifecycleOwner(), canDelete -> {
            if (canDelete) {
                // Xóa thành công
                // Sử dụng tên đã lưu, không truy cập vào artistToDelete.getName() nữa
                Toast.makeText(getContext(), "Đã xóa nghệ sĩ '" + artistNameToDelete + "'", Toast.LENGTH_SHORT).show();

                // Chỉ xóa file khi đã xóa thành công trong database
                if (artistToDelete != null) {
                    // deleteArtistFiles(artistToDelete); // Gọi phương thức xóa file của bạn
                    artistToDelete = null; // Reset biến tạm
                }
            } else {
                // Xóa thất bại
                if (artistToDelete != null) { // Kiểm tra để chắc chắn đây là phản hồi cho một yêu cầu xóa
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Không thể xóa")
                            .setMessage("Không thể xóa nghệ sĩ '" + artistNameToDelete + "' vì họ vẫn còn bài hát trong hệ thống.")
                            .setPositiveButton("OK", null)
                            .show();
                    artistToDelete = null; // Reset biến tạm
                }
            }
        });
    }

    @Override
    public void onEditClick(Artist artist) {
        // SỬ DỤNG NAVCONTROLLER VÀ BUNDLE ĐỂ TRUYỀN DỮ LIỆU
        Bundle bundle = new Bundle();
        bundle.putInt("artistId", artist.getArtistId()); // "artistId" phải khớp với tên argument trong nav_graph

        navController.navigate(R.id.action_adminArtistListFragment_to_artistDetailsFragment, bundle);
    }

    // --- Phương thức xử lý Xóa ---
    @Override
    public void onDeleteClick(Artist artist) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nghệ sĩ '" + artist.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Lưu lại đối tượng VÀ TÊN của nghệ sĩ sắp xóa
                    this.artistToDelete = artist;
                    this.artistNameToDelete = artist.getName(); // <<--- LƯU TÊN LẠI

                    // Gọi ViewModel để bắt đầu quá trình xóa
                    artistViewModel.deleteArtist(artist);
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
                imageFile.delete();
                if (artistDirectory.list().length == 0) {
                    artistDirectory.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}