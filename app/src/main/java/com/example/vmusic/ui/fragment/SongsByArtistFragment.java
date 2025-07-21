package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.PopupMenu;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.repository.PlaylistRepository;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.RecentlyPlayedManager;
import com.example.vmusic.service.PlaybackService;
import com.example.vmusic.ui.adapter.SongAdapter;
import com.example.vmusic.ui.adapter.SongsByArtistAdapter;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // Thêm import cho shuffle

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsByArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsByArtistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int artistId;
    private String artistName;
    private String artistImage;
    private SongViewModel songViewModel;
    private RecyclerView recyclerView;
    private SongsByArtistAdapter songAdapter;
    private TextView tvArtistName;
    private ImageView imgArtistBackground;
    private ImageView imgArtistAvatar; // Avatar tròn nhỏ
    private TextView tvSongSummary; // TextView tổng quan
    private ImageButton btnBack;
    private ImageButton btnPlay; // Thêm biến cho nút play
    private ImageButton btnMore; // Thêm biến cho nút ba chấm
    private PlayerViewModel playerViewModel;
    private List<Song> artistSongs = new ArrayList<>(); // Lưu danh sách bài hát nghệ sĩ
    private PlaylistRepository playlistRepository;
    private SessionManager sessionManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SongsByArtistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsByArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongsByArtistFragment newInstance(String param1, String param2) {
        SongsByArtistFragment fragment = new SongsByArtistFragment();
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
            artistId = getArguments().getInt("artistId");
            artistName = getArguments().getString("artistName");
            artistImage = getArguments().getString("artistImage");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_songs_by_artist, container, false);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvArtistName = view.findViewById(R.id.tvArtistName);
        imgArtistBackground = view.findViewById(R.id.imgArtistBackground);
        imgArtistAvatar = view.findViewById(R.id.imgArtistAvatar); // Avatar tròn nhỏ
        tvSongSummary = view.findViewById(R.id.tvSongSummary); // Tổng quan
        btnBack = view.findViewById(R.id.btnBack);
        btnPlay = view.findViewById(R.id.btnPlay); // Ánh xạ nút play
        recyclerView = view.findViewById(R.id.recyclerSongsByArtist);
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        btnMore = view.findViewById(R.id.btnMenu);
        playlistRepository = new PlaylistRepository(requireActivity().getApplication());
        sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getUserId();

        // Set name and image
        tvArtistName.setText(artistName);
        Glide.with(requireContext()).load(artistImage).into(imgArtistBackground);
        Glide.with(requireContext()).load(artistImage).circleCrop().into(imgArtistAvatar); // Avatar tròn

        // Back button
        btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // play button
        btnPlay.setOnClickListener(v -> {
            if (artistSongs != null && !artistSongs.isEmpty()) {
                List<Song> shuffledList = new ArrayList<>(artistSongs);
                Collections.shuffle(shuffledList);

                Intent intent = new Intent(requireContext(), PlaybackService.class);
                intent.putExtra("song_list", new ArrayList<>(shuffledList));
                intent.putExtra("index", 0);
                requireContext().startService(intent);

                // Cập nhật ViewModel
                playerViewModel.setCurrentSong(shuffledList.get(0));
                playerViewModel.setIsPlaying(true);
                songViewModel.increaseListenCount(shuffledList.get(0));
                new RecentlyPlayedManager(requireContext(), 1).addSongId(shuffledList.get(0).getSongId());
            }
        });

        // Setup RecyclerViewS
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        // Truyền thêm songViewModel và userId vào Adapter
        songAdapter = new SongsByArtistAdapter(requireContext(), new ArrayList<>(), song -> {
            List<Song> songList = songAdapter.getSongs();
            int index = songList.indexOf(song);

            Intent intent = new Intent(requireContext(), PlaybackService.class);
            intent.putExtra("song_list", new ArrayList<>(songList));
            intent.putExtra("index", index);
            requireContext().startService(intent);

            playerViewModel.setCurrentSong(song);
            playerViewModel.setIsPlaying(true);
            songViewModel.increaseListenCount(song);
            new RecentlyPlayedManager(requireContext(), 1).addSongId(song.getSongId());
        }, songViewModel, userId);


        recyclerView.setAdapter(songAdapter);


        // ViewModel
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        // Load songs by artist
        if (artistId != -1) {
            songViewModel.getSongsByArtistId(artistId).observe(getViewLifecycleOwner(), songs -> {
                songAdapter.setSongs(songs);
                artistSongs = songs;
                int totalSongs = songs.size();
                tvSongSummary.setText(totalSongs + " bài hát • ");
            });
        }
    }
}