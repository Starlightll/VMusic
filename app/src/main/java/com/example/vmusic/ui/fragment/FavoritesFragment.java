package com.example.vmusic.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.ui.adapter.FavoriteSongAdapter;
import com.example.vmusic.viewmodel.FavoriteViewModel;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.PlaylistViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private FavoriteViewModel favoriteVM;
    private RecyclerView recyclerView;
    private FavoriteSongAdapter favoriteSongAdapter;
    private PlayerViewModel playerVM;
    private PlaylistViewModel playlistVM;
    private ImageView backButton;
    private TextView tvSongCount;
    private EditText searchEditText;
    private ImageButton btnPlayAll;
    private Button addSongButton;
    private int playlistId;
    private Playlist playlist;
    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
            playlistId = getArguments().getInt("playlistId", -1);
            playlist = (Playlist) getArguments().getSerializable("playlist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.songsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvSongCount = view.findViewById(R.id.songCount);
        searchEditText = view.findViewById(R.id.searchEditText);
        btnPlayAll = view.findViewById(R.id.playAllButton);
        addSongButton = view.findViewById(R.id.addSongButton);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoriteSongAdapter = new FavoriteSongAdapter(new FavoriteSongAdapter.OnItemClickListener() {
            @Override
            public void onSongClick(SongWithArtists song) {
                // Play bài hát
                List<Song> songs = new ArrayList<>();
                List<SongWithArtists> currentFavoriteSongs = favoriteVM.getFavoritePlaylist().getValue();
                if (currentFavoriteSongs != null) {
                    for (SongWithArtists s : currentFavoriteSongs) {
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
                // Xóa bài hát khỏi danh sách yêu thích
                // Lấy Context từ View của Fragment/Activity
                // (Nếu bạn đang ở trong Fragment, hãy dùng requireContext() hoặc getContext())
                // (Nếu bạn đang ở trong Activity, hãy dùng 'this' hoặc 'YourActivity.this')
                android.content.Context context = getContext();

                LayoutInflater inflater = LayoutInflater.from(context);
                View customView = inflater.inflate(R.layout.custom_delete_dialog, null);

                ImageView dialogIcon = customView.findViewById(R.id.dialog_icon);
                TextView dialogTitle = customView.findViewById(R.id.dialog_title);
                TextView dialogMessage = customView.findViewById(R.id.dialog_message);
                Button buttonCancel = customView.findViewById(R.id.button_cancel);
                Button buttonDelete = customView.findViewById(R.id.button_delete);

                dialogTitle.setText("Xác nhận xóa");
                dialogMessage.setText("Bạn có chắc chắn muốn xóa bài hát \"" + song.song.getName() + "\" khỏi danh sách yêu thích không?");

                AlertDialog dialog = new AlertDialog.Builder(context, R.style.TransparentDialogTheme)
                        .setView(customView) // Đặt layout tùy chỉnh vào dialog
                        .setCancelable(true) // Cho phép hủy bằng cách chạm ra ngoài hoặc nút Back
                        .create(); // Tạo dialog

                buttonDelete.setOnClickListener(v -> {
                    favoriteVM.removeSongFromFavorite(song.song.songId, userId);
                    Toast.makeText(context, "Đã xóa \"" + song.song.getName() + "\" khỏi danh sách yêu thích.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                buttonCancel.setOnClickListener(v -> {
                    Toast.makeText(context, "Hủy bỏ thao tác xóa.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                dialog.show();
            }
        });

        backButton = view.findViewById(R.id.backArrow);
        backButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(FavoritesFragment.this);
            navController.navigateUp();
        });


        userId = getCurrentUserId();
        favoriteVM = new FavoriteViewModel(requireActivity().getApplication(), userId);
        playerVM = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        favoriteVM.getFavoritePlaylist().observe(getViewLifecycleOwner(), playlist -> {
            //TODO: Update adapter with the list of favorite playlists
            if(playlist != null && !playlist.isEmpty()) {
                favoriteSongAdapter.setSongs(playlist);
                recyclerView.setAdapter(favoriteSongAdapter);
                tvSongCount.setText(String.format("%d bài hát", playlist.size()));
            } else {
                // Handle empty playlist case
                tvSongCount.setText(String.format("%d bài hát", 0));
                favoriteSongAdapter.setSongs(new ArrayList<>());
            }
        });

        btnPlayAll.setOnClickListener(v -> {
            List<SongWithArtists> songsWithArtist = favoriteVM.getFavoritePlaylist().getValue();
            if (songsWithArtist == null || songsWithArtist.isEmpty()) {
                return;
            }
            List<Song> songs = new ArrayList<>();
            for (SongWithArtists songWithArtist : songsWithArtist) {
                songs.add(songWithArtist.song);
            }

            playerVM.setPlaylist(songs, 0);
            Song firstSong = favoriteVM.getFavoritePlaylist().getValue().get(0).song;
            playerVM.setCurrentSong(firstSong);
            playerVM.setIsPlaying(true);
            playerVM.play();
        });

        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<SongWithArtists> favoriteSongs = favoriteVM.getFavoritePlaylist().getValue();
                if (favoriteSongs == null) {
                    favoriteSongs = new ArrayList<>();
                }
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    List<SongWithArtists> filteredSongs = new ArrayList<>();
                    for (SongWithArtists song : favoriteSongs) {
                        if (song.song.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredSongs.add(song);
                        }
                    }
                    favoriteSongAdapter.setSongs(filteredSongs);
                    TextView noResultsTextView = view.findViewById(R.id.tvMessage);
                    if (filteredSongs.isEmpty()) {
                        noResultsTextView.setVisibility(View.VISIBLE);
                        noResultsTextView.setText("Không tìm thấy bài hát nào");
                    } else {
                        noResultsTextView.setVisibility(View.GONE);
                    }
                } else {
                    favoriteSongAdapter.setSongs(favoriteVM.getFavoritePlaylist().getValue());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        addSongButton.setOnClickListener(v -> {
            int favoritePlaylistId = playlistId;
            List<SongWithArtists> currentSongsInPlaylist = favoriteVM.getFavoritePlaylist().getValue();
            if (currentSongsInPlaylist == null) {
                currentSongsInPlaylist = new ArrayList<>();
            }
            SearchSongsBottomSheetFragment bottomSheet = SearchSongsBottomSheetFragment.newInstance(favoritePlaylistId, currentSongsInPlaylist);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag()); // Sử dụng getChildFragmentManager()
        });

    }

    private int getCurrentUserId() {
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.isLoggedIn()) {
            return sessionManager.getUserId();
        } else {
            // Handle the case where the user is not logged in
            return -1; // or throw an exception, or return a default value
        }
    }
}