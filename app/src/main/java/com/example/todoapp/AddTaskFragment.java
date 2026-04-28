package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.*;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddTaskFragment extends Fragment {

    private String selectedPriority = "Medium";
    private String selectedDueDate  = "";
    private String selectedReminder = ""; // "HH:mm"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_task, container, false);

        EditText inputTitle       = root.findViewById(R.id.input_task_title);
        EditText inputDescription = root.findViewById(R.id.input_task_description);
        EditText inputCategory    = root.findViewById(R.id.input_task_category);
        TextView textDueDate      = root.findViewById(R.id.text_due_date);
        TextView textReminder     = root.findViewById(R.id.text_reminder);
        TextView btnHigh          = root.findViewById(R.id.btn_priority_high);
        TextView btnMedium        = root.findViewById(R.id.btn_priority_medium);
        TextView btnLow           = root.findViewById(R.id.btn_priority_low);
        TextView btnSave          = root.findViewById(R.id.btn_save_task);
        TextView btnBack          = root.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Priority
        View.OnClickListener priorityClick = v -> {
            resetPriority(btnHigh, btnMedium, btnLow);
            if (v.getId() == R.id.btn_priority_high)   { selectedPriority = "High";   highlight(btnHigh,   "#C0392B"); }
            else if (v.getId() == R.id.btn_priority_medium) { selectedPriority = "Medium"; highlight(btnMedium, "#E65100"); }
            else                                        { selectedPriority = "Low";    highlight(btnLow,    "#2E7D32"); }
        };
        btnHigh.setOnClickListener(priorityClick);
        btnMedium.setOnClickListener(priorityClick);
        btnLow.setOnClickListener(priorityClick);

        // Date picker
        textDueDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                selectedDueDate = String.format(Locale.getDefault(), "%d/%02d/%02d", y, m + 1, d);
                textDueDate.setText(selectedDueDate);
                textDueDate.setTextColor(0xFF1A1A2E);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker
        textReminder.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(requireContext(), (tp, h, min) -> {
                selectedReminder = String.format(Locale.getDefault(), "%02d:%02d", h, min);
                textReminder.setText(selectedReminder);
                textReminder.setTextColor(0xFF1A1A2E);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // Save
        btnSave.setOnClickListener(v -> {
            String title = inputTitle.getText().toString().trim();
            String desc  = inputDescription.getText().toString().trim();
            String cat   = inputCategory.getText().toString().trim();

            if (title.isEmpty()) { inputTitle.setError("Title is required"); return; }
            if (cat.isEmpty()) cat = "General";

            Task task = new Task(0, title, desc, selectedPriority, cat,
                    selectedDueDate, selectedReminder, "In Progress");
            int newId = TaskStore.getInstance().addTask(task);

            // Schedule notification if reminder was set
            if (!selectedReminder.isEmpty()) {
                scheduleReminder(newId, title, desc, selectedReminder);
            }

            Toast.makeText(requireContext(), "Task saved!", Toast.LENGTH_SHORT).show();
            if (requireActivity() instanceof Mainactivity) {
                ((Mainactivity) requireActivity()).navigateToTasks();
            } else {
                requireActivity().onBackPressed();
            }
        });

        return root;
    }

    private void scheduleReminder(int taskId, String title, String desc, String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int min  = Integer.parseInt(parts[1]);

            Calendar target = Calendar.getInstance();
            target.set(Calendar.HOUR_OF_DAY, hour);
            target.set(Calendar.MINUTE, min);
            target.set(Calendar.SECOND, 0);

            long now   = System.currentTimeMillis();
            long delay = target.getTimeInMillis() - now;
            if (delay <= 0) delay += TimeUnit.DAYS.toMillis(1); // tomorrow

            Data data = new Data.Builder()
                    .putString(ReminderWorker.KEY_TITLE, title)
                    .putString(ReminderWorker.KEY_DESC,  desc)
                    .build();

            OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("reminder_" + taskId)
                    .build();

            WorkManager.getInstance(requireContext()).enqueue(req);
        } catch (Exception ignored) {}
    }

    private void highlight(TextView tv, String color) {
        tv.setBackgroundColor(android.graphics.Color.parseColor(color));
        tv.setTextColor(android.graphics.Color.WHITE);
    }

    private void resetPriority(TextView high, TextView medium, TextView low) {
        high.setBackgroundColor(0xFFFFEBEE); high.setTextColor(0xFFC0392B);
        medium.setBackgroundColor(0xFFFFF3E0); medium.setTextColor(0xFFE65100);
        low.setBackgroundColor(0xFFE8F5E9);  low.setTextColor(0xFF2E7D32);
    }
}
