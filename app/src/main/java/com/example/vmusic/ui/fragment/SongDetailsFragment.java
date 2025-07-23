package com.example.vmusic.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Song;
import com.example.vmusic.viewmodel.ArtistViewModel;
import com.example.vmusic.viewmodel.GenreViewModel;
import com.example.vmusic.viewmodel.SongViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class SongDetailsFragment extends Fragment {
    /// --- Biến Giao diện ---
    private ImageView imageCoverArt;
    private Button btnChooseImage, btnChooseAudio, btnChooseLyric, btnSave;
    private EditText editTitle;
    private TextView textAudioFileName, textLyricFileName;
    private ProgressBar progressBar;
    private LinearLayout genreContainer;
    private AutoCompleteTextView autocompleteArtist;
    // private ChipGroup chipGroupArtists; // <<--- XÓA BIẾN NÀY
    private MaterialToolbar toolbar;

    // --- Biến ViewModel ---
    private SongViewModel songViewModel;
    private GenreViewModel genreViewModel;
    private ArtistViewModel artistViewModel;

    // --- Biến Dữ liệu ---
    private Song songToEdit = null;
    private Uri imageUri, audioUri, lyricUri;
    private int songIdToEdit = -1;
    private final List<Integer> selectedGenreIds = new ArrayList<>();
    // <<--- THAY ĐỔI: Sử dụng một biến duy nhất cho nghệ sĩ được chọn
    private Artist selectedArtist = null;
    private List<Artist> allArtistsList = new ArrayList<>();

    // --- Trình khởi chạy Activity Result mới ---
    private ActivityResultLauncher<String> imagePickerLauncher, audioPickerLauncher, lyricPickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                Glide.with(requireContext()).load(uri).into(imageCoverArt);
            }
        });
        audioPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                audioUri = uri;
                textAudioFileName.setText(getFileName(uri));
            }
        });
        lyricPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                lyricUri = uri;
                textLyricFileName.setText(getFileName(uri));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            songIdToEdit = getArguments().getInt("songId", -1);
        }
        initViews(view);
        setupViewModels();
        setupButtons();
        observeGenres();
        setupArtistSearch();
        if (songIdToEdit != -1) {
            toolbar.setTitle("Chỉnh sửa bài hát");
            btnSave.setText("Lưu thay đổi");
            loadSongDataForEdit();
        }
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_song_details);
        imageCoverArt = view.findViewById(R.id.imageCoverArt);
        btnChooseImage = view.findViewById(R.id.btnUploadArtwork);
        btnChooseAudio = view.findViewById(R.id.btnUploadAudio);
        btnChooseLyric = view.findViewById(R.id.btnUploadLyric);
        btnSave = view.findViewById(R.id.savereleaseandaddsong);
        editTitle = view.findViewById(R.id.editTitle);
        textAudioFileName = view.findViewById(R.id.textAudioFileName);
        textLyricFileName = view.findViewById(R.id.textLyricFileName);
        progressBar = view.findViewById(R.id.progressBar);
        genreContainer = view.findViewById(R.id.genreContainer);
        autocompleteArtist = view.findViewById(R.id.autocomplete_artist);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void setupViewModels() {
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
    }

    private void setupButtons() {
        btnChooseImage.setOnClickListener(v -> pickFile(imagePickerLauncher, "image/*"));
        btnChooseAudio.setOnClickListener(v -> pickFile(audioPickerLauncher, "audio/*"));
        btnChooseLyric.setOnClickListener(v -> pickFile(lyricPickerLauncher, "*/*"));
        btnSave.setOnClickListener(v -> uploadAndSaveSong());
    }

    private void pickFile(ActivityResultLauncher<String> launcher, String type) {
        launcher.launch(type);
    }

    private void observeGenres() {
        genreViewModel.getAllGenres().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null) {
                genreContainer.removeAllViews();
                for (Genre genre : genres) {
                    CheckBox checkBox = new CheckBox(requireContext());
                    checkBox.setText(genre.name);
                    checkBox.setTextColor(getResources().getColor(R.color.white, null));
                    checkBox.setTag(genre.genreId);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        int genreId = (int) buttonView.getTag();
                        if (isChecked) {
                            selectedGenreIds.add(genreId);
                        } else {
                            selectedGenreIds.remove(Integer.valueOf(genreId));
                        }
                    });
                    genreContainer.addView(checkBox);
                }
                if (songToEdit != null) {
                    updateCheckboxes(selectedGenreIds);
                }
            }
        });
    }

    private void setupArtistSearch() {
        artistViewModel.getAllArtists().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                allArtistsList = artists;
                List<String> artistNames = new ArrayList<>();
                for (Artist artist : artists) {
                    artistNames.add(artist.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, artistNames);
                autocompleteArtist.setAdapter(adapter);
            }
        });

        autocompleteArtist.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Artist artist : allArtistsList) {
                if (artist.getName().equals(selectedName)) {
                    selectedArtist = artist;
                    autocompleteArtist.setText(artist.getName(), false);
                    break;
                }
            }
        });
    }

    private void loadSongDataForEdit() {
        songViewModel.getSongWithGenre(songIdToEdit).observe(getViewLifecycleOwner(), songWithGenres -> {
            if (songWithGenres != null && songWithGenres.song != null) {
                this.songToEdit = songWithGenres.song;
                editTitle.setText(songWithGenres.song.getName());
                File imageFile = new File(songWithGenres.song.getImage());
                if (imageFile.exists()) {
                    Glide.with(requireContext()).load(imageFile).into(imageCoverArt);
                }
                textAudioFileName.setText(new File(songWithGenres.song.getAudioUrl()).getName());
                textLyricFileName.setText(new File(songWithGenres.song.getUrlLyric()).getName());
                List<Integer> checkedGenreIds = new ArrayList<>();
                if (songWithGenres.genres != null) {
                    for (Genre genre : songWithGenres.genres) {
                        checkedGenreIds.add(genre.genreId);
                    }
                }
                selectedGenreIds.clear();
                selectedGenreIds.addAll(checkedGenreIds);
                updateCheckboxes(checkedGenreIds);
            }
        });

        songViewModel.getSongWithArtists(songIdToEdit).observe(getViewLifecycleOwner(), songWithArtists -> {
            if (songWithArtists != null && songWithArtists.artists != null && !songWithArtists.artists.isEmpty()) {
                selectedArtist = songWithArtists.artists.get(0);
                if (selectedArtist != null) {
                    autocompleteArtist.setText(selectedArtist.getName(), false);
                }
            }
        });
    }

    private void uploadAndSaveSong() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedArtist == null) {
            Toast.makeText(getContext(), "Vui lòng chọn một nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (songToEdit == null && (imageUri == null || audioUri == null)) {
            Toast.makeText(getContext(), "Vui lòng chọn file ảnh bìa và file nhạc", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        new Thread(() -> {
            try {
                List<Integer> finalArtistIds = Collections.singletonList(selectedArtist.getArtistId());

                if (songToEdit != null) {
                    songToEdit.setName(title);
                    songToEdit.setArtist(selectedArtist.getName());
                    File songFolder = new File(songToEdit.getAudioUrl()).getParentFile();
                    if (imageUri != null) {
                        new File(songToEdit.getImage()).delete();
                        songToEdit.setImage(copyFileToFolder(imageUri, songFolder, "cover." + getFileExtension(imageUri)));
                    }
                    if (audioUri != null) {
                        new File(songToEdit.getAudioUrl()).delete();
                        songToEdit.setAudioUrl(copyFileToFolder(audioUri, songFolder, "audio." + getFileExtension(audioUri)));
                    }
                    if (lyricUri != null) {
                        new File(songToEdit.getUrlLyric()).delete();
                        songToEdit.setUrlLyric(copyFileToFolder(lyricUri, songFolder, "lyric." + getFileExtension(lyricUri)));
                    }
                    songViewModel.updateSongWithRelationships(songToEdit, selectedGenreIds, finalArtistIds);
                } else { // CHẾ ĐỘ TẠO MỚI
                    String uniqueFolderName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date()) + "_" + UUID.randomUUID().toString().substring(0, 8);
                    File songFolder = new File(requireActivity().getFilesDir(), "songs/" + uniqueFolderName);
                    songFolder.mkdirs();
                    String imagePath = copyFileToFolder(imageUri, songFolder, "cover." + getFileExtension(imageUri));
                    String audioPath = copyFileToFolder(audioUri, songFolder, "audio." + getFileExtension(audioUri));
                    String lyricPath = (lyricUri != null) ? copyFileToFolder(lyricUri, songFolder, "lyric." + getFileExtension(lyricUri)) : "";
                    if (imagePath == null || audioPath == null) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Lưu file thất bại", Toast.LENGTH_SHORT).show());
                        songFolder.delete();
                        return;
                    }
                    Song newSong = new Song();
                    newSong.setName(title);
                    newSong.setArtist(selectedArtist.getName()); // Gán tên nghệ sĩ
                    newSong.setImage(imagePath);
                    newSong.setAudioUrl(audioPath);
                    newSong.setUrlLyric(lyricPath);
                    newSong.setListenCounts(0);
                    songViewModel.insertSongWithRelationships(newSong, selectedGenreIds, finalArtistIds);
                }
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lưu bài hát thành công!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(SongDetailsFragment.this).popBackStack();
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                });
            }
        }).start();
    }
    private void updateCheckboxes(List<Integer> checkedIds) {
        if (checkedIds == null || genreContainer == null) return;
        for (int i = 0; i < genreContainer.getChildCount(); i++) {
            View childView = genreContainer.getChildAt(i);
            if (childView instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) childView;
                Object tag = checkBox.getTag();
                if (tag instanceof Integer) {
                    int genreId = (Integer) tag;
                    checkBox.setChecked(checkedIds.contains(genreId));
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "unknown";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex);
                }
            }
        }
        return fileName;
    }

    private String copyFileToFolder(Uri uri, File targetFolder, String outputFileName) throws IOException {
        if (!targetFolder.exists()) targetFolder.mkdirs();
        File outFile = new File(targetFolder, outputFileName);
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[4096]; int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return outFile.getAbsolutePath();
    }

    private String getFileExtension(Uri uri) {
        String extension = null;
        if (uri.getScheme().equals("content")) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(requireContext().getContentResolver().getType(uri));
        }
        if (extension == null) {
            String path = uri.getPath();
            if (path != null && path.contains(".")) {
                extension = path.substring(path.lastIndexOf('.') + 1);
            }
        }
        return extension != null ? extension : "bin";
    }
}
