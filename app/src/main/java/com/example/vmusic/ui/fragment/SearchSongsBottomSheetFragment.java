package com.example.vmusic.ui.fragment;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.ui.adapter.SearchSongAdapter;
import com.example.vmusic.viewmodel.PlaylistViewModel;
import com.example.vmusic.viewmodel.SongViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchSongsBottomSheetFragment  extends BottomSheetDialogFragment {
    private EditText searchEditText;
    private RecyclerView songsRecyclerView;
    private Button confirmButton;
    private SearchSongAdapter searchSongAdapter;
    private SongViewModel songViewModel;
    private int targetPlaylistId;
    private Playlist playlist;
    private List<SongWithArtists> currentSongsInPlaylist = new ArrayList<>();

    public interface OnSongsAddedListener {
        void onSongsAdded(List<SongWithArtists> addedSongs);
    }

    private OnSongsAddedListener onSongsAddedListener;

    public static SearchSongsBottomSheetFragment newInstance(int playlistId, List<SongWithArtists> currentSOngs) {
        SearchSongsBottomSheetFragment fragment = new SearchSongsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("playlistId", playlistId);
        args.putSerializable("currentSongsInPlaylist", (Serializable) (ArrayList<SongWithArtists>) currentSOngs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetPlaylistId = getArguments().getInt("playlistId", -1);
        }
        ArrayList<SongWithArtists> songs = (ArrayList<SongWithArtists>) getArguments().getSerializable("currentSongsInPlaylist");
        if (songs != null) {
            currentSongsInPlaylist = songs;
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                int desiredHeight = (int) (screenHeight * 0.90);

                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeight);
                } else {
                    layoutParams.height = desiredHeight;
                }
                bottomSheet.setLayoutParams(layoutParams);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_songs_bottom_sheet, container, false);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        searchEditText = view.findViewById(R.id.bottomSheetSearchEditText);
        songsRecyclerView = view.findViewById(R.id.bottomSheetSongsRecyclerView);
        confirmButton = view.findViewById(R.id.bottomSheetConfirmButton);

        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchSongAdapter = new SearchSongAdapter();
        songsRecyclerView.setAdapter(searchSongAdapter);
        searchSongAdapter.setSongsInCurrentPlaylist(currentSongsInPlaylist);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                songViewModel.searchSongsWithArtists(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        songViewModel.getSearchResults().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                searchSongAdapter.setSongs(songs);
                searchSongAdapter.setSongsInCurrentPlaylist(currentSongsInPlaylist);
            }
            else{
                searchSongAdapter.setSongs(new ArrayList<>());
            }
        });


        searchSongAdapter.setOnItemActionListener(new SearchSongAdapter.OnItemActionListener() {
            @Override
            public void onAddClick(SongWithArtists song) {
                songViewModel.addSongToSelection(song);
                Toast.makeText(getContext(), song.song.getName() + " đã được chọn.", Toast.LENGTH_SHORT).show();
                songViewModel.addSongsToPlaylist(targetPlaylistId, Collections.singletonList(song));
                currentSongsInPlaylist.add(song);
                searchSongAdapter.setSongsInCurrentPlaylist(currentSongsInPlaylist);
            }

            @Override
            public void onRemoveClick(SongWithArtists song) {
                Toast.makeText(getContext(), song.song.getName() + " đã có trong playlist. Click Confirm để thêm", Toast.LENGTH_SHORT).show();
            }
        });

        confirmButton.setOnClickListener(v -> {
            List<SongWithArtists> selectedSongs = songViewModel.getSelectedSongs().getValue();
            if (selectedSongs != null && !selectedSongs.isEmpty()) {
                songViewModel.addSongsToPlaylist(targetPlaylistId, selectedSongs);
                if (onSongsAddedListener != null) {
                    onSongsAddedListener.onSongsAdded(selectedSongs);
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một bài hát", Toast.LENGTH_SHORT).show();
            }
        });


        if (getParentFragment() instanceof OnSongsAddedListener) {
            onSongsAddedListener = (OnSongsAddedListener) getParentFragment();
        } else if (getActivity() instanceof OnSongsAddedListener) {
            onSongsAddedListener = (OnSongsAddedListener) getActivity();
        }
    }


}
