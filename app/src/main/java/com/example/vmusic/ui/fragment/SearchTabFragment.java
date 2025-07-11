package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;

import java.util.ArrayList;
import java.util.List;


import entity.Genre;
import entity.Song;
import ui.adapter.GenreGridAdapter;
import ui.adapter.SongAdapter;
import viewmodel.GenreViewModel;
import viewmodel.SongViewModel;

public class SearchTabFragment extends Fragment {

    private GenreViewModel genreViewModel;
    private SongViewModel songViewModel;
    private GenreGridAdapter genreAdapter;
    private SongAdapter songAdapter;

    private List<Genre> fullGenreList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tab, container, false);

        // Genre RecyclerView
        RecyclerView genreRecyclerView = view.findViewById(R.id.rv_genres);
        genreRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        genreAdapter = new GenreGridAdapter(getContext(), new ArrayList<>());
        genreRecyclerView.setAdapter(genreAdapter);

        // Song RecyclerView
        RecyclerView songRecyclerView = view.findViewById(R.id.rv_songs);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(song -> {
            Toast.makeText(getContext(), "Clicked: " + song.name, Toast.LENGTH_SHORT).show();
        });

        songRecyclerView.setAdapter(songAdapter);

        // ViewModels
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        // Load genres
        genreViewModel.getAllGenres().observe(getViewLifecycleOwner(), new Observer<List<Genre>>() {
            @Override
            public void onChanged(List<Genre> genres) {
                fullGenreList = genres;
                genreAdapter.setGenres(genres);
            }
        });

        // Search EditText
        AppCompatEditText searchEditText = view.findViewById(R.id.et_search);
        searchEditText.setFocusableInTouchMode(true);
        searchEditText.requestFocus();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    filterGenres(query);
                    songViewModel.searchSongs(query).observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
                        @Override
                        public void onChanged(List<Song> songs) {
                            songAdapter.setSongs(songs);
                        }
                    });
                } else {
                    genreAdapter.setGenres(fullGenreList);
                    songAdapter.setSongs(new ArrayList<>());
                }
            }
        });

        return view;
    }

    private void filterGenres(String query) {
        List<Genre> filtered = new ArrayList<>();
        for (Genre genre : fullGenreList) {
            if (genre.name.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(genre);
            }
        }
        genreAdapter.setGenres(filtered);
    }
}
