package com.example.todoapp;

import com.example.todoapp.db.TaskEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * UI model wrapping the Room entity.
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private String priority;
    private String category;
    private String dueDate;
    private String reminder;
    private String status;
    private List<String> subtasks;

    public Task(int id, String title, String description, String priority,
                String category, String dueDate, String reminder, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
        this.reminder = reminder;
        this.status = status;
        this.subtasks = new ArrayList<>();
    }

    public static Task fromEntity(TaskEntity e) {
        return new Task(e.id, e.title, e.description, e.priority,
                e.category, e.dueDate, e.reminderTime, e.status);
    }

    public TaskEntity toEntity(long userId) {
        TaskEntity e = new TaskEntity();
        e.id           = id;
        e.title        = title;
        e.description  = description;
        e.priority     = priority;
        e.category     = category;
        e.dueDate      = dueDate;
        e.reminderTime = reminder;
        e.status       = status;
        e.userId       = userId;
        return e;
    }

    public int getId()          { return id; }
    public String getTitle()    { return title; }
    public void setTitle(String t)  { this.title = t; }
    public String getDescription()  { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getPriority() { return priority; }
    public void setPriority(String p) { this.priority = p; }
    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }
    public String getDueDate()  { return dueDate; }
    public void setDueDate(String d) { this.dueDate = d; }
    public String getReminder() { return reminder; }
    public void setReminder(String r) { this.reminder = r; }
    public String getStatus()   { return status; }
    public void setStatus(String s) { this.status = s; }
    public List<String> getSubtasks() { return subtasks; }
    public void addSubtask(String s) { subtasks.add(s); }
    public boolean isDone()     { return "Done".equals(status); }
}
