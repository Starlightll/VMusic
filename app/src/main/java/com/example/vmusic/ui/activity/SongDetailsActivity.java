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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.vmusic.R;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.Song;
import com.example.vmusic.viewmodel.GenreViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class SongDetailsActivity extends AppCompatActivity {
    // Khai báo các thành phần giao diện
    private ImageView imageCoverArt;
    private Button btnChooseImage, btnChooseAudio, btnChooseLyric, btnSave;
    private EditText editTitle, editArtist;
    private TextView textAudioFileName, textLyricFileName;
    private ProgressBar progressBar;
    private LinearLayout genreContainer;

    // Khai báo các biến dữ liệu
    private Uri imageUri, audioUri, lyricUri;
    private SongViewModel songViewModel;
    private GenreViewModel genreViewModel;
    private final List<Integer> selectedGenreIds = new ArrayList<>();

    // Các hằng số request code
    private static final int PICK_IMAGE = 1001;
    private static final int PICK_AUDIO = 1002;
    private static final int PICK_LYRIC = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmusic_admin);

        initViews();
        setupViewModels();
        observeGenres();
        setupButtons();
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
                checkBox.setButtonTintList(getResources().getColorStateList(R.color.primary_green));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedGenreIds.add(genre.genreId);
                    } else {
                        selectedGenreIds.remove((Integer) genre.genreId);
                    }
                });
                genreContainer.addView(checkBox);
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

        if (title.isEmpty() || artist.isEmpty() || imageUri == null || audioUri == null || lyricUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn file", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        new Thread(() -> {
            try {

                // Tạo folder riêng cho mỗi bài hát
                String uniqueFolderName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date()) + "_" + UUID.randomUUID();
                File songFolder = new File(getFilesDir(), "songs/" + uniqueFolderName);
                if (!songFolder.exists()) songFolder.mkdirs();

                // Copy file vào folder bài hát
                String imageExt = getFileExtension(imageUri);
                String audioExt = getFileExtension(audioUri);
                String lyricExt = getFileExtension(lyricUri);

                String imagePath = copyFileToFolder(imageUri, songFolder, "cover." + imageExt);
                String audioPath = copyFileToFolder(audioUri, songFolder, "audio." + audioExt);
                String lyricPath = copyFileToFolder(lyricUri, songFolder, "lyric." + lyricExt);

                if (imagePath == null || audioPath == null || lyricPath == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Lưu file thất bại", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                    });
                    return;
                }

                Song song = new Song();
                song.setName(title);
                song.setArtist(artist);
                song.setImage(imagePath);
                song.setAudioUrl(audioPath);
                song.setUrlLyric(lyricPath);
                song.setListenCounts(0);

                songViewModel.insertSongWithGenres(song, selectedGenreIds);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Lưu bài hát thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String uploadToCloudinary(Uri fileUri, String folder) throws InterruptedException {
        final String[] resultUrl = {null}; // Khởi tạo là null
        final CountDownLatch latch = new CountDownLatch(1);

        MediaManager.get().upload(fileUri)
                .option("folder", folder)
                .option("resource_type", "auto")
                .unsigned("vmusic_upload") // Thay bằng upload preset của bạn
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        if (resultData != null && resultData.containsKey("url")) {
                            String rawUrl = (String) resultData.get("url");
                            if (rawUrl != null) {
                                resultUrl[0] = rawUrl.replace("http://", "https://");
                            }
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("CloudinaryError", "Upload failed: " + error.getDescription());
                        latch.countDown();
                    }

                    // Các phương thức callback khác
                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) { latch.countDown(); }

                }).dispatch();

        // Chờ cho đến khi upload hoàn tất hoặc thất bại
        latch.await();
        return resultUrl[0];
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


}


