package com.example.vmusic.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.vmusic.R;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Song;
import com.example.vmusic.viewmodel.GenreViewModel;
import com.example.vmusic.viewmodel.SongViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class SongDetailsActivity extends AppCompatActivity {
    // Khai báo các thành phần giao diện
    private ImageView imageCoverArt;
    private Button btnChooseImage, btnChooseAudio, btnChooseLyric, btnSave;
    private EditText editTitle, editArtist;
    private TextView textAudioFileName, textLyricFileName;
    private ProgressBar progressBar;
    private LinearLayout genreContainer;

    // Khai báo các biến dữ liệu
    private Song songToEdit = null;
    private Uri imageUri, audioUri, lyricUri;
    private SongViewModel songViewModel;
    private GenreViewModel genreViewModel;
    private final List<Integer> selectedGenreIds = new ArrayList<>();

    // Các hằng số request code
    private static final int PICK_IMAGE = 1001;
    private static final int PICK_AUDIO = 1002;
    private static final int PICK_LYRIC = 1003;
    private int songIdToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmusic_admin);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_song_details);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        // === KẾT THÚC CODE MỚI ===
        songIdToEdit = getIntent().getIntExtra("SONG_ID_TO_EDIT", -1);
        initViews();
        setupViewModels();
        observeGenres();
        setupButtons();
        if (songIdToEdit != -1) {
            // Gọi hàm để bắt đầu quá trình khởi tạo songToEdit
            loadSongDataForEdit();
        }
    }
    private void loadSongDataForEdit() {
        setTitle("Chỉnh sửa bài hát");
        btnSave.setText("Lưu thay đổi");

        songViewModel.getSongWithGenre(songIdToEdit).observe(this, songWithGenres -> {
            if (songWithGenres != null && songWithGenres.song != null) {
                // 1. Lưu lại bài hát đang sửa
                this.songToEdit = songWithGenres.song;

                // 2. Đổ dữ liệu cơ bản lên các EditText và ImageView
                editTitle.setText(songWithGenres.song.getName());
                editArtist.setText(songWithGenres.song.getArtist());
                File imageFile = new File(songWithGenres.song.getImage());
                if (imageFile.exists()) {
                    Glide.with(this).load(imageFile).into(imageCoverArt);
                }

                // ================== PHẦN SỬA LỖI HIỂN THỊ TÊN FILE ==================
                // Lấy đường dẫn đầy đủ của file nhạc từ database
                String audioPath = songWithGenres.song.getAudioUrl();
                if (audioPath != null && !audioPath.isEmpty()) {
                    // Dùng lớp File để lấy ra tên file từ đường dẫn đầy đủ
                    String audioFileName = new File(audioPath).getName();
                    textAudioFileName.setText(audioFileName);
                }

                // Lấy đường dẫn đầy đủ của file lyric từ database
                String lyricPath = songWithGenres.song.getUrlLyric();
                if (lyricPath != null && !lyricPath.isEmpty()) {
                    // Dùng lớp File để lấy ra tên file từ đường dẫn đầy đủ
                    String lyricFileName = new File(lyricPath).getName();
                    textLyricFileName.setText(lyricFileName);
                }
                // ====================================================================

                // 3. Lấy và hiển thị các thể loại đã chọn
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
    }

    private void initViews() {
        imageCoverArt = findViewById(R.id.imageCoverArt);
        btnChooseImage = findViewById(R.id.btnUploadArtwork);
        btnChooseAudio = findViewById(R.id.btnUploadAudio);
        btnChooseLyric = findViewById(R.id.btnUploadLyric);
        btnSave = findViewById(R.id.savereleaseandaddsong);
        editTitle = findViewById(R.id.editTitle);
        editArtist = findViewById(R.id.editAdditionalArtist); // Ánh xạ tới EditText của tên ca sĩ
        textAudioFileName = findViewById(R.id.textAudioFileName);
        textLyricFileName = findViewById(R.id.textLyricFileName);
        progressBar = findViewById(R.id.progressBar);
        genreContainer = findViewById(R.id.genreContainer);
    }

    private void setupViewModels() {
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);
    }

    private void observeGenres() {
        genreViewModel.getAllGenres().observe(this, genres -> {
            genreContainer.removeAllViews();
            for (Genre genre : genres) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(genre.name);
                checkBox.setTextColor(getResources().getColor(R.color.white));

                // LƯU Ý QUAN TRỌNG: Gán genreId vào tag của CheckBox
                checkBox.setTag(genre.genreId);

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedGenreIds.add(genre.genreId);
                    } else {
                        selectedGenreIds.remove((Integer) genre.genreId);
                    }
                });
                genreContainer.addView(checkBox);
            }

            // Sau khi đã tạo tất cả checkbox, nếu đang ở chế độ sửa, hãy gọi lại loadSongDataForEdit
            // để đảm bảo dữ liệu được đổ vào đúng lúc.
            if (songIdToEdit != -1) {
                loadSongDataForEdit();
            }
        });
    }

    private void setupButtons() {
        btnChooseImage.setOnClickListener(v -> pickFile(PICK_IMAGE, "image/*"));
        btnChooseAudio.setOnClickListener(v -> pickFile(PICK_AUDIO, "audio/*"));
        btnChooseLyric.setOnClickListener(v -> pickFile(PICK_LYRIC, "*/*")); // Cho phép chọn mọi loại file cho lyric
        btnSave.setOnClickListener(v -> uploadAndSaveSong());
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
                    imageUri = uri;
                    imageCoverArt.setImageURI(uri);
                    break;
                case PICK_AUDIO:
                    audioUri = uri;
                    textAudioFileName.setText(getFileName(uri));
                    break;
                case PICK_LYRIC:
                    lyricUri = uri;
                    textLyricFileName.setText(getFileName(uri));
                    break;
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "unknown";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }
        return fileName;
    }



    private void uploadAndSaveSong() {
        String title = editTitle.getText().toString().trim();
        String artist = editArtist.getText().toString().trim();

        // --- BƯỚC 1: KIỂM TRA DỮ LIỆU ĐẦU VÀO ---
        if (title.isEmpty() || artist.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tên bài hát và nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu là chế độ TẠO MỚI, bắt buộc phải chọn file ảnh và nhạc
        if (songToEdit == null && (imageUri == null || audioUri == null)) {
            Toast.makeText(this, "Vui lòng chọn file ảnh bìa và file nhạc", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // --- BƯỚC 2: THỰC HIỆN TÁC VỤ NỀN (LƯU/CẬP NHẬT) ---
        new Thread(() -> {
            try {
                // =================================================================
                // === TRƯỜNG HỢP 1: CẬP NHẬT BÀI HÁT ĐÃ CÓ (UPDATE MODE) ===
                // =================================================================
                if (songToEdit != null) {
                    // Lấy thư mục hiện tại của bài hát từ đường dẫn file audio
                    File songFolder = new File(songToEdit.getAudioUrl()).getParentFile();

                    // Cập nhật thông tin metadata
                    songToEdit.setName(title);
                    songToEdit.setArtist(artist);

                    // KIỂM TRA VÀ THAY THẾ FILE NẾU NGƯỜI DÙNG CHỌN FILE MỚI
                    // 1. Cập nhật ảnh bìa (nếu có)
                    if (imageUri != null) {
                        new File(songToEdit.getImage()).delete(); // Xóa file ảnh cũ
                        String newImageExt = getFileExtension(imageUri);
                        String newImagePath = copyFileToFolder(imageUri, songFolder, "cover." + newImageExt);
                        songToEdit.setImage(newImagePath);
                    }

                    // 2. Cập nhật file nhạc (nếu có)
                    if (audioUri != null) {
                        new File(songToEdit.getAudioUrl()).delete(); // Xóa file nhạc cũ
                        String newAudioExt = getFileExtension(audioUri);
                        String newAudioPath = copyFileToFolder(audioUri, songFolder, "audio." + newAudioExt);
                        songToEdit.setAudioUrl(newAudioPath);
                    }

                    // 3. Cập nhật file lyric (nếu có)
                    if (lyricUri != null) {
                        new File(songToEdit.getUrlLyric()).delete(); // Xóa file lyric cũ
                        String newLyricExt = getFileExtension(lyricUri);
                        String newLyricPath = copyFileToFolder(lyricUri, songFolder, "lyric." + newLyricExt);
                        songToEdit.setUrlLyric(newLyricPath);
                    }

                    // Gọi ViewModel để cập nhật bài hát và các thể loại liên quan
                    songViewModel.updateSongWithGenres(songToEdit, selectedGenreIds);

                    // Cập nhật UI sau khi thành công
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật bài hát thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });

                }
                // ===========================================================
                // === TRƯỜNG HỢP 2: LƯU BÀI HÁT MỚI (CREATE MODE) ===
                // ===========================================================
                else {
                    // Tạo thư mục mới, độc nhất cho bài hát
                    String uniqueFolderName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date()) + "_" + UUID.randomUUID().toString().substring(0, 8);
                    File songFolder = new File(getFilesDir(), "songs/" + uniqueFolderName);
                    songFolder.mkdirs();

                    // Sao chép các file đã chọn vào thư mục mới
                    String imagePath = copyFileToFolder(imageUri, songFolder, "cover." + getFileExtension(imageUri));
                    String audioPath = copyFileToFolder(audioUri, songFolder, "audio." + getFileExtension(audioUri));

                    // File lyric không bắt buộc, nên cần kiểm tra null
                    String lyricPath = "";
                    if (lyricUri != null) {
                        lyricPath = copyFileToFolder(lyricUri, songFolder, "lyric." + getFileExtension(lyricUri));
                    }

                    // Kiểm tra lại nếu việc copy file bị lỗi
                    if (imagePath == null || audioPath == null) {
                        runOnUiThread(() -> Toast.makeText(this, "Lưu file thất bại", Toast.LENGTH_SHORT).show());
                        // Dọn dẹp thư mục rỗng đã tạo nếu thất bại
                        songFolder.delete();
                        return;
                    }

                    // Tạo đối tượng Song mới
                    Song newSong = new Song();
                    newSong.setName(title);
                    newSong.setArtist(artist);
                    newSong.setImage(imagePath);
                    newSong.setAudioUrl(audioPath);
                    newSong.setUrlLyric(lyricPath);
                    newSong.setListenCounts(0);

                    // Gọi ViewModel để chèn bài hát mới và các thể loại liên quan
                    songViewModel.insertSongWithGenres(newSong, selectedGenreIds);

                    // Cập nhật UI sau khi thành công
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Lưu bài hát mới thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                // --- BƯỚC 3: XỬ LÝ LỖI (NẾU CÓ) ---
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                });
            }
        }).start();
    }

    private String copyFileToFolder(Uri uri, File targetFolder, String outputFileName) throws IOException {
        if (!targetFolder.exists()) targetFolder.mkdirs();

        File outFile = new File(targetFolder, outputFileName);
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return outFile.getAbsolutePath();
    }

    private String copyFileToInternalStorage(Uri uri, String subFolder, String fileName) throws IOException {
        File dir = new File(getFilesDir(), subFolder);
        if (!dir.exists()) dir.mkdirs();

        File outFile = new File(dir, fileName);
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return outFile.getAbsolutePath();
    }



    private String getFileExtension(Uri uri) {
        String extension = null;

        // Kiểm tra MIME type
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        // Fallback nếu MIME không có
        if (extension == null) {
            String path = uri.getPath();
            if (path != null && path.contains(".")) {
                extension = path.substring(path.lastIndexOf('.') + 1);
            }
        }

        return extension != null ? extension : "bin"; // Nếu không xác định, để .bin
    }
    private void updateCheckboxes(List<Integer> checkedIds) {
        if (checkedIds == null) return;

        // genreContainer là LinearLayout chứa các checkbox của bạn
        for (int i = 0; i < genreContainer.getChildCount(); i++) {
            View childView = genreContainer.getChildAt(i);
            if (childView instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) childView;
                // Chúng ta cần một cách để lấy genreId từ CheckBox.
                // Một cách hay là dùng setTag/getTag.
                Object tag = checkBox.getTag();
                if (tag instanceof Integer) {
                    int genreId = (Integer) tag;
                    // Nếu ID của checkbox này có trong danh sách đã chọn, thì tick vào nó.
                    if (checkedIds.contains(genreId)) {
                        checkBox.setChecked(true);
                    }
                }
            }
        }
    }

}


