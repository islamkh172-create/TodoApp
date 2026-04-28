package com.example.todoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReminderWorker extends Worker {

    public static final String KEY_TITLE  = "task_title";
    public static final String KEY_DESC   = "task_desc";
    public static final String CHANNEL_ID = "todo_reminders";

    public ReminderWorker(@NonNull Context ctx, @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(KEY_TITLE);
        String desc  = getInputData().getString(KEY_DESC);
        sendNotification(title, desc);
        return Result.success();
    }

    private void sendNotification(String title, String desc) {
        NotificationManager nm = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel (required API 26+)
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Task Reminders", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Reminders for your to-do tasks");
        nm.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("⏰ Reminder: " + title)
                .setContentText(desc != null && !desc.isEmpty() ? desc : "Time to work on your task!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
