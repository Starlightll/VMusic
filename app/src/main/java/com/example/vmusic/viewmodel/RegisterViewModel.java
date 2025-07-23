package com.example.vmusic.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.R;
import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.User;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.UserRepository;
import com.example.vmusic.utils.HashUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterViewModel extends AndroidViewModel {
    private final UserRepository repo;
    private final PlaylistRepository playlistRepo;
    public MutableLiveData<User> registerResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application app) {
        super(app);
        repo = new UserRepository(app);
        playlistRepo = new PlaylistRepository(app);
    }

    public void register(String email, String password) {
        MutableLiveData<User> existCheck = new MutableLiveData<>();
        repo.getUserByEmail(email, existCheck);

        existCheck.observeForever(existing -> {
            if (existing != null) {
                registerResult.setValue(null);
                Toast.makeText(getApplication(), "Email already exists", Toast.LENGTH_SHORT).show();
            } else {
                User user = new User();
                user.setEmail(email);
                user.setUserName("User_" + email.split("@")[0]);
                user.setPasswordHash(HashUtil.sha256(password));
                user.setRole("user");

                //Create folder to store user image inside app's internal storage
                File userDir = new File(getApplication().getFilesDir(), "users");
                if (!userDir.exists()) {
                    userDir.mkdirs();
                }
                Bitmap defaultAvatar = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.default_avt);
                String fileName = "user_" + email.replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
                File avatarFile = new File(userDir, fileName);

                try (FileOutputStream out = new FileOutputStream(avatarFile)) {
                    defaultAvatar.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    String avatarPath = avatarFile.getAbsolutePath();
                    user.setAvatarUrl(avatarPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MutableLiveData<Long> insertedId = new MutableLiveData<>();
                repo.register(user, insertedId);

                insertedId.observeForever(userId -> {
                    if (userId != null && userId > 0) {
                        user.setUserId(userId.intValue());
                        Playlist playlist = new Playlist(0, "Bài hát ưa thích", "Favorite", user.getUserId());
                        playlistRepo.insert(playlist);

                        registerResult.setValue(user);
                    }
                });
            }
        });
    }
}
