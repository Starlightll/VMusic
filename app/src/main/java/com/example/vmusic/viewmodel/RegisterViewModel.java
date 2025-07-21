package com.example.vmusic.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.entity.Playlist;
import com.example.vmusic.entity.User;
import com.example.vmusic.repository.PlaylistRepository;
import com.example.vmusic.repository.UserRepository;
import com.example.vmusic.utils.HashUtil;

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
