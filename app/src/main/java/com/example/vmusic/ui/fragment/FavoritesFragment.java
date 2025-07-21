package com.example.vmusic.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.ui.adapter.FavoriteSongAdapter;
import com.example.vmusic.viewmodel.FavoriteViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private FavoriteViewModel favoriteVM;
    private RecyclerView recyclerView;
    private FavoriteSongAdapter favoriteSongAdapter;
    private ImageView backButton;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoriteSongAdapter = new FavoriteSongAdapter(new FavoriteSongAdapter.OnItemClickListener() {
            @Override
            public void onSongClick(SongWithArtists song) {
                // Play bài hát
            }

            @Override
            public void onUnlikeClick(SongWithArtists song) {
                // Xóa bài hát khỏi danh sách yêu thích
            }
        });
        backButton = view.findViewById(R.id.backArrow);
        backButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(FavoritesFragment.this);
            navController.navigateUp();
        });
        recyclerView = view.findViewById(R.id.songsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(favoriteSongAdapter);

        userId = getCurrentUserId();
        favoriteVM = new FavoriteViewModel(requireActivity().getApplication(), userId);

        favoriteVM.getFavoritePlaylist().observe(getViewLifecycleOwner(), playlists -> {
            //TODO: Update adapter with the list of favorite playlists

        });

        // Initialize your UI components and observe LiveData from favoriteVM here
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