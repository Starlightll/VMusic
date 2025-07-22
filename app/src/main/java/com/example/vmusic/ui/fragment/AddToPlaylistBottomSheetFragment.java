package com.example.vmusic.ui.fragment;

import android.app.Dialog;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vmusic.R;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.models.PlaylistWithSongs;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.ui.adapter.PlaylistSelectionAdapter;
import com.example.vmusic.viewmodel.PlaylistViewModel;
import com.example.vmusic.viewmodel.SongViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddToPlaylistBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddToPlaylistBottomSheetFragment extends BottomSheetDialogFragment {


    private int userId;
    private int songId;
    private SongWithArtists songToAdd;
    private SongViewModel songViewModel;

    private PlaylistSelectionAdapter playlistSelectionAdapter;


    public AddToPlaylistBottomSheetFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddToPlaylistBottomSheetFragment newInstance(int userId, int songId) {
        AddToPlaylistBottomSheetFragment fragment = new AddToPlaylistBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putInt("songId", songId);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnSongAddedToPlaylistListener {
        void onSongAddedToPlaylist(SongWithArtists song, Playlist targetPlaylist);
    }

    private OnSongAddedToPlaylistListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
            songId = getArguments().getInt("songId", -1);
        }

        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_playlist_bottom_sheet, container, false);

        TextView buttonCancel = view.findViewById(R.id.button_cancel);
        Button buttonNewPlaylist = view.findViewById(R.id.button_new_playlist);
        RecyclerView playlistsRecyclerView = view.findViewById(R.id.playlists_recyclerview);

        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        songViewModel.getSongWithArtists(songId).observe(getViewLifecycleOwner(), songWithArtists -> {
            if (songWithArtists != null) {
                songToAdd = songWithArtists;
            } else {
                Toast.makeText(getContext(), "Không tìm thấy bài hát", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistSelectionAdapter = new PlaylistSelectionAdapter(new PlaylistSelectionAdapter.OnPlaylistSelectedListener() {
            @Override
            public void onPlaylistSelected(PlaylistWithSongs playlist) {
                if (songToAdd != null) {
                    songViewModel.addSongsToPlaylist(playlist.playlist.getPlayListId(), Collections.singletonList(songToAdd));
                    Toast.makeText(getContext(), "Đã thêm \"" + songToAdd.song.getName() + "\" vào playlist \"" + playlist.playlist.getName() + "\"", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onSongAddedToPlaylist(songToAdd, playlist.playlist);
                    }
                    dismiss();
                }
            }
        });
        playlistsRecyclerView.setAdapter(playlistSelectionAdapter);



        songViewModel.getPlaylistsByTypeAndUser("playlist", userId).observe(getViewLifecycleOwner(), playlists -> {
            if (playlists != null) {
                playlistSelectionAdapter.setPlaylists(playlists);
            }
        });

        // Xử lý nút "Hủy"
        buttonCancel.setOnClickListener(v -> dismiss());

        // Xử lý nút "Danh sách phát mới"
        buttonNewPlaylist.setOnClickListener(v -> {
            // TODO: Mở một dialog hoặc fragment khác để tạo playlist mới


        });

        return view;
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
}