package com.example.vmusic.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.R;
import com.example.vmusic.entity.User;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.UserProfile;
import com.example.vmusic.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserProfileViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private MutableLiveData<UserProfile> currentUser;
    private int currentUserId;

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application); // Khởi tạo UserRepository
        currentUser = new MutableLiveData<>();
        loadCurrentUser();
    }

    public LiveData<UserProfile> getCurrentUser() {
        return currentUser;
    }

    public void updateUserProfile(String username, String avatarUrl, int userId) {
        User user = userRepository.getUserById(userId);
        if (user != null) {
            user.setUserName(username);
            user.setAvatarUrl(avatarUrl);
            userRepository.updateUser(user);
            currentUser.setValue(new UserProfile(username, avatarUrl));
        }
    }

    public void updateUsername(String newUsername) {
        if (currentUser.getValue() != null) {
            UserProfile updatedProfile = currentUser.getValue();
            updatedProfile.setUsername(newUsername);
            currentUser.setValue(updatedProfile);
            userRepository.updateUserUsername(currentUserId, newUsername);
        }
    }

    public void updateAvatarUrl(String newAvatarUrl) {
        if (currentUser.getValue() != null) {
            UserProfile updatedProfile = currentUser.getValue();
            updatedProfile.setAvatarUrl(newAvatarUrl);
            currentUser.setValue(updatedProfile);
            User user = userRepository.getUserById(currentUserId);

            File userDir = new File(getApplication().getFilesDir(), "users");
            if (!userDir.exists()) {
                userDir.mkdirs();
            }
            Bitmap avatarBitmap = BitmapFactory.decodeFile(newAvatarUrl);
            String fileName = "user_" + user.getEmail().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
            File avatarFile = new File(userDir, fileName);

            try (FileOutputStream out = new FileOutputStream(avatarFile)) {
                avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                String avatarPath = avatarFile.getAbsolutePath();
                userRepository.updateUserAvatar(currentUserId, avatarPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasChanges(String currentUsernameInView, String currentAvatarUrlInView) {
        UserProfile originalProfile = currentUser.getValue();
        if (originalProfile == null) return false;
        boolean usernameChanged = !originalProfile.getUsername().equals(currentUsernameInView);
        boolean avatarChanged = !originalProfile.getAvatarUrl().equals(currentAvatarUrlInView);
        return usernameChanged || avatarChanged;
    }

    private void loadCurrentUser() {
        SessionManager sessionManager = new SessionManager(getApplication());
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            currentUser.setValue(new UserProfile("", ""));
            return;
        }
        User user = userRepository.getUserById(userId);
        currentUserId = userId;
        if (user != null) {
            currentUser.setValue(new UserProfile(user.getUserName(), user.getAvatarUrl()));
        } else {
            currentUser.setValue(new UserProfile("", ""));
        }
    }
}
