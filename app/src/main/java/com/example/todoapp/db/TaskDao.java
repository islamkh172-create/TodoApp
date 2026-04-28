package com.example.todoapp.db;

import androidx.room.*;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(TaskEntity task);

    @Update
    void update(TaskEntity task);

    @Delete
    void delete(TaskEntity task);

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    List<TaskEntity> getTasksForUser(long userId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    TaskEntity getById(int id);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = :status")
    int countByStatus(long userId, String status);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND LOWER(category) = LOWER(:category)")
    int countByCategory(long userId, String category);
}
