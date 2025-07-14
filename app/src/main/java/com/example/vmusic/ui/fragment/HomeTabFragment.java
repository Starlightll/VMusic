package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vmusic.R;
import com.example.vmusic.helper.RecentlyPlayedManager;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.service.PlaybackService;
import com.example.vmusic.viewmodel.PlayerViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ui.adapter.PopularSongAdapter;
import ui.adapter.RecentSongAdapter;
import ui.adapter.RecentlyPlayedAdapter;
import viewmodel.SongViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerRecent , recyclerPopular , recyclerRecentlyPlayed;
    RecentSongAdapter recentSongAdapter ;
    PopularSongAdapter  popularSongAdapter;
    RecentlyPlayedAdapter recentlyPlayedAdapter;

    String userName ="";

    private SongViewModel songViewModel;
    public HomeTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeTabFragment newInstance(String param1, String param2) {
        HomeTabFragment fragment = new HomeTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_tab, container, false);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager sessionManager = new SessionManager(requireContext());
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        tvGreeting.setText(getGreetingMessage());
        PlayerViewModel playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        int currentID = sessionManager.getUserId();
        RecentlyPlayedManager recentlyPlayedManager = new RecentlyPlayedManager(requireContext() , currentID);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        recyclerRecent = view.findViewById(R.id.recyclerRecent);

        recentSongAdapter = new RecentSongAdapter(requireContext(), new ArrayList<>(), song -> {
            Intent intent = new Intent(requireContext(), PlaybackService.class);
            intent.putExtra("url", song.getAudioUrl());
            intent.putExtra("name", song.getName());
            intent.putExtra("artist", song.getArtist());        // THÊM
            intent.putExtra("image", song.getImage());          // THÊM
            requireContext().startService(intent);

            // Cập nhật ViewModel để mini player hiển thị
            playerViewModel.setCurrentSong(song);
            playerViewModel.setIsPlaying(true);
            songViewModel.increaseListenCount(song);
            recentlyPlayedManager.addSongId(song.songId);
        });

        popularSongAdapter = new PopularSongAdapter(requireContext(), new ArrayList<>(), song -> {
            Intent intent = new Intent(requireContext(), PlaybackService.class);
            intent.putExtra("url", song.getAudioUrl());
            intent.putExtra("name", song.getName());
            intent.putExtra("artist", song.getArtist());        // THÊM
            intent.putExtra("image", song.getImage());          // THÊM
            requireContext().startService(intent);

            // Cập nhật ViewModel để mini player hiển thị
            playerViewModel.setCurrentSong(song);
            playerViewModel.setIsPlaying(true);
            songViewModel.increaseListenCount(song);
            recentlyPlayedManager.addSongId(song.songId);
        });

        recyclerRecent.setAdapter(recentSongAdapter);
        recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        songViewModel.getRecentSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                recentSongAdapter.setSongs(songs);
            }
        });


        recyclerPopular = view.findViewById(R.id.recyclerPopular);

        recyclerPopular.setAdapter(popularSongAdapter);

        recyclerPopular.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        songViewModel.getPopularSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                popularSongAdapter.setSongs(songs);
            }
        });

        recyclerRecentlyPlayed = view.findViewById(R.id.recyclerRecentlyPlayed);

        recentlyPlayedAdapter = new RecentlyPlayedAdapter(requireContext(), new ArrayList<>(), song -> {
            Intent intent = new Intent(requireContext(), PlaybackService.class);
            intent.putExtra("url", song.getAudioUrl());
            intent.putExtra("name", song.getName());
            intent.putExtra("artist", song.getArtist());
            intent.putExtra("image", song.getImage());
            requireContext().startService(intent);

            playerViewModel.setCurrentSong(song);
            playerViewModel.setIsPlaying(true);
            songViewModel.increaseListenCount(song);
            recentlyPlayedManager.addSongId(song.songId);
        });

        recyclerRecentlyPlayed.setAdapter(recentlyPlayedAdapter);
        recyclerRecentlyPlayed.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Integer> recentIds = recentlyPlayedManager.getSongIds();

        songViewModel.getSongsByIds(recentIds).observe(getViewLifecycleOwner(), songs -> {
            recentlyPlayedAdapter.setSongs(songs);
        });


    }
    private String getGreetingMessage() {
        SessionManager sessionManager = new SessionManager(requireContext());
        userName = sessionManager.getUsername();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Chào buổi sáng " + userName + "👋" ;
        } else if (hour >= 12 && hour < 17) {
            return "Chào buổi trưa " + userName + "👋";
        } else if (hour >= 17 && hour < 21) {
            return "Chào buổi chiều " + userName + "👋";
        } else {
            return "Chào buổi tối " + userName + "👋";
        }
    }
}