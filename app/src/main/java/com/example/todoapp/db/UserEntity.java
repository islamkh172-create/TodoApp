package com.example.todoapp.db;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = "username", unique = true)})
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String passwordHash;
    public String email;
    public String fullName;
}
