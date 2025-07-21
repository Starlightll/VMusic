package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.RecentlyPlayedManager;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.service.PlaybackService;
import com.example.vmusic.ui.adapter.ArtistAdapter;
import com.example.vmusic.ui.adapter.PopularSongAdapter;
import com.example.vmusic.ui.adapter.RecentSongAdapter;
import com.example.vmusic.ui.adapter.RecentlyPlayedAdapter;
import com.example.vmusic.viewmodel.ArtistViewModel;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeTabFragment extends Fragment {

    private RecyclerView recyclerRecent, recyclerPopular, recyclerRecentlyPlayed;
    private RecentSongAdapter recentSongAdapter;
    private PopularSongAdapter popularSongAdapter;
    private RecentlyPlayedAdapter recentlyPlayedAdapter;

    private SongViewModel songViewModel;

    private ArtistViewModel artistViewModel;
    private PlayerViewModel playerViewModel;
    private SessionManager sessionManager;
    private RecentlyPlayedManager recentlyPlayedManager;
    private RecyclerView recyclerArtists;
    private ArtistAdapter artistAdapter;

    private String userName = "";

    public HomeTabFragment() {}

    public static HomeTabFragment newInstance(String param1, String param2) {
        HomeTabFragment fragment = new HomeTabFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        int currentUserId = sessionManager.getUserId();
        recentlyPlayedManager = new RecentlyPlayedManager(requireContext(), currentUserId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_tab, container, false);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Greeting
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        tvGreeting.setText(getGreetingMessage());

        // ViewModels
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        // Setup UI
        setupRecyclerViews(view);
        setupAdapters();
        observeData();
    }

    private void setupRecyclerViews(View view) {
        recyclerRecent = view.findViewById(R.id.recyclerRecent);
        recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerPopular = view.findViewById(R.id.recyclerPopular);
        recyclerPopular.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        recyclerRecentlyPlayed = view.findViewById(R.id.recyclerRecentlyPlayed);
        recyclerRecentlyPlayed.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerArtists = view.findViewById(R.id.recyclerFavoriteArtists);
        recyclerArtists.setLayoutManager( new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupAdapters() {
        // Recent
        recentSongAdapter = new RecentSongAdapter(requireContext(), new ArrayList<>(), song -> {
            List<Song> songList = recentSongAdapter.getSongs();
            int startIndex = 0;
            for (int i = 0; i < songList.size(); i++) {
                if (songList.get(i).getSongId() == song.getSongId()) {
                    startIndex = i;
                    break;
                }
            }

//            ArrayList<Song> serializableList = new ArrayList<>(songList);
//            Intent intent = new Intent(requireContext(), PlaybackService.class);
//            intent.putExtra("song_list", serializableList);
//            intent.putExtra("index", startIndex);
//            requireContext().startService(intent);

//            playerViewModel.setCurrentSong(song);
//            playerViewModel.setIsPlaying(true);
            playerViewModel.playSong(song);
            songViewModel.increaseListenCount(song);
            recentlyPlayedManager.addSongId(song.getSongId());
        });
        recyclerRecent.setAdapter(recentSongAdapter);

        // Popular
        popularSongAdapter = new PopularSongAdapter(requireContext(), new ArrayList<>(), song -> {
            List<Song> songList = popularSongAdapter.getSongs();
            int startIndex = 0;
            for (int i = 0; i < songList.size(); i++) {
                if (songList.get(i).getSongId() == song.getSongId()) {
                    startIndex = i;
                    break;
                }
            }

            ArrayList<Song> serializableList = new ArrayList<>(songList);
            Intent intent = new Intent(requireContext(), PlaybackService.class);
            intent.putExtra("song_list", serializableList);
            intent.putExtra("index", startIndex);
            requireContext().startService(intent);

//            playerViewModel.setCurrentSong(song);
//            playerViewModel.setIsPlaying(true);
            playerViewModel.playSong(song);
            songViewModel.increaseListenCount(song);
            recentlyPlayedManager.addSongId(song.songId);
        });
        recyclerPopular.setAdapter(popularSongAdapter);

        // Recently Played
        recentlyPlayedAdapter = new RecentlyPlayedAdapter(requireContext(), new ArrayList<>(), song -> {
            List<Song> songList = recentlyPlayedAdapter.getSongs();
            int startIndex = 0;
            for (int i = 0; i < songList.size(); i++) {
                if (songList.get(i).getSongId() == song.getSongId()) {
                    startIndex = i;
                    break;
                }
            }

            ArrayList<Song> serializableList = new ArrayList<>(songList);
            Intent intent = new Intent(requireContext(), PlaybackService.class);
            intent.putExtra("song_list", serializableList);
            intent.putExtra("index", startIndex);
            requireContext().startService(intent);

//            playerViewModel.setCurrentSong(song);
//            playerViewModel.setIsPlaying(true);
            playerViewModel.playSong(song);
            songViewModel.increaseListenCount(song);
            recentlyPlayedManager.addSongId(song.songId);
        });
        recyclerRecentlyPlayed.setAdapter(recentlyPlayedAdapter);

        // Artists
        artistAdapter = new ArtistAdapter(requireContext(), new ArrayList<>(), artist -> {
            Bundle bundle = new Bundle();
            bundle.putInt("artistId", artist.getArtistId());
            bundle.putString("artistName", artist.getName());
            bundle.putString("artistImage", artist.getImage());
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_homeTabFragment_to_songsByArtistFragment,bundle);
        });

        recyclerArtists.setAdapter(artistAdapter);

    }

    private void observeData() {
        // Gần đây
        songViewModel.getRecentSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) recentSongAdapter.setSongs(songs);
        });

        // Phổ biến
        songViewModel.getPopularSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) popularSongAdapter.setSongs(songs);
        });

        // Đã nghe
        List<Integer> recentIds = recentlyPlayedManager.getSongIds();
        songViewModel.getSongsByIds(recentIds).observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) recentlyPlayedAdapter.setSongs(songs);
        });

        // Nghệ sĩ
        artistViewModel.getAllArtists().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                artistAdapter.setArtists(artists);
                if (artists.isEmpty()) {
                    recyclerArtists.setVisibility(View.GONE);
                } else {
                    recyclerArtists.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String getGreetingMessage() {
        userName = sessionManager.getUsername() != null ? sessionManager.getUsername() : "";
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Chào buổi sáng " + userName + " 👋";
        } else if (hour >= 12 && hour < 17) {
            return "Chào buổi trưa " + userName + " 👋";
        } else if (hour >= 17 && hour < 21) {
            return "Chào buổi chiều " + userName + " 👋";
        } else {
            return "Chào buổi tối " + userName + " 👋";
        }
    }
}
