package com.example.vmusic.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.vmusic.entity.Genre;
import com.example.vmusic.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUserLive();

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE userName = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    User login(String email, String passwordHash);

    @Query("SELECT COUNT(*) FROM users")
    int countUsers();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(User... users);
}
