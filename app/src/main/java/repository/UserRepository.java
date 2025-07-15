package repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executors;

import dao.UserDao;
import database.AppDatabase;
import entity.User;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public void register(User user) {
        Executors.newSingleThreadExecutor().execute(() -> userDao.insert(user));
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
