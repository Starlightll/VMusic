package com.example.vmusic.ui.fragment;

import android.app.Dialog;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.models.UserProfile;
import com.example.vmusic.viewmodel.UserProfileViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileBottomSheetFragment extends BottomSheetDialogFragment {

    private Button btnEditProfile;
    private UserProfile userProfile;
    private UserProfileViewModel userProfileViewModel;
    private LiveData<UserProfile> updateProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "username";
    private static final String ARG_PARAM2 = "avatarUrl";

    // TODO: Rename and change types of parameters
    private String username;
    private String avatarUrl;

    private EditText usernameEditText;
    private CircleImageView avatarImageView;
    private TextView btnChangeAvatar, btnCancel, btnSave;

    private Uri tempAvatarUri;
    private ActivityResultLauncher<String> pickImageLauncher;


    public UpdateProfileBottomSheetFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UpdateProfileBottomSheetFragment newInstance(String username, String avatarUrl) {
        UpdateProfileBottomSheetFragment fragment = new UpdateProfileBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, username);
        args.putString(ARG_PARAM2, avatarUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);

        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            avatarUrl = getArguments().getString(ARG_PARAM2);
            userProfile = new UserProfile();
            userProfile.setUsername(username);
            userProfile.setAvatarUrl(getArguments().getString(avatarUrl, ""));
        } else {
            userProfile = new UserProfile();
        }

        setStyle(STYLE_NORMAL, R.style.CustomBottomUpdateProfileSheetDialogTheme);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        tempAvatarUri = uri;
                        Glide.with(this).load(tempAvatarUri).into(avatarImageView);
                        updateSaveButtonState();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile_bottom_sheet, container, false);
        usernameEditText = view.findViewById(R.id.text_display_name);
        avatarImageView = view.findViewById(R.id.profile_image);
        btnChangeAvatar = view.findViewById(R.id.text_change_photo);
        btnCancel = view.findViewById(R.id.button_cancel);
        btnSave = view.findViewById(R.id.button_save);
        btnSave.setEnabled(false);


        usernameEditText.setText(userProfile.getUsername());
        if (userProfile.getAvatarUrl() != null && !userProfile.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(userProfile.getAvatarUrl())
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder của bạn
                    .into(avatarImageView);
        } else {
            avatarImageView.setImageResource(R.drawable.bluelight_gradient);
        }

        updateProfile = new LiveData<UserProfile>() {
            @Override
            protected void onActive() {
                super.onActive();
                setValue(userProfile);
            }
            @Override
            protected void onInactive() {
                super.onInactive();
                // Handle any cleanup if necessary
            }
        };

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        usernameEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable save button if text is changed
                btnSave.setEnabled(!s.toString().equals(userProfile.getUsername()));
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed
            }
        });

        btnChangeAvatar.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });



        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String newUsername = usernameEditText.getText().toString().trim();

            if (!newUsername.equals(userProfile.getUsername())) {
                userProfileViewModel.updateUsername(newUsername);
            }

            if (tempAvatarUri != null) {
                userProfileViewModel.updateAvatarUrl(tempAvatarUri.toString());
                Toast.makeText(getContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getContext(), "Hồ sơ đã được cập nhật.", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        updateSaveButtonState();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                int desiredHeight = (int) (screenHeight * 0.95);

                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeight);
                } else {
                    layoutParams.height = desiredHeight;
                }
                bottomSheet.setLayoutParams(layoutParams);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }


    private void updateSaveButtonState() {
        String currentUsername = usernameEditText.getText().toString().trim();
        String currentAvatarUrl = (tempAvatarUri != null) ? tempAvatarUri.toString() : userProfile.getAvatarUrl();

        boolean hasChanges = userProfileViewModel.hasChanges(currentUsername, currentAvatarUrl);
        btnSave.setEnabled(hasChanges);

        if (hasChanges) {
            btnSave.setTextColor(getResources().getColor(R.color.primary, null));
        } else {
            btnSave.setTextColor(getResources().getColor(R.color.gray, null));
        }
    }


}

