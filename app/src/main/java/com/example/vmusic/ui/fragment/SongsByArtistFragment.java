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

import com.example.vmusic.Interface.OnSongMenuClickListener;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.LibraryViewModel;
import com.example.vmusic.repository.PlaylistRepository;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.RecentlyPlayedManager;
import com.example.vmusic.service.PlaybackService;
import com.example.vmusic.ui.adapter.PlaylistDialogAdapter;
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
        imgArtistAvatar = view.findViewById(R.id.imgArtistAvatar);
        tvSongSummary = view.findViewById(R.id.tvSongSummary);
        btnBack = view.findViewById(R.id.btnBack);
        btnPlay = view.findViewById(R.id.btnPlay);
        recyclerView = view.findViewById(R.id.recyclerSongsByArtist);
        LibraryViewModel libraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);


        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        playlistRepository = new PlaylistRepository(requireActivity().getApplication());
        sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getUserId();

        tvArtistName.setText(artistName);
        Glide.with(requireContext()).load(artistImage).into(imgArtistBackground);
        Glide.with(requireContext()).load(artistImage).circleCrop().into(imgArtistAvatar);

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        btnPlay.setOnClickListener(v -> {
            if (artistSongs != null && !artistSongs.isEmpty()) {
                List<Song> shuffledList = new ArrayList<>(artistSongs);
                Collections.shuffle(shuffledList);

                Intent intent = new Intent(requireContext(), PlaybackService.class);
                intent.putExtra("song_list", new ArrayList<>(shuffledList));
                intent.putExtra("index", 0);
                requireContext().startService(intent);

                playerViewModel.playSong(shuffledList.get(0));
                songViewModel.increaseListenCount(shuffledList.get(0));
                new RecentlyPlayedManager(requireContext(), 1).addSongId(shuffledList.get(0).getSongId());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        songAdapter = new SongsByArtistAdapter(
                requireContext(),
                new ArrayList<>(),
                song -> {
                    List<Song> songList = songAdapter.getSongs();
                    int index = songList.indexOf(song);

                    Intent intent = new Intent(requireContext(), PlaybackService.class);
                    intent.putExtra("song_list", new ArrayList<>(songList));
                    intent.putExtra("index", index);
                    requireContext().startService(intent);

                    playerViewModel.playSong(song);
                    songViewModel.increaseListenCount(song);
                    new RecentlyPlayedManager(requireContext(), 1).addSongId(song.getSongId());
                },
                songViewModel,
                userId,
                new OnSongMenuClickListener() {
                    @Override
                    public void onAddToFavorite(Song song) {
                        songViewModel.addToFavorite(song.getSongId(), userId);
                        Toast.makeText(requireContext(), "Đã thêm \"" + song.getName() + "\" vào yêu thích", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAddToPlaylist(Song song) {
                        showAddToPlaylistDialog(song);
                    }
                }

        );


        recyclerView.setAdapter(songAdapter);

        if (artistId != -1) {
            songViewModel.getSongsByArtistId(artistId).observe(getViewLifecycleOwner(), songs -> {
                songAdapter.setSongs(songs);
                artistSongs = songs;
                tvSongSummary.setText(songs.size() + " bài hát • ");
            });
        }
    }

    private void showAddToPlaylistDialog(Song song) {
        int userId = sessionManager.getUserId();
        LibraryViewModel libraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        libraryViewModel.getPlaylistsByUser(userId).observe(getViewLifecycleOwner(), playlists -> {
            if (playlists == null || playlists.isEmpty()) {
                Toast.makeText(requireContext(), "Bạn chưa có playlist nào", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lọc bỏ playlist có type là "Favorite"
            List<Playlist> filteredPlaylists = new ArrayList<>();
            for (Playlist p : playlists) {
                if (p.getType() == null || !p.getType().equalsIgnoreCase("Favorite")) {
                    filteredPlaylists.add(p);
                }
            }

            if (filteredPlaylists.isEmpty()) {
                Toast.makeText(requireContext(), "Không có playlist nào hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo dialog custom
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_playlist_list, null);
            builder.setView(dialogView);

            RecyclerView rvPlaylists = dialogView.findViewById(R.id.rvPlaylists);
            rvPlaylists.setLayoutManager(new LinearLayoutManager(requireContext()));
            PlaylistDialogAdapter adapter = new PlaylistDialogAdapter(filteredPlaylists, playlist -> {
                libraryViewModel.addSongToPlaylist(song.getSongId(), playlist.getPlayListId());
                Toast.makeText(requireContext(), "Đã thêm vào \"" + playlist.getName() + "\"", Toast.LENGTH_SHORT).show();
            });
            rvPlaylists.setAdapter(adapter);

            builder.setNegativeButton("Hủy", null);
            builder.show();
        });
    }




}