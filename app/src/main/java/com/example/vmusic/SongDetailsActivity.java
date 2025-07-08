package com.example.vmusic;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import entity.Genre;
import entity.Song;
import viewmodel.GenreViewModel;
import viewmodel.SongViewModel;

public class SongDetailsActivity extends AppCompatActivity {
    private ImageView imageCoverArt;
    private Button btnChooseImage, btnChooseAudio, btnChooseLyric, btnSave, btnAddArtist;
    private EditText editTitle, editAdditionalArtist;
    private TextView textAudioFileName, textLyricFileName;
    private Spinner spinnerGenre;
    private ProgressBar progressBar;
    private LinearLayout artistTagsContainer;

    private Uri imageUri, audioUri, lyricUri;
    private int selectedGenreId = -1;
    private List<String> artists = new ArrayList<>();

    private SongViewModel songViewModel;
    private GenreViewModel genreViewModel;

    private static final int PICK_IMAGE = 1001;
    private static final int PICK_AUDIO = 1002;
    private static final int PICK_LYRIC = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmusic_admin);

        initViews();
        setupViewModels();
        setupGenreSpinner();
        setupButtons();
    }

    private void initViews() {
        imageCoverArt = findViewById(R.id.imageCoverArt);
        btnChooseImage = findViewById(R.id.btnUploadArtwork);
        btnChooseAudio = findViewById(R.id.btnUploadAudio);
        btnChooseLyric = findViewById(R.id.btnUploadLyric);
        btnSave = findViewById(R.id.savereleaseandaddsong);
        btnAddArtist = findViewById(R.id.btnAddArtist);

        editTitle = findViewById(R.id.editTitle);
        editAdditionalArtist = findViewById(R.id.editAdditionalArtist);
        textAudioFileName = findViewById(R.id.textAudioFileName);
        textLyricFileName = findViewById(R.id.textLyricFileName);
        spinnerGenre = findViewById(R.id.spinnerPrimaryGenre);
        progressBar = findViewById(R.id.progressBar);
        artistTagsContainer = findViewById(R.id.artistTagsContainer);
    }

    private void setupViewModels() {
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);
    }

    private void setupGenreSpinner() {
        genreViewModel.getAllGenres().observe(this, genres -> {
            ArrayAdapter<Genre> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGenre.setAdapter(adapter);

            spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedGenreId = genres.get(position).genreId;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedGenreId = -1;
                }
            });
        });
    }

    private void setupButtons() {
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnChooseAudio.setOnClickListener(v -> pickFile(PICK_AUDIO, "audio/*"));
        btnChooseLyric.setOnClickListener(v -> pickFile(PICK_LYRIC, "*/*"));

        btnAddArtist.setOnClickListener(v -> {
            String newArtist = editAdditionalArtist.getText().toString().trim();
            if (!newArtist.isEmpty() && artists.size() < 5) {
                if (artists.contains(newArtist)) {
                    Toast.makeText(this, "Ca sĩ đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    artists.add(newArtist);
                    editAdditionalArtist.setText("");
                    updateArtistTags();
                }
            } else if (artists.size() >= 5) {
                Toast.makeText(this, "Tối đa 5 ca sĩ!", Toast.LENGTH_SHORT).show();
            }
        });

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

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (requestCode == PICK_IMAGE) {
                imageUri = uri;
                imageCoverArt.setImageURI(uri);
            } else if (requestCode == PICK_AUDIO) {
                audioUri = uri;
                textAudioFileName.setText(getFileName(uri));
            } else if (requestCode == PICK_LYRIC) {
                lyricUri = uri;
                textLyricFileName.setText(getFileName(uri));
            }
        }
    }

    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String name = "unknown";
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            name = cursor.getString(nameIndex);
            cursor.close();
        }
        return name;
    }

    private void updateArtistTags() {
        artistTagsContainer.removeAllViews();
        for (String artist : artists) {
            TextView tag = new TextView(this);
            tag.setText(artist);
            tag.setTextColor(getResources().getColor(android.R.color.white));
            tag.setBackgroundResource(R.drawable.tag_background);
            tag.setPadding(12, 6, 12, 6);
            tag.setTextSize(14);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 8, 8);
            tag.setLayoutParams(params);

            ImageView closeIcon = new ImageView(this);
            closeIcon.setImageResource(android.R.drawable.ic_delete);
            closeIcon.setPadding(4, 4, 4, 4);
            closeIcon.setOnClickListener(v -> {
                artists.remove(artist);
                updateArtistTags();
            });

            LinearLayout tagLayout = new LinearLayout(this);
            tagLayout.setOrientation(LinearLayout.HORIZONTAL);
            tagLayout.addView(tag);
            tagLayout.addView(closeIcon);
            artistTagsContainer.addView(tagLayout);
        }
    }

    private void uploadAndSaveSong() {
        String title = editTitle.getText().toString();
        String allArtists = String.join(",", artists);
        //genre null
        if (title.isEmpty() || artists.isEmpty() || imageUri == null || audioUri == null || lyricUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin, bao gồm ít nhất 1 ca sĩ", Toast.LENGTH_SHORT).show();
            return;
        }
        //genre not null
//        if (title.isEmpty() || artist.isEmpty() || imageUri == null || audioUri == null || lyricUri == null || selectedGenreId == -1) {
//            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//            return;
//        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        new Thread(() -> {
            try {
                String imageUrl = uploadToCloudinary(imageUri, "cover_art");
                String audioUrl = uploadToCloudinary(audioUri, "songs");
                String lyricUrl = uploadToCloudinary(lyricUri, "lyrics");

                if (imageUrl == null || audioUrl == null || lyricUrl == null) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Upload thất bại do 1 hoặc nhiều URL bị null", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                Song song = new Song();
                song.setName(title);
                song.setArtist(allArtists); // Lưu tất cả ca sĩ
                song.setImage(imageUrl);
                song.setAudioUrl(audioUrl);
                song.setUrlLyric(lyricUrl);
                song.setListenCounts(0);

                songViewModel.insert(song);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Upload thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi khi upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String uploadToCloudinary(Uri fileUri, String folder) throws InterruptedException {
        final String[] resultUrl = new String[1];
        final CountDownLatch latch = new CountDownLatch(1);

        MediaManager.get().upload(fileUri)
                .option("folder", folder)
                .option("resource_type", "auto")
                .unsigned("vmusic_upload")
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        resultUrl[0] = (String) resultData.get("url");
                        Log.d("CloudinaryUpload", "Success: " + resultUrl[0]);
                        latch.countDown();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("CloudinaryUpload", "Error: " + error.getDescription());
                        latch.countDown();
                    }

                    @Override
                    public void onStart(String requestId) {
                        Log.d("CloudinaryUpload", "Start uploading...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d("CloudinaryUpload", "Uploading: " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e("CloudinaryUpload", "Rescheduled: " + error.getDescription());
                        latch.countDown();
                    }
                })
                .dispatch();

        latch.await();
        return resultUrl[0];
    }
}


