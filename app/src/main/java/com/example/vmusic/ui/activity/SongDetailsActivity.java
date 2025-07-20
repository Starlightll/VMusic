package com.example.vmusic.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Song;
import com.example.vmusic.models.SongWithArtists;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SongDetailsActivity extends AppCompatActivity {

    // --- Biến Giao diện ---
    private ImageView imageCoverArt;
    private Button btnChooseImage, btnChooseAudio, btnChooseLyric, btnSave;
    private EditText editTitle;
    private TextView textAudioFileName, textLyricFileName;
    private ProgressBar progressBar;
    private LinearLayout genreContainer;
    private AutoCompleteTextView autocompleteArtist;
    private ChipGroup chipGroupArtists;
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
    private final Map<Integer, String> selectedArtists = new HashMap<>();
    private List<Artist> allArtistsList = new ArrayList<>();

    // --- Hằng số ---
    private static final int PICK_IMAGE = 1001;
    private static final int PICK_AUDIO = 1002;
    private static final int PICK_LYRIC = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmusic_admin);

        songIdToEdit = getIntent().getIntExtra("SONG_ID_TO_EDIT", -1);

        initViews();
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

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_song_details);
        imageCoverArt = findViewById(R.id.imageCoverArt);
        btnChooseImage = findViewById(R.id.btnUploadArtwork);
        btnChooseAudio = findViewById(R.id.btnUploadAudio);
        btnChooseLyric = findViewById(R.id.btnUploadLyric);
        btnSave = findViewById(R.id.savereleaseandaddsong);
        editTitle = findViewById(R.id.editTitle);
        textAudioFileName = findViewById(R.id.textAudioFileName);
        textLyricFileName = findViewById(R.id.textLyricFileName);
        progressBar = findViewById(R.id.progressBar);
        genreContainer = findViewById(R.id.genreContainer);
        autocompleteArtist = findViewById(R.id.autocomplete_artist);
        chipGroupArtists = findViewById(R.id.chipgroup_artists);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViewModels() {
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
    }

    private void setupButtons() {
        btnChooseImage.setOnClickListener(v -> pickFile(PICK_IMAGE, "image/*"));
        btnChooseAudio.setOnClickListener(v -> pickFile(PICK_AUDIO, "audio/*"));
        btnChooseLyric.setOnClickListener(v -> pickFile(PICK_LYRIC, "*/*"));
        btnSave.setOnClickListener(v -> uploadAndSaveSong());
    }

    private void observeGenres() {
        genreViewModel.getAllGenres().observe(this, genres -> {
            if (genres != null) {
                genreContainer.removeAllViews();
                for (Genre genre : genres) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(genre.name);
                    checkBox.setTextColor(getResources().getColor(R.color.white));
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
                // Sau khi genres đã được tạo, nếu là chế độ sửa, cập nhật lại checkbox
                if (songToEdit != null) {
                    updateCheckboxes(selectedGenreIds);
                }
            }
        });
    }

    private void setupArtistSearch() {
        artistViewModel.getAllArtists().observe(this, artists -> {
            if (artists != null) {
                allArtistsList = artists;
                List<String> artistNames = new ArrayList<>();
                for (Artist artist : artists) {
                    artistNames.add(artist.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, artistNames);
                autocompleteArtist.setAdapter(adapter);
            }
        });
        autocompleteArtist.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Artist artist : allArtistsList) {
                if (artist.getName().equals(selectedName)) {
                    addArtistChip(artist);
                    break;
                }
            }
            autocompleteArtist.setText("");
        });
    }

    private void addArtistChip(Artist artist) {
        if (selectedArtists.containsKey(artist.getArtistId())) {
            Toast.makeText(this, "Nghệ sĩ này đã được thêm", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedArtists.put(artist.getArtistId(), artist.getName());
        Chip chip = new Chip(this);
        chip.setText(artist.getName());
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupArtists.removeView(chip);
            selectedArtists.remove(artist.getArtistId());
        });
        chipGroupArtists.addView(chip);
    }

    private void loadSongDataForEdit() {
        // Cập nhật giao diện cho chế độ Sửa
        MaterialToolbar toolbar = findViewById(R.id.toolbar_song_details);
        toolbar.setTitle("Chỉnh sửa bài hát");
        btnSave.setText("Lưu thay đổi");

        // --- Tải dữ liệu Genre ---
        songViewModel.getSongWithGenre(songIdToEdit).observe(this, songWithGenres -> {
            // 'songWithGenres' là tên biến cho đối tượng kết quả
            if (songWithGenres != null && songWithGenres.song != null) {
                this.songToEdit = songWithGenres.song;

                // Đổ dữ liệu cơ bản lên giao diện
                editTitle.setText(songWithGenres.song.getName());
                File imageFile = new File(songWithGenres.song.getImage());
                if (imageFile.exists()) {
                    Glide.with(this).load(imageFile).into(imageCoverArt);
                }
                String audioPath = songWithGenres.song.getAudioUrl();
                if (audioPath != null && !audioPath.isEmpty()) {
                    textAudioFileName.setText(new File(audioPath).getName());
                }
                String lyricPath = songWithGenres.song.getUrlLyric();
                if (lyricPath != null && !lyricPath.isEmpty()) {
                    textLyricFileName.setText(new File(lyricPath).getName());
                }

                // Lấy danh sách ID của các genre đã chọn
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

        // --- Tải dữ liệu Artist (ĐÃ SỬA LẠI) ---
        songViewModel.getSongWithArtists(songIdToEdit).observe(this, songWithArtists -> {
            // 'songWithArtists' (chữ 's' viết thường) là TÊN BIẾN cho đối tượng kết quả.
            if (songWithArtists != null && songWithArtists.artists != null) {
                // Dọn dẹp danh sách và Chip cũ
                selectedArtists.clear();
                chipGroupArtists.removeAllViews();

                // Lặp qua danh sách artists lấy được từ biến 'songWithArtists'
                for (Artist artist : songWithArtists.artists) {
                    addArtistChip(artist);
                }
            }
        });
    }

    private void uploadAndSaveSong() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedArtists.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (songToEdit == null && (imageUri == null || audioUri == null)) {
            Toast.makeText(this, "Vui lòng chọn file ảnh bìa và file nhạc", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        new Thread(() -> {
            try {
                List<Integer> finalArtistIds = new ArrayList<>(selectedArtists.keySet());
                if (songToEdit != null) { // UPDATE MODE
                    songToEdit.setName(title);
                    songToEdit.setArtist(getArtistDisplayString());
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
                } else { // CREATE MODE
                    String uniqueFolderName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date()) + "_" + UUID.randomUUID().toString().substring(0, 8);
                    File songFolder = new File(getFilesDir(), "songs/" + uniqueFolderName);
                    songFolder.mkdirs();
                    String imagePath = copyFileToFolder(imageUri, songFolder, "cover." + getFileExtension(imageUri));
                    String audioPath = copyFileToFolder(audioUri, songFolder, "audio." + getFileExtension(audioUri));
                    String lyricPath = (lyricUri != null) ? copyFileToFolder(lyricUri, songFolder, "lyric." + getFileExtension(lyricUri)) : "";
                    if (imagePath == null || audioPath == null) {
                        runOnUiThread(() -> Toast.makeText(this, "Lưu file thất bại", Toast.LENGTH_SHORT).show());
                        songFolder.delete();
                        return;
                    }
                    Song newSong = new Song();
                    newSong.setName(title);
                    newSong.setArtist(getArtistDisplayString());
                    newSong.setImage(imagePath);
                    newSong.setAudioUrl(audioPath);
                    newSong.setUrlLyric(lyricPath);
                    newSong.setListenCounts(0);
                    songViewModel.insertSongWithRelationships(newSong, selectedGenreIds, finalArtistIds);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lưu bài hát thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                });
            }
        }).start();
    }

    private String getArtistDisplayString() {
        if (selectedArtists.isEmpty()) return "Nhiều nghệ sĩ";
        List<String> names = new ArrayList<>(selectedArtists.values());
        String firstArtist = names.get(0);
        if (names.size() > 1) {
            return firstArtist + ", " + names.get(1) + (names.size() > 2 ? ",..." : "");
        }
        return firstArtist;
    }

    // --- Các phương thức helper khác ---

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

    private void pickFile(int requestCode, String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(Intent.createChooser(intent, "Select file"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            switch (requestCode) {
                case PICK_IMAGE:
                    imageUri = uri; imageCoverArt.setImageURI(uri); break;
                case PICK_AUDIO:
                    audioUri = uri; textAudioFileName.setText(getFileName(uri)); break;
                case PICK_LYRIC:
                    lyricUri = uri; textLyricFileName.setText(getFileName(uri)); break;
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "unknown";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
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
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
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