package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vmusic.R;
import com.example.vmusic.ui.adapter.ListSongAdapter;
import com.example.vmusic.viewmodel.PlayerViewModel;

import java.util.ArrayList;
import java.util.List;

import entity.Song;
import models.PlayerManager;
import ui.activity.PlaySongActivity;
import ui.adapter.PopularSongAdapter;
import ui.adapter.RecentSongAdapter;
import viewmodel.SongViewModel;


public class ListSongFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListSongAdapter listSongAdapter;
    private SongViewModel songViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_song, container, false);

        recyclerView = view.findViewById(R.id.rec_song);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        PlayerViewModel playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        listSongAdapter = new ListSongAdapter(requireContext(), new ArrayList<>(), song -> {

            ExoPlayer player = PlayerManager.getPlayer(requireContext());
            player.setMediaItem(MediaItem.fromUri(song.getAudioUrl()));
            player.prepare();
            player.play();


            Intent intent = new Intent(requireContext(), PlaySongActivity.class);
            intent.putExtra("song", song);
            requireContext().startActivity(intent);
        });


        recyclerView.setAdapter(listSongAdapter);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                listSongAdapter.setSongs(songs);
            }
        });

        return view;
    }
}
