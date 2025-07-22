package com.example.vmusic.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentProfileBinding;
import com.example.vmusic.databinding.FragmentSettingsBinding;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.viewmodel.ProfileViewModel;


public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = com.example.vmusic.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        TextView tvUserName = binding.tvUsername;
        SessionManager sessionManager = new SessionManager(requireContext());
        tvUserName.setText(sessionManager.getUsername() != null ? sessionManager.getUsername() : "Guest");

        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        binding.btnBackToSettings.setOnClickListener(v -> {
            navController.navigateUp();
        });

        ImageView avatarImageView = binding.avatar;
        LinearLayout headerLayout = binding.headerLayout;

        Drawable drawable = avatarImageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    if (palette != null) {
                        int primaryColor = 0;
                        if (palette.getDarkVibrantColor(0) != 0) {
                            primaryColor = palette.getDarkVibrantColor(0);
                        } else if (palette.getVibrantColor(0) != 0) {
                            primaryColor = palette.getVibrantColor(0);
                        } else if (palette.getDominantColor(0) != 0) {
                            primaryColor = palette.getDominantColor(0);
                        }
                        if (primaryColor != 0) {
                            int[] colors = new int[]{
                                    primaryColor,
                                    Color.TRANSPARENT
                            };

                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    colors
                            );
                            headerLayout.setBackground(gradientDrawable);
                        } else {
                            headerLayout.setBackgroundResource(R.drawable.green_gradient);
                        }
                    } else {
                        headerLayout.setBackgroundResource(R.drawable.green_gradient);
                    }
                }
            });
        }else {
            headerLayout.setBackgroundResource(R.drawable.green_gradient);
        }
    }
}