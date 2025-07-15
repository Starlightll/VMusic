package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.utils.HashUtil;

import com.example.vmusic.entity.User;
import com.example.vmusic.repository.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public MutableLiveData<User> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void login(String email, String password) {
        String hash = HashUtil.sha256(password);
        userRepository.login(email, hash, loginResult);
    }

}
