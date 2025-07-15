package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentLibraryTabBinding;
import com.example.vmusic.entity.Song;
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
        View view = inflater.inflate(R.layout.fragment_library_tab, container, false);
        binding = FragmentLibraryTabBinding.inflate(inflater, container, false);


        RecyclerView songRecyclerView = view.findViewById(R.id.songRecyclerView);
        RecyclerView playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);

        searchEditText = view.findViewById(R.id.searchEditText);
        genreSpinner = view.findViewById(R.id.genreSpinner);
        sortSpinner = view.findViewById(R.id.sortSpinner);

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

        // TODO: Spinner cho genre và sắp xếp

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSetting.setOnClickListener(v ->{
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_libraryTabFragment_to_settingsFragment)
            ;
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
