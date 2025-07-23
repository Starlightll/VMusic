package com.example.vmusic.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.example.vmusic.R;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.viewmodel.FavoriteViewModel;
import com.example.vmusic.viewmodel.PlaylistViewModel;
import com.example.vmusic.viewmodel.SongViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongOptionsBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongOptionsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_SONG = "song_with_artists";
    private SongWithArtists currentSong;
    private SongViewModel songViewModel;
    private boolean isLiked;
    private int userId;


    public SongOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    public interface OnOptionSelectedListener {
        void onAddToPlaylistSelected(SongWithArtists song);
        void onAddToQueueSelected(SongWithArtists song);
        void onViewArtistSelected(SongWithArtists song);
        void onLikedStatusChanged(SongWithArtists song, boolean newStatus);
    }

    private OnOptionSelectedListener listener;


    public static SongOptionsBottomSheetFragment newInstance(SongWithArtists song) {
        SongOptionsBottomSheetFragment fragment = new SongOptionsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG, song);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentSong = (SongWithArtists) getArguments().getSerializable(ARG_SONG);
        }
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_options_bottom_sheet, container, false);

        ImageView albumArt = view.findViewById(R.id.album_art);
        ImageView optionLikedIcon = view.findViewById(R.id.option_liked_icon);
        TextView optionLikedText = view.findViewById(R.id.option_liked_text);
        TextView songTitle = view.findViewById(R.id.song_title);
        TextView songArtistAlbum = view.findViewById(R.id.song_artist_album);
        LinearLayout optionLiked = view.findViewById(R.id.option_liked);
        LinearLayout optionAddToPlaylist = view.findViewById(R.id.option_add_to_playlist);
        LinearLayout optionViewArtist = view.findViewById(R.id.option_view_artist);
        Button buttonClose = view.findViewById(R.id.button_close);

        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        userId = getCurrentUserId();
        songViewModel.isSongIsInFavorite(currentSong.song.songId, userId).observe(getViewLifecycleOwner(), isFavorite -> {
            isLiked = isFavorite;
            if (isLiked) {
                optionLikedIcon.setImageResource(R.drawable.ic_favorite_full);
                optionLikedIcon.setColorFilter(getResources().getColor(R.color.primary));
                optionLikedText.setText("Đã thích");
                optionLikedText.setTextColor(getResources().getColor(R.color.primary));
            } else {
                optionLikedIcon.setImageResource(R.drawable.ic_favorite);
                optionLikedIcon.setColorFilter(getResources().getColor(android.R.color.white));
                optionLikedText.setText("Thêm vào yêu thích");
                optionLikedText.setTextColor(getResources().getColor(android.R.color.white));
            }
        });


        if (currentSong != null) {
            songTitle.setText(currentSong.song.getName());
            Glide.with(this)
                    .load(currentSong.song.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(albumArt);
            LinearLayout headerLayout = view.findViewById(R.id.song_info_container);
            if (currentSong.song.getImage() != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(currentSong.song.getImage())
                        .into(new com.bumptech.glide.request.target.SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Palette.from(resource).generate(palette -> {
                                    if (palette != null) {
                                        int vibrantColor = palette.getVibrantColor(Color.BLACK);
                                        int darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK);
                                        int[] colors = new int[]{
                                                darkVibrantColor,
                                                Color.TRANSPARENT
                                        };

                                        GradientDrawable gradientDrawable = new GradientDrawable(
                                                GradientDrawable.Orientation.TOP_BOTTOM,
                                                colors
                                        );
                                        headerLayout.setBackground(gradientDrawable);
                                        headerLayout.setBackground(gradientDrawable);
                                    }
                                });
                            }
                        });
            }

            optionLiked.setOnClickListener(v -> {
                if (!isLiked) {
                    isLiked = true;
                    songViewModel.addToFavorite(currentSong.song.getSongId(), userId);
                    optionLikedIcon.setImageResource(R.drawable.ic_favorite_full);
                    optionLikedIcon.setColorFilter(getResources().getColor(R.color.primary));
                    optionLikedText.setText("Đã thích");
                    optionLikedText.setTextColor(getResources().getColor(R.color.primary));
                } else {
                    Context context = getContext();
                    LayoutInflater inflaterDialog = LayoutInflater.from(context);
                    View customView = inflaterDialog.inflate(R.layout.custom_delete_dialog, null);

                    ImageView dialogIcon = customView.findViewById(R.id.dialog_icon);
                    TextView dialogTitle = customView.findViewById(R.id.dialog_title);
                    TextView dialogMessage = customView.findViewById(R.id.dialog_message);
                    Button buttonCancel = customView.findViewById(R.id.button_cancel);
                    Button buttonDelete = customView.findViewById(R.id.button_delete);

                    dialogTitle.setText("Xác nhận xóa");
                    dialogMessage.setText("Bạn có chắc chắn muốn xóa bài hát \"" + currentSong.song.getName() + "\" khỏi danh sách yêu thích không?");

                    AlertDialog dialog = new AlertDialog.Builder(context, R.style.TransparentDialogTheme)
                            .setView(customView)
                            .setCancelable(true)
                            .create();

                    buttonDelete.setOnClickListener(v1 -> {
                        isLiked = false;
                        songViewModel.removeFromFavorite(currentSong.song.getSongId(), userId);
                        Toast.makeText(context, "Đã xóa \"" + currentSong.song.getName() + "\" khỏi danh sách yêu thích.", Toast.LENGTH_SHORT).show();
                        optionLikedIcon.setImageResource(R.drawable.ic_favorite);
                        optionLikedIcon.setColorFilter(getResources().getColor(android.R.color.white));
                        optionLikedText.setText("Thêm vào yêu thích");
                        optionLikedText.setTextColor(getResources().getColor(android.R.color.white));
                        dialog.dismiss();
                    });

                    buttonCancel.setOnClickListener(v1 -> {
                        Toast.makeText(context, "Hủy bỏ thao tác xóa.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });

                    dialog.show();
                }
            });

            optionAddToPlaylist.setOnClickListener(v -> {
                AddToPlaylistBottomSheetFragment bottomSheet = AddToPlaylistBottomSheetFragment.newInstance(userId, currentSong.song.songId);
                bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
            });

            optionViewArtist.setOnClickListener(v -> {
                if (listener != null) listener.onViewArtistSelected(currentSong);
                dismiss();
            });
        }

        buttonClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setTitle("Tùy chọn bài hát");
        }



    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });
        return dialog;
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnOptionSelectedListener) {
            listener = (OnOptionSelectedListener) context;
        } else if (getParentFragment() instanceof OnOptionSelectedListener) {
            listener = (OnOptionSelectedListener) getParentFragment();
        } else {
            // throw new RuntimeException(context.toString() + " must implement OnOptionSelectedListener");
            // Không nhất thiết phải throw exception nếu không phải tất cả các fragment/activity đều cần lắng nghe
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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