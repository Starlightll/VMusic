package com.example.vmusic.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentLibraryTabBinding;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.LibraryViewModel;
import com.example.vmusic.ui.adapter.PlaylistAdapter;
import com.example.vmusic.ui.adapter.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LibraryTabFragment extends Fragment {
    private LibraryViewModel viewModel;
    private SongAdapter songAdapter;
    private PlaylistAdapter playlistAdapter;
    private ImageView btnSetting;

    private EditText searchEditText;
    private Spinner genreSpinner, sortSpinner;

    private List<Song> currentSongs = new ArrayList<>();
    private List<Playlist> currentPlaylists = new ArrayList<>();
    private FragmentLibraryTabBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLibraryTabBinding.inflate(inflater, container, false);

        btnSetting = binding.btnSetting;
        SessionManager sessionManager = new SessionManager(requireContext());
        Glide.with(this)
                .load(sessionManager.getUserAvatar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(btnSetting);

        // Khởi tạo RecyclerView
        RecyclerView songRecyclerView = binding.songRecyclerView;
        RecyclerView playlistRecyclerView = binding.playlistRecyclerView;
        sortSpinner = binding.sortSpinner;

        // Song RecyclerView setup
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(song -> {
            // TODO: Mở chi tiết bài hát
        });
        songRecyclerView.setAdapter(songAdapter);

        // Playlist RecyclerView setup
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // ✅ Gán sự kiện click và giữ lâu cho Playlist
        playlistAdapter = new PlaylistAdapter(new PlaylistAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(Playlist playlist) {
                if(playlist.getType().equalsIgnoreCase("playlist")) {
                    //TODO: Mở playlist cá nhân
                    // Chuyển đến PlaylistFragment với ID của playlist
                    Bundle bundle = new Bundle();
                    bundle.putInt("playlistId", playlist.getPlayListId());
                    bundle.putSerializable("playlist", playlist);
                    NavController navController = NavHostFragment.findNavController(LibraryTabFragment.this);
                    navController.navigate(R.id.action_libraryTabFragment_to_playlistFragment, bundle);
                }else if(playlist.getType().equalsIgnoreCase("favorite")) {
                    //TODO: Mở playlist yêu thích
                    Bundle bundle = new Bundle();
                    bundle.putInt("playlistId", playlist.getPlayListId());
                    bundle.putSerializable("playlist", playlist);
                    NavController navController = NavHostFragment.findNavController(LibraryTabFragment.this);
                    navController.navigate(R.id.action_libraryTabFragment_to_favoritesFragment, bundle);
                }else if(playlist.getType().equalsIgnoreCase("system")) {
                    //TODO: Mở playlist hệ thống
                }else{
                    Toast.makeText(requireContext(), "Playlist không hợp lệ", Toast.LENGTH_SHORT).show();
                    Log.e("LibraryTabFragment", "Invalid playlist type: " + playlist.getType());
                }
            }

            @Override
            public void onPlaylistLongClick(Playlist playlist) {
                showPlaylistOptionsDialog(playlist); // Hiển thị dialog sửa hoặc xoá
            }
        });
        playlistRecyclerView.setAdapter(playlistAdapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);


        // ✅ Lấy danh sách playlist theo userId (chỉ hiển thị playlist của người dùng đăng nhập)
        SessionManager session = new SessionManager(requireContext());
        int userId = session.getUserId();

        if (userId != -1) {
            viewModel.getPlaylistsByUser(userId).observe(getViewLifecycleOwner(), playlists -> {
                currentPlaylists = playlists;
                filterAndSortPlaylists();
            });
        } else {
            Toast.makeText(requireContext(), "Bạn cần đăng nhập để xem playlist.", Toast.LENGTH_SHORT).show();
        }

        // Tìm kiếm
        EditText searchBox = binding.etLibrarySearch;
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterLibrary(s.toString().trim().toLowerCase(Locale.ROOT));
            }
        });

        // Sắp xếp
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Tất cả");
        sortOptions.add("Mới thêm gần đây");
        sortOptions.add("Tên (A-Z)");
        sortOptions.add("Tên (Z-A)");

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(0);
        sortSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterAndSortPlaylists();
            }

            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Chuyển sang màn settings
        binding.btnSetting.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_libraryTabFragment_to_settingsFragment);
        });

        // Tạo playlist mới
        binding.btnAddPlaylist.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Tạo Playlist mới");

            final EditText input = new EditText(requireContext());
            input.setHint("Tên Playlist");
            builder.setView(input);

            builder.setPositiveButton("Tạo", (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    SessionManager session = new SessionManager(requireContext());
                    int userId = session.getUserId();

                    if (userId == -1) {
                        Toast.makeText(requireContext(), "Bạn cần đăng nhập để tạo playlist.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Playlist newPlaylist = new Playlist(0, name, "playlist", userId);
                    viewModel.insertPlaylist(newPlaylist);
                    Toast.makeText(requireContext(), "Tạo playlist thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Tên playlist không được để trống.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    // ✅ Hiển thị menu sửa / xoá khi giữ lâu playlist
    private void showPlaylistOptionsDialog(Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tùy chọn playlist")
                .setItems(new CharSequence[]{"Đổi tên", "Xoá"}, (dialog, which) -> {
                    if (which == 0) {
                        showRenamePlaylistDialog(playlist);
                    } else if (which == 1) {
                        showDeleteConfirmationDialog(playlist);
                    }
                })
                .show();
    }

    // ✅ Dialog đổi tên playlist
    private void showRenamePlaylistDialog(Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đổi tên Playlist");

        final EditText input = new EditText(requireContext());
        input.setText(playlist.getName());
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                playlist.setName(newName);
                viewModel.updatePlaylist(playlist);
                Toast.makeText(requireContext(), "Đã đổi tên", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // ✅ Dialog xác nhận xoá playlist
    private void showDeleteConfirmationDialog(Playlist playlist) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá Playlist")
                .setMessage("Bạn có chắc muốn xoá playlist \"" + playlist.getName() + "\"?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    viewModel.deletePlaylist(playlist);
                    Toast.makeText(requireContext(), "Đã xoá playlist", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Lọc theo tìm kiếm
    private void filterLibrary(String query) {
        List<Song> filteredSongs = new ArrayList<>();
        List<Playlist> filteredPlaylists = new ArrayList<>();

        if (query.isEmpty()) {
            filteredSongs.addAll(currentSongs);
            filteredPlaylists.addAll(currentPlaylists);
        } else {
            String[] keywords = query.split("\\s+");

            for (Song song : currentSongs) {
                String name = song.getName().toLowerCase();
                String artist = song.getArtist().toLowerCase();
                for (String keyword : keywords) {
                    if (name.contains(keyword) || artist.contains(keyword)) {
                        filteredSongs.add(song);
                        break;
                    }
                }
            }

            for (Playlist playlist : currentPlaylists) {
                String name = playlist.getName().toLowerCase();
                for (String keyword : keywords) {
                    if (name.contains(keyword)) {
                        filteredPlaylists.add(playlist);
                        break;
                    }
                }
            }
        }

        songAdapter.setSongs(filteredSongs);
        playlistAdapter.setPlaylists(filteredPlaylists);
    }

    // Lọc và sắp xếp playlist
    private void filterAndSortPlaylists() {
        String sortOption = sortSpinner.getSelectedItem() != null ? sortSpinner.getSelectedItem().toString() : "";

        List<Playlist> filtered = new ArrayList<>(currentPlaylists);

        switch (sortOption) {
            case "Mới thêm gần đây":
                Collections.sort(filtered, (a, b) -> Integer.compare(b.playListId, a.playListId));
                break;
            case "Tên (A-Z)":
                Collections.sort(filtered, Comparator.comparing(Playlist::getName));
                break;
            case "Tên (Z-A)":
                Collections.sort(filtered, (a, b) -> b.getName().compareTo(a.getName()));
                break;
        }

        playlistAdapter.setPlaylists(filtered);
    }
}