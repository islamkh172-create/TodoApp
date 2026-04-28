package com.example.todoapp.db;

import androidx.room.*;

@Dao
public interface UserDao {
    @Insert
    long insert(UserEntity user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    UserEntity findByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity findById(int id);
}
