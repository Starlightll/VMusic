package com.example.vmusic.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.ui.adapter.SongAdapter;
import com.example.vmusic.viewmodel.SongViewModel;

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
    private SongAdapter songAdapter;
    private TextView tvArtistName;
    private ImageView imgArtistBackground;
    private ImageButton btnBack;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvArtistName = view.findViewById(R.id.tvArtistName);
        imgArtistBackground = view.findViewById(R.id.imgArtistBackground);
        btnBack = view.findViewById(R.id.btnBack);
        recyclerView = view.findViewById(R.id.recyclerSongsByArtist);

        // Set name and image
        tvArtistName.setText(artistName);
        Glide.with(requireContext()).load(artistImage).into(imgArtistBackground);

        // Back button
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter = new SongAdapter(song -> {
            // Xử lý khi nhấn bài hát (nếu cần)
        });
        recyclerView.setAdapter(songAdapter);


        // ViewModel
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        // Load songs by artist
        if (artistId != -1) {
            songViewModel.getSongsByArtistId(artistId).observe(getViewLifecycleOwner(), songs -> {
                songAdapter.setSongs(songs);
            });
        }
    }
}