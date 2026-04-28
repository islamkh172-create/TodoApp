package com.example.todoapp;

import androidx.room.*;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true) public int id;
    public String title, description, priority, category, dueDate, reminderTime, status;
    public long userId;

    public boolean isDone() { return "Done".equals(status); }
}