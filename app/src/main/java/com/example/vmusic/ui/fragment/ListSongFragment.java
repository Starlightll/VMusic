package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.PlayerManager;
import com.example.vmusic.ui.adapter.ListSongAdapter;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.ArrayList;

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

        // ViewModel
        PlayerViewModel playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        // Lấy userId từ Session
        SessionManager session = new SessionManager(requireContext());
        int userId = session.getUserId();

        // Load danh sách yêu thích từ DB
        songViewModel.loadFavoriteSongs(userId);

        // Adapter
        listSongAdapter = new ListSongAdapter(
                requireContext(),
                new ArrayList<>(),
                song -> {
                    ExoPlayer player = PlayerManager.getPlayer(requireContext());
                    player.setMediaItem(MediaItem.fromUri(song.getAudioUrl()));
                    player.prepare();
                    player.play();

                    playerViewModel.setCurrentSong(song);
                },
                songViewModel,
                userId
        );

        recyclerView.setAdapter(listSongAdapter);

        // Gọi observer để theo dõi danh sách yêu thích và tự động cập nhật UI trái tim
        listSongAdapter.initFavoriteObserver();

        // Observer danh sách bài hát
        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                listSongAdapter.setSongs(songs);
            }
        });

        return view;
    }
}
