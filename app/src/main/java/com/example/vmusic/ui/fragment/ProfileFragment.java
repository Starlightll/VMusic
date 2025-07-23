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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentProfileBinding;
import com.example.vmusic.databinding.FragmentSettingsBinding;
import com.example.vmusic.entity.User;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.UserProfile;
import com.example.vmusic.viewmodel.ProfileViewModel;
import com.example.vmusic.viewmodel.UserProfileViewModel;


public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    private UserProfileViewModel userProfileViewModel;
    private ImageView avatarImageView;
    private Button btnEditProfile;
    private String username;
    private String avatarUrl;
    private LiveData<UserProfile> user;
    private int userId;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = com.example.vmusic.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        TextView tvUserName = binding.tvUsername;
        btnEditProfile = binding.btnEditProfile;
        avatarImageView = binding.avatar;
        user = userProfileViewModel.getCurrentUser();
        username = getUsername();
        avatarUrl = getAvatarUrl();
        SessionManager sessionManager = new SessionManager(requireContext());
        tvUserName.setText(sessionManager.getUsername() != null ? sessionManager.getUsername() : "Guest");
        Glide.with(this)
                .load(sessionManager.getUserAvatar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(avatarImageView);
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
        LinearLayout headerLayout = binding.headerLayout;

        btnEditProfile.setOnClickListener(v -> {
            UpdateProfileBottomSheetFragment bottomSheet = UpdateProfileBottomSheetFragment.newInstance(username, avatarUrl);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });

        userId = getCurrentUserId();
        if (userId == -1) {
            btnEditProfile.setVisibility(View.GONE);
            return;
        }else{
            btnEditProfile.setVisibility(View.VISIBLE);
        }

//        if (avatarImageView != null) {
//            Glide.with(this)
//                    .asBitmap()
//                    .load(avatarImageView)
//                    .into(new com.bumptech.glide.request.target.SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            Palette.from(resource).generate(palette -> {
//                                if (palette != null) {
//                                    int vibrantColor = palette.getVibrantColor(Color.BLACK);
//                                    int darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK);
//                                    int[] colors = new int[]{
//                                            darkVibrantColor,
//                                            Color.TRANSPARENT
//                                    };
//
//                                    GradientDrawable gradientDrawable = new GradientDrawable(
//                                            GradientDrawable.Orientation.TOP_BOTTOM,
//                                            colors
//                                    );
//                                    headerLayout.setBackground(gradientDrawable);
//                                    headerLayout.setBackground(gradientDrawable);
//                                }
//                            });
//                        }
//                    });
//        }

        ImageView avatarImageView = binding.avatar;


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

    private int getCurrentUserId() {
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.isLoggedIn()) {
            return sessionManager.getUserId();
        } else {
            return -1;
        }
    }

    private String getUsername() {
        SessionManager sessionManager = new SessionManager(requireContext());
        return sessionManager.getUsername() != null ? sessionManager.getUsername() : "Guest";
    }

    private String getAvatarUrl() {
        SessionManager sessionManager = new SessionManager(requireContext());
        return sessionManager.getUserAvatar() != null ? sessionManager.getUserAvatar() : "";
    }
}