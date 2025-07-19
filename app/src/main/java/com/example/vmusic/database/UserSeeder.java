package com.example.vmusic.database;

import android.content.Context;

import com.example.vmusic.dao.GenreDao;
import com.example.vmusic.dao.UserDao;
import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.User;
import com.example.vmusic.utils.HashUtil;

import java.util.concurrent.ExecutorService;

public class UserSeeder {
    public static void seedUsers(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        UserDao dao = db.userDao();
        ExecutorService executor = AppDatabase.databaseWriteExecutor;

        executor.execute(() -> {
            if (dao.countUsers() == 0) {
                dao.insertAll(getInitialUser());
            }
        });
    }

    private static User[] getInitialUser() {
        return new User[]{
                createUser("admin@gmail.com", "admin", "admin")
        };
    }

    private static User createUser(String email, String password, String role) {
        User user = new User();
        String passwordHash = HashUtil.sha256(password);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash(passwordHash);
        return user;
    }
}
