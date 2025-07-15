package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.utils.HashUtil;

import com.example.vmusic.entity.User;
import com.example.vmusic.repository.UserRepository;

public class RegisterViewModel extends AndroidViewModel {
    private final UserRepository repo;
    public MutableLiveData<User> registerResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application app) {
        super(app);
        repo = new UserRepository(app);
    }

    public void register(String email, String password) {
        MutableLiveData<User> existCheck = new MutableLiveData<>();
        repo.getUserByEmail(email, existCheck);

        existCheck.observeForever(existing -> {
            if (existing != null) {
                registerResult.setValue(null);
            } else {
                User user = new User();
                user.setEmail(email);
                user.setUserName("User_" + email.split("@")[0]); // Default username based on email;
                user.setPasswordHash(HashUtil.sha256(password));
                user.setRole("user");

                repo.register(user);
                registerResult.setValue(user);
            }
        });
    }
}
