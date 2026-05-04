package com.example.todoapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true) public int id;
    public String title, description, priority, category, dueDate, reminderTime, status;
    public long userId;

    public boolean isDone() { return "Done".equals(status); }
}
