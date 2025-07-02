package com.example.vmusic;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Calendar;
import java.util.Map; // Đảm bảo có import này
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vmusic.model.Song;
import com.example.vmusic.model.SongDBHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class SongDetailsActivity extends AppCompatActivity {

    private static final String TAG = "SongDetailsActivity";

    // --- UI Views ---
    private EditText editTitle, editArtist, editReleaseDate;
    private Button btnUploadArtwork, btnUploadAudio, btnSave;
    private ImageView imageCoverArt;
    private TextView textAudioFileName;
    private RadioGroup radioGroupExplicit, radioGroupReleased;
    private ProgressBar progressBar;
    private Spinner spinnerLanguage, spinnerPrimaryGenre, spinnerSecondaryGenre; // <-- SỬA LẠI KHAI BÁO GỌN GÀNG

    // --- Data Variables ---
    private Uri imageUri;
    private Uri audioUri;
    private String uploadedImageUrl;
    private String uploadedAudioUrl;

    // --- ActivityResultLaunchers for picking files ---
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    imageCoverArt.setImageURI(imageUri);
                    Log.d(TAG, "Image selected: " + imageUri.toString());
                }
            });

    private final ActivityResultLauncher<Intent> audioPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    audioUri = result.getData().getData();
                    String fileName = getFileName(audioUri);
                    textAudioFileName.setText(fileName);
                    Log.d(TAG, "Audio selected: " + audioUri.toString());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadmusicforadmin);

        initViews();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        editArtist = findViewById(R.id.editArtist);
        editReleaseDate = findViewById(R.id.editReleaseDate);
        btnUploadArtwork = findViewById(R.id.btnUploadArtwork);
        btnUploadAudio = findViewById(R.id.btnUploadAudio);
        btnSave = findViewById(R.id.savereleaseandaddsong);
        imageCoverArt = findViewById(R.id.imageCoverArt);
        textAudioFileName = findViewById(R.id.textAudioFileName);
        radioGroupExplicit = findViewById(R.id.radioGroupExplicit);
        radioGroupReleased = findViewById(R.id.radioGroupReleased);
        progressBar = findViewById(R.id.progressBar);

        // Ánh xạ các Spinner
        spinnerLanguage = findViewById(R.id.spinnerLanguage); // <-- SỬA LẠI ÁNH XẠ ĐÚNG
        spinnerPrimaryGenre = findViewById(R.id.spinnerPrimaryGenre);
        spinnerSecondaryGenre = findViewById(R.id.spinnerSecondaryGenre);
    }

    private void setupSpinners() {
        // --- Thiết lập Spinner Ngôn ngữ ---
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, R.layout.custom_spinner_item);
        languageAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        // --- Thiết lập Spinner Thể loại chính ---
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(this,
                R.array.genre_array, R.layout.custom_spinner_item);
        genreAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerPrimaryGenre.setAdapter(genreAdapter);

        // --- Thiết lập Spinner Thể loại phụ ---
        spinnerSecondaryGenre.setAdapter(genreAdapter);
    }

    private void setupClickListeners() {
        btnUploadArtwork.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnUploadAudio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("audio/*");
            audioPickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                startUploadProcess();
            }
        });

        editReleaseDate.setOnClickListener(v -> showDatePickerDialog());
        editReleaseDate.setFocusable(false);
        editReleaseDate.setClickable(true);
    }

    private boolean validateInput() {
        if (editTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a song title.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editArtist.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter the artist's name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerLanguage.getSelectedItemPosition() == 0) { // Kiểm tra nếu mục mặc định được chọn
            Toast.makeText(this, "Please select a language.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select a cover artwork.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (audioUri == null) {
            Toast.makeText(this, "Please select an audio file.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void startUploadProcess() {
        Log.d(TAG, "Starting upload process...");
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        uploadImageToCloudinary();
    }

    private void uploadImageToCloudinary() {
        Log.d(TAG, "Uploading image to Cloudinary...");
        final String uploadPreset = "vmusic_upload"; // Đảm bảo tên này đúng

        MediaManager.get().upload(imageUri)
                .option("upload_preset", uploadPreset)
                .option("resource_type", "image")
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        uploadedImageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Image uploaded successfully. URL: " + uploadedImageUrl);
                        uploadAudioToCloudinary();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        handleUploadFailure("Image upload failed: " + error.getDescription());
                    }

                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void uploadAudioToCloudinary() {
        Log.d(TAG, "Uploading audio to Cloudinary...");
        final String uploadPreset = "vmusic_upload"; // Đồng bộ tên preset

        MediaManager.get().upload(audioUri)
                .option("upload_preset", uploadPreset)
                .option("resource_type", "video")
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        uploadedAudioUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Audio uploaded successfully. URL: " + uploadedAudioUrl);
                        saveAllDataToDatabase();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        handleUploadFailure("Audio upload failed: " + error.getDescription());
                    }

                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void saveAllDataToDatabase() {
        Log.d(TAG, "Saving all data to SQLite database...");

        Song newSong = new Song();
        newSong.setName(editTitle.getText().toString().trim());
        newSong.setMainArtist(editArtist.getText().toString().trim());
        newSong.setReleaseDate(editReleaseDate.getText().toString().trim());
        newSong.setImageUrl(uploadedImageUrl);
        newSong.setUrl(uploadedAudioUrl);
        newSong.setExplicit(radioGroupExplicit.getCheckedRadioButtonId() == R.id.radioExplicitYes);
        newSong.setPreviouslyReleased(radioGroupReleased.getCheckedRadioButtonId() == R.id.radioReleasedYes);

        // SỬA LẠI CÁCH LẤY DỮ LIỆU TỪ CÁC SPINNER
        newSong.setLanguage(spinnerLanguage.getSelectedItem().toString());
        newSong.setPrimaryGenre(spinnerPrimaryGenre.getSelectedItem().toString());
        newSong.setSecondaryGenre(spinnerSecondaryGenre.getSelectedItem().toString());

        // Lưu vào SQLite
        SongDBHelper dbHelper = new SongDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", newSong.getName());
        values.put("mainArtist", newSong.getMainArtist());
        values.put("language", newSong.getLanguage());
        values.put("releaseDate", newSong.getReleaseDate());
        values.put("primaryGenre", newSong.getPrimaryGenre());
        values.put("secondaryGenre", newSong.getSecondaryGenre());
        values.put("isExplicit", newSong.isExplicit() ? 1 : 0);
        values.put("isReleased", newSong.isPreviouslyReleased() ? 1 : 0);
        values.put("urlImage", newSong.getImageUrl());
        values.put("urlAudio", newSong.getUrl());

        long rowId = db.insert("Songs", null, values);
        db.close();

        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);

        if (rowId != -1) {
            Log.i(TAG, "Song saved to SQLite with row ID: " + rowId); // Đổi sang Log.i cho dễ thấy
            Toast.makeText(this, "Song '" + newSong.getName() + "' saved successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.e(TAG, "Failed to save song to SQLite.");
            Toast.makeText(this, "Error saving song to database.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleUploadFailure(String message) {
        Log.e(TAG, message);
        runOnUiThread(() -> {
            Toast.makeText(SongDetailsActivity.this, message, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", month + 1, dayOfMonth, year);
                    editReleaseDate.setText(selectedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name from URI", e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "Unknown file";
    }
}