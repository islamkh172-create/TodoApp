package com.example.todoapp.db;

import android.content.Context;
import androidx.room.*;
import com.example.todoapp.Task;

@Database(entities = {Task.class, UserEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) synchronized (AppDatabase.class) {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                                AppDatabase.class, "todoapp.db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
        }
        return INSTANCE;
    }
}