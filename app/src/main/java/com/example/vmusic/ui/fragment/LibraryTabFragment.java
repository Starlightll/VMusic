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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentLibraryTabBinding;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.LibraryViewModel;
import com.example.vmusic.ui.adapter.PlaylistAdapter;
import com.example.vmusic.ui.adapter.SongAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LibraryTabFragment extends Fragment {
    private LibraryViewModel viewModel;
    private SongAdapter songAdapter;
    private PlaylistAdapter playlistAdapter;

    private EditText searchEditText;
    private Spinner genreSpinner, sortSpinner;

    private List<Song> currentSongs = new ArrayList<>();

    private FragmentLibraryTabBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Chỉ dùng binding thôi
        binding = FragmentLibraryTabBinding.inflate(inflater, container, false);

        // Setup RecyclerView từ binding
        RecyclerView songRecyclerView = binding.songRecyclerView;
        RecyclerView playlistRecyclerView = binding.playlistRecyclerView;

        searchEditText = binding.searchEditText;
        genreSpinner = binding.genreSpinner;
        sortSpinner = binding.sortSpinner;

        // Song setup
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(song -> {
            // TODO: Mở chi tiết bài hát
        });
        songRecyclerView.setAdapter(songAdapter);

        // Playlist setup
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        playlistAdapter = new PlaylistAdapter(playlist -> {
            // TODO: Mở chi tiết playlist
        });
        playlistRecyclerView.setAdapter(playlistAdapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

        // Quan sát danh sách bài hát
        viewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            currentSongs = songs;
            filterAndSortSongs();
        });

        // Quan sát danh sách playlist
        viewModel.getAllPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            playlistAdapter.setPlaylists(playlists);
        });

        // Tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndSortSongs();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return binding.getRoot(); // ✅ Dùng layout từ binding
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSetting.setOnClickListener(v ->{
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_libraryTabFragment_to_settingsFragment)
            ;
        });


        binding.btnAddPlaylist.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Tạo Playlist mới");

            final EditText input = new EditText(requireContext());
            input.setHint("Tên Playlist");
            builder.setView(input);

            builder.setPositiveButton("Tạo", (dialog, which) -> {
                try {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        SessionManager session = new SessionManager(requireContext());
                        int userId = session.getUserId();

                        // Không cho phép tạo nếu chưa đăng nhập
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi khi tạo playlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });





//        binding.btnSetting.setOnClickListener(v -> {
//            NavController navController = NavHostFragment.findNavController(this);
//            navController.navigate(R.id.action_libraryTabFragment_to_settingsFragment, null, new NavOptions.Builder()
//                    .setLaunchSingleTop(true)
//                    .setRestoreState(true)
//                    .setEnterAnim(R.anim.slide_in_right)
//                    .setExitAnim(R.anim.slide_out_left)
//                    .build());
//        });
    }

    private void filterAndSortSongs() {
        String query = searchEditText.getText().toString().toLowerCase(Locale.ROOT);
        List<Song> filtered = new ArrayList<>();
        for (Song song : currentSongs) {
            if (song.getName().toLowerCase().contains(query) ||
                    song.getArtist().toLowerCase().contains(query)) {
                filtered.add(song);
            }
        }

        // TODO: Apply lọc theo thể loại và sắp xếp ở đây

        songAdapter.setSongs(filtered);
    }
}
