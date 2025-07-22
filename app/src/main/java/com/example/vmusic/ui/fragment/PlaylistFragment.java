package com.example.vmusic.ui.fragment;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vmusic.R;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.ui.adapter.PlaylistSongAdapter;
import com.example.vmusic.ui.adapter.PlaylistSongAdapter;
import com.example.vmusic.viewmodel.FavoriteViewModel;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.PlaylistViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistFragment extends Fragment {

    private PlaylistViewModel playlistVM;
    private RecyclerView recyclerView;
    private PlaylistSongAdapter playlistSongAdapter;
    private PlayerViewModel playerVM;
    private SongViewModel songViewModel;
    private ImageView backButton;
    private TextView tvSongCount, songListenTime, playlistTitle;
    private ImageButton btnPlayAll;
    private EditText searchEditText;
    private Button addSongButton;
    private int userId;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int currentPlaylistId;
    private Playlist currentPlaylist;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistFragment newInstance(String param1, String param2) {
        PlaylistFragment fragment = new PlaylistFragment();
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
            currentPlaylistId = getArguments().getInt("playlistId", -1);
            currentPlaylist = (Playlist) getArguments().getSerializable("playlist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        recyclerView = view.findViewById(R.id.songsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvSongCount = view.findViewById(R.id.songCount);
        songListenTime = view.findViewById(R.id.songListenTime);
        playlistTitle = view.findViewById(R.id.playlistTitle);
        btnPlayAll = view.findViewById(R.id.playAllButton);
        searchEditText = view.findViewById(R.id.searchEditText);
        addSongButton = view.findViewById(R.id.addSongButton);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = getCurrentUserId();
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        playlistSongAdapter = new PlaylistSongAdapter(new PlaylistSongAdapter.OnItemClickListener() {
            @Override
            public void onSongClick(SongWithArtists song) {
                // Play bài hát
                List<Song> songs = new ArrayList<>();
                List<SongWithArtists> currentPlaylistSongs = playlistVM.getPlaylistSongs().getValue();
                if (currentPlaylistSongs != null) {
                    for (SongWithArtists s : currentPlaylistSongs) {
                        songs.add(s.song);
                    }
                    int songIndex = songs.indexOf(song.song);
                    if (songIndex != -1) {
                        playerVM.setPlaylist(songs, songIndex);
                        playerVM.play();
                    }
                }
            }

            @Override
            public void onUnlikeClick(SongWithArtists song) {
                SongOptionsBottomSheetFragment bottomSheet = SongOptionsBottomSheetFragment.newInstance(song);
                bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
            }
        });

        backButton = view.findViewById(R.id.backArrow);
        backButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PlaylistFragment.this);
            navController.navigateUp();
        });


        userId = getCurrentUserId();
        playlistVM = new PlaylistViewModel(requireActivity().getApplication(), currentPlaylistId);
        playerVM = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        if (currentPlaylist.playListId != -1) {
            playlistVM.getPlaylistSongs(currentPlaylist.playListId).observe(getViewLifecycleOwner(), playlist -> {
                //TODO: Update adapter with the list of favorite playlists
                if(playlist != null && !playlist.isEmpty()) {
                    playlistSongAdapter.setSongs(playlist);
                    recyclerView.setAdapter(playlistSongAdapter);
                    tvSongCount.setText(String.format("%d bài hát", playlist.size()));
                } else {
                    tvSongCount.setText(String.format("%d bài hát", 0));
                    playlistSongAdapter.setSongs(new ArrayList<>());
                }
                double totalListenTime = 0;
                for (SongWithArtists songWithArtist : playlist) {
                    try{
                        totalListenTime += songWithArtist.song.duration;
                    }
                    catch (Exception e) {
                       totalListenTime += 0;
                    }
                }

                int hours = (int) (totalListenTime / 3600);
                int minutes = (int) ((totalListenTime % 3600) / 60);
                int seconds = (int) (totalListenTime % 60);
                String listenTimeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                songListenTime.setText(listenTimeFormatted);
                playlistTitle.setText((currentPlaylist.getName() != null && !currentPlaylist.getName().isEmpty()) ? currentPlaylist.getName() : "Playlist");
            });
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy ID playlist", Toast.LENGTH_SHORT).show();
        }



        btnPlayAll.setOnClickListener(v -> {
            List<SongWithArtists> songsWithArtist = playlistVM.getPlaylistSongs().getValue();
            if (songsWithArtist == null || songsWithArtist.isEmpty()) {
                return;
            }
            List<Song> songs = new ArrayList<>();
            for (SongWithArtists songWithArtist : songsWithArtist) {
                songs.add(songWithArtist.song);
            }

            playerVM.setPlaylist(songs, 0);
            Song firstSong = songsWithArtist.get(0).song;
            playerVM.setCurrentSong(firstSong);
            playerVM.setIsPlaying(true);
            playerVM.play();
        });

        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<SongWithArtists> playlistSongs = playlistVM.getPlaylistSongs().getValue();
                if (playlistSongs == null) {
                    playlistSongs = new ArrayList<>();
                }
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    List<SongWithArtists> filteredSongs = new ArrayList<>();
                    for (SongWithArtists song : playlistSongs) {
                        if (song.song.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredSongs.add(song);
                        }
                    }
                    playlistSongAdapter.setSongs(filteredSongs);
                    TextView noResultsTextView = view.findViewById(R.id.tvMessage);
                    if (filteredSongs.isEmpty()) {
                        noResultsTextView.setVisibility(View.VISIBLE);
                        noResultsTextView.setText("Không tìm thấy bài hát nào");
                    } else {
                        noResultsTextView.setVisibility(View.GONE);
                    }
                } else {
                    playlistSongAdapter.setSongs(playlistVM.getPlaylistSongs().getValue());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        addSongButton.setOnClickListener(v -> {
            int currentPlaylistId = currentPlaylist.playListId;
            List<SongWithArtists> currentSongsInPlaylist = playlistVM.getPlaylistSongs().getValue();
            if (currentSongsInPlaylist == null) {
                currentSongsInPlaylist = new ArrayList<>();
            }
            SearchSongsBottomSheetFragment bottomSheet = SearchSongsBottomSheetFragment.newInstance(currentPlaylistId, currentSongsInPlaylist);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag()); // Sử dụng getChildFragmentManager()
        });

    }

    private int getCurrentUserId() {
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.isLoggedIn()) {
            return sessionManager.getUserId();
        } else {
            return -1;
        }
    }
}