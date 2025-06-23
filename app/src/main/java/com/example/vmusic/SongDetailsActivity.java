package com.example.vmusic;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.vmusic.databinding.UploadmusicforadminBinding;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.Locale;

public class SongDetailsActivity extends AppCompatActivity {

    private static final String TAG = "SongDetailsActivity";
    private  UploadmusicforadminBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UploadmusicforadminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gọi hàm để cài đặt tất cả các sự kiện
        setupEventListeners();
    }

    /**
     * Nơi tập trung cài đặt tất cả các sự kiện cho các View trên màn hình.
     */
    private void setupEventListeners() {

        // 1. Sự kiện cho nút "ADD MAIN ARTIST"
        binding.btnAddMainArtist.setOnClickListener(v -> {
            String artistName = binding.editArtist.getText().toString().trim();
            if (!TextUtils.isEmpty(artistName)) {
                addArtistChip(artistName);
                binding.editArtist.setText(""); // Xóa chữ trong ô input sau khi thêm
            } else {
                Toast.makeText(this, "Please enter an artist name", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Sự kiện khi nhấn vào ô "Release Date"
        binding.savereleaseandaddsong.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        // 3. Sự kiện cho nút "SAVE RELEASE AND ADD SONG"
        binding.savereleaseandaddsong.setOnClickListener(v -> {
            collectAndShowData();
        });
    }

    /**
     * Tạo và thêm một Chip nghệ sĩ vào ChipGroup.
     * @param artistName Tên của nghệ sĩ để hiển thị trên Chip.
     */
    private void addArtistChip(String artistName) {
        Chip chip = new Chip(this);
        chip.setText(artistName);
        chip.setCloseIconVisible(true); // Hiển thị icon 'x' để xóa
//        chip.setChipBackgroundColorResource(R.color.chip_background_color); // Cần tạo màu này
        chip.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        chip.setCloseIconTintResource(android.R.color.white);

        // Sự kiện khi nhấn vào icon 'x' trên Chip để xóa nó
        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroupArtists.removeView(chip);
        });

        binding.chipGroupArtists.addView(chip);
    }

    /**
     * Hiển thị một DatePickerDialog để người dùng chọn ngày.
     */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Lưu ý: selectedMonth bắt đầu từ 0 (tháng 1 là 0)
                    String selectedDate = String.format(Locale.US, "%02d/%02d/%d", selectedMonth + 1, selectedDayOfMonth, selectedYear);
                    binding.savereleaseandaddsong.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    /**
     * Thu thập tất cả dữ liệu từ các trường input và hiển thị chúng.
     */
    private void collectAndShowData() {
        StringBuilder summary = new StringBuilder();
        summary.append("--- SONG DETAILS ---").append("\n");

        // Title
        summary.append("Title: ").append(binding.editTitle.getText().toString()).append("\n");

        // Artists
        summary.append("Artists: ");
        for (int i = 0; i < binding.chipGroupArtists.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupArtists.getChildAt(i);
            summary.append(chip.getText().toString());
            if (i < binding.chipGroupArtists.getChildCount() - 1) {
                summary.append(", ");
            }
        }
        summary.append("\n");

        // Explicit Lyrics
        // Lấy ID của RadioButton được chọn từ RadioGroup
        int selectedExplicitId = binding.radioGroupExplicit.getCheckedRadioButtonId();
        if (selectedExplicitId != -1) { // -1 nghĩa là không có nút nào được chọn
            RadioButton explicitRadio = findViewById(selectedExplicitId);
            summary.append("Explicit Lyrics: ").append(explicitRadio.getText()).append("\n");
        } else {
            summary.append("Explicit Lyrics: Not selected").append("\n");
        }

        // Language
        summary.append("Language: ").append(binding.editLanguage.getText().toString()).append("\n");

        // Genres
        summary.append("Primary Genre: ").append(binding.spinnerPrimaryGenre.getSelectedItem().toString()).append("\n");
        summary.append("Secondary Genre: ").append(binding.spinnerSecondaryGenre.getSelectedItem().toString()).append("\n");

        // Release Date
        summary.append("Release Date: ").append(binding.editReleaseDate.getText().toString()).append("\n");

        // Previously Released
        // Lấy ID của RadioButton được chọn từ RadioGroup
        int selectedReleasedId = binding.radioGroupReleased.getCheckedRadioButtonId();
        if (selectedReleasedId != -1) {
            RadioButton releasedRadio = findViewById(selectedReleasedId);
            summary.append("Previously Released: ").append(releasedRadio.getText()).append("\n");
        } else {
            summary.append("Previously Released: Not selected").append("\n");
        }

        // Hiển thị kết quả ra Logcat (Dễ xem hơn Toast nếu text dài)
        Log.d(TAG, summary.toString());

        // Hiển thị một Toast ngắn để báo hiệu đã lưu
        Toast.makeText(this, "Data collected! Check Logcat for details.", Toast.LENGTH_LONG).show();
    }
}
