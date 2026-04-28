package com.example.todoapp.db;

import androidx.room.*;
import com.example.todoapp.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    List<Task> getAll(long userId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getById(int id);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = :status")
    int countStatus(long userId, String status);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND LOWER(category) = LOWER(:category)")
    int countCategory(long userId, String category);
}
