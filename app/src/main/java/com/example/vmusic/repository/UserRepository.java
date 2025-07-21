package com.example.vmusic.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.dao.UserDao;
import com.example.vmusic.database.AppDatabase;
import com.example.vmusic.entity.User;

import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public void register(User user, MutableLiveData<Long> registerResult) {
        Executors.newSingleThreadExecutor().execute(() -> {
            long id = userDao.insert(user);
            registerResult.postValue(id);
        });
    }

    public void login(String email, String passwordHash, MutableLiveData<User> userLiveData) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.login(email, passwordHash);
            userLiveData.postValue(user);
        });
    }

    public void getUserByEmail(String email, MutableLiveData<User> userLiveData) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(email);
            userLiveData.postValue(user);
        });
    }
}
