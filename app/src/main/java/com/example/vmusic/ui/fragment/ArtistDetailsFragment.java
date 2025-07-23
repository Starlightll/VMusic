package com.example.vmusic.ui.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class ArtistDetailsFragment extends Fragment {

    private ImageView imageArtist;
    private EditText editArtistName;
    private Button btnChooseImage, btnSave;
    private ProgressBar progressBar;
    private LinearLayout layoutSongList;
    private RecyclerView recyclerViewArtistSongs;
    private ArtistViewModel artistViewModel;
    private ArtistSongAdapter songAdapter;
    private Uri imageUri = null;
    private Artist artistToEdit = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(requireContext()).load(uri).into(imageArtist);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupViewModels();
        setupButtons(view);

        int artistIdToEdit = getArguments() != null ? getArguments().getInt("artistId", -1) : -1;

        if (artistIdToEdit != -1) {
            MaterialToolbar toolbar = view.findViewById(R.id.toolbar_artist_details);
            toolbar.setTitle("Chỉnh sửa nghệ sĩ");
            btnSave.setText("Lưu thay đổi");
            observeArtistData(artistIdToEdit);
        }
    }

    private void initViews(View view) {
        imageArtist = view.findViewById(R.id.image_artist_details);
        editArtistName = view.findViewById(R.id.edit_artist_name);
        btnChooseImage = view.findViewById(R.id.btn_choose_artist_image);
        btnSave = view.findViewById(R.id.btn_save_artist);
        progressBar = view.findViewById(R.id.progress_bar_artist);
        layoutSongList = view.findViewById(R.id.layout_song_list);
        recyclerViewArtistSongs = view.findViewById(R.id.recycler_view_artist_songs);

        songAdapter = new ArtistSongAdapter(requireContext());
        recyclerViewArtistSongs.setAdapter(songAdapter);
        recyclerViewArtistSongs.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupViewModels() {
        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
    }

    private void setupButtons(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_artist_details);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        btnChooseImage.setOnClickListener(v -> pickImage());
        btnSave.setOnClickListener(v -> saveArtist());
    }

    private void observeArtistData(int artistId) {
        artistViewModel.getArtistWithSongs(artistId).observe(getViewLifecycleOwner(), artistWithSongs -> {
            if (artistWithSongs != null && artistWithSongs.artist != null) {
                this.artistToEdit = artistWithSongs.artist;
                editArtistName.setText(artistToEdit.getName());
                File imageFile = new File(artistToEdit.getImage());
                if (imageFile.exists()) {
                    Glide.with(requireContext()).load(imageFile).placeholder(R.drawable.ic_person).into(imageArtist);
                }
                if (artistWithSongs.songs != null && !artistWithSongs.songs.isEmpty()) {
                    layoutSongList.setVisibility(View.VISIBLE);
                    songAdapter.setSongs(artistWithSongs.songs);
                } else {
                    layoutSongList.setVisibility(View.GONE);
                }
            }
        });
    }

    private void pickImage() {
        imagePickerLauncher.launch("image/*");
    }

    private void saveArtist() {
        String name = editArtistName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu là chế độ TẠO MỚI, bắt buộc phải chọn ảnh
        if (artistToEdit == null && imageUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn ảnh cho nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // Tạo một bản sao của imageUri để sử dụng an toàn trong Thread
        final Uri finalImageUri = imageUri;

        new Thread(() -> {
            try {
                if (artistToEdit != null) { // UPDATE MODE
                    artistToEdit.setName(name);
                    // Chỉ cập nhật ảnh nếu người dùng đã chọn một ảnh MỚI
                    if (finalImageUri != null) {
                        File artistFolder = new File(artistToEdit.getImage()).getParentFile();
                        new File(artistToEdit.getImage()).delete();
                        artistToEdit.setImage(copyFileToInternalStorage(finalImageUri, artistFolder));
                    }
                    artistViewModel.update(artistToEdit);
                } else {
                    File artistsDir = new File(requireActivity().getFilesDir(), "artists");
                    File newArtistFolder = new File(artistsDir, UUID.randomUUID().toString());
                    String imagePath = copyFileToInternalStorage(finalImageUri, newArtistFolder);

                    Artist newArtist = new Artist();
                    newArtist.setName(name);
                    newArtist.setImage(imagePath);
                    newArtist.setListenCounts(0);
                    artistViewModel.insert(newArtist);
                }

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                });

            } catch (Exception e) { // Bắt Exception chung để an toàn hơn
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String copyFileToInternalStorage(Uri uri, File targetFolder) throws IOException {
        if (!targetFolder.exists()) targetFolder.mkdirs();
        String fileName = "avatar.jpg";
        File outFile = new File(targetFolder, fileName);
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
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