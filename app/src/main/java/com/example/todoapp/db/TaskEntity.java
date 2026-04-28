package com.example.todoapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String description;
    public String priority;
    public String category;
    public String dueDate;
    public String reminderTime; // "HH:mm" or ""
    public String status;       // "In Progress" | "Done"
    public long userId;         // FK to users table (by id)
}
