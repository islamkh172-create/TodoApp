package com.example.todoapp;

import android.content.Context;
import com.example.todoapp.db.AppDatabase;
import com.example.todoapp.db.TaskDao;
import com.example.todoapp.db.TaskEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Thin wrapper around Room that keeps the rest of the app
 * decoupled from the DAO layer.
 */
public class TaskStore {

    private static TaskStore instance;
    private TaskDao dao;
    private long userId;

    private TaskStore() {}

    public static TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }

    /** Call once after login before using any other method. */
    public void init(Context context, long userId) {
        this.dao    = AppDatabase.getInstance(context).taskDao();
        this.userId = userId;
    }

    public int addTask(Task task) {
        TaskEntity e = task.toEntity(userId);
        e.id = 0; // let Room auto-generate
        long newId = dao.insert(e);
        return (int) newId;
    }

    public void updateTask(Task task) {
        dao.update(task.toEntity(userId));
    }

    public void removeTask(int id) {
        TaskEntity e = dao.getById(id);
        if (e != null) dao.delete(e);
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> entities = dao.getTasksForUser(userId);
        List<Task> result = new ArrayList<>();
        for (TaskEntity e : entities) result.add(Task.fromEntity(e));
        return result;
    }

    public int countByStatus(String status) {
        return dao.countByStatus(userId, status);
    }

    public int countByCategory(String category) {
        return dao.countByCategory(userId, category);
    }
}
