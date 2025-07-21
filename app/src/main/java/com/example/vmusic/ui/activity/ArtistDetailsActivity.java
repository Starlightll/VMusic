package com.example.vmusic.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.entity.Artist;
import com.example.vmusic.ui.adapter.ArtistSongAdapter;
import com.example.vmusic.viewmodel.ArtistViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ArtistDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1001;
    private ImageView imageArtist;
    private EditText editArtistName;
    private Button btnChooseImage, btnSave;
    private ProgressBar progressBar;

    private ArtistViewModel artistViewModel;
    private Uri imageUri = null;
    private Artist artistToEdit = null;
    private LinearLayout layoutSongList;
    private RecyclerView recyclerViewArtistSongs;
    private ArtistSongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        // Ánh xạ View
        imageArtist = findViewById(R.id.image_artist_details);
        editArtistName = findViewById(R.id.edit_artist_name);
        btnChooseImage = findViewById(R.id.btn_choose_artist_image);
        btnSave = findViewById(R.id.btn_save_artist);
        progressBar = findViewById(R.id.progress_bar_artist);
        MaterialToolbar toolbar = findViewById(R.id.toolbar_artist_details);
        layoutSongList = findViewById(R.id.layout_song_list);
        recyclerViewArtistSongs = findViewById(R.id.recycler_view_artist_songs);

        // ViewModel
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);

        // Lấy dữ liệu nếu là chế độ sửa
        if (getIntent().hasExtra("ARTIST_TO_EDIT")) {
            artistToEdit = (Artist) getIntent().getSerializableExtra("ARTIST_TO_EDIT");
            loadArtistData();
            toolbar.setTitle("Chỉnh sửa nghệ sĩ");
        }

        // Bắt sự kiện
        toolbar.setNavigationOnClickListener(v -> finish());
        btnChooseImage.setOnClickListener(v -> pickImage());
        btnSave.setOnClickListener(v -> saveArtist());
    }

    private void loadArtistData() {
        if (artistToEdit == null) {
            return;
        }

        editArtistName.setText(artistToEdit.getName());
        File imageFile = new File(artistToEdit.getImage());
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .placeholder(R.drawable.ic_person) // Ảnh chờ
                    .into(imageArtist);
        }

        //    Vì trong XML nó có android:visibility="gone", chúng ta cần đổi nó thành VISIBLE.
        layoutSongList.setVisibility(View.VISIBLE);

        //    Đây là bước chuẩn bị để RecyclerView sẵn sàng nhận dữ liệu.
        songAdapter = new ArtistSongAdapter(this);
        recyclerViewArtistSongs.setAdapter(songAdapter);
        recyclerViewArtistSongs.setLayoutManager(new LinearLayoutManager(this));

        //    Chúng ta lấy về một đối tượng LiveData<ArtistWithSongs>.
        artistViewModel.getArtistWithSongs(artistToEdit.getArtistId()).observe(this, artistWithSongs -> {

            if (artistWithSongs != null && artistWithSongs.songs != null) {
                songAdapter.setSongs(artistWithSongs.songs);
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageArtist.setImageURI(imageUri);
        }
    }

    private void saveArtist() {
        String name = editArtistName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu là chế độ tạo mới, bắt buộc phải có ảnh
        if (artistToEdit == null && imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh cho nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        new Thread(() -> {
            try {
                // CHẾ ĐỘ SỬA
                if (artistToEdit != null) {
                    artistToEdit.setName(name);
                    // Nếu người dùng chọn ảnh mới, thì thay thế file cũ
                    if (imageUri != null) {
                        File artistFolder = new File(artistToEdit.getImage()).getParentFile();
                        new File(artistToEdit.getImage()).delete();
                        String newImagePath = copyFileToInternalStorage(imageUri, artistFolder);
                        artistToEdit.setImage(newImagePath);
                    }
                    artistViewModel.update(artistToEdit);
                }
                // CHẾ ĐỘ TẠO MỚI
                else {
                    File artistsDir = new File(getFilesDir(), "artists");
                    String uniqueFolderName = UUID.randomUUID().toString();
                    File newArtistFolder = new File(artistsDir, uniqueFolderName);
                    String imagePath = copyFileToInternalStorage(imageUri, newArtistFolder);

                    Artist newArtist = new Artist();
                    newArtist.setName(name);
                    newArtist.setImage(imagePath);
                    newArtist.setListenCounts(0);
                    artistViewModel.insert(newArtist);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                });
            }
        }).start();
    }

    private String copyFileToInternalStorage(Uri uri, File targetFolder) throws IOException {
        if (!targetFolder.exists()) targetFolder.mkdirs();

        String fileName = "avatar.jpg"; // Luôn đặt tên là avatar cho dễ quản lý
        File outFile = new File(targetFolder, fileName);

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
}
