package com.example.todoapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.todoapp.R;
import com.example.todoapp.activities.Mainactivity;
import com.example.todoapp.db.Task;
import com.example.todoapp.workers.ReminderWorker;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddTaskFragment extends BaseFragment {

    private String priority = "Medium", dueDate = "", reminder = "";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View root = inf.inflate(R.layout.add_task, c, false);

        EditText etTitle = root.findViewById(R.id.input_task_title);
        EditText etDesc  = root.findViewById(R.id.input_task_description);
        EditText etCat   = root.findViewById(R.id.input_task_category);
        TextView tvDate  = root.findViewById(R.id.text_due_date);
        TextView tvRem   = root.findViewById(R.id.text_reminder);
        TextView btnHigh = root.findViewById(R.id.btn_priority_high);
        TextView btnMed  = root.findViewById(R.id.btn_priority_medium);
        TextView btnLow  = root.findViewById(R.id.btn_priority_low);

        root.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());

        View.OnClickListener priClick = v -> {
            resetPri(btnHigh, btnMed, btnLow);
            if      (v.getId() == R.id.btn_priority_high)   { priority = "High";   btnHigh.setBackgroundColor(0xFFC0392B); btnHigh.setTextColor(0xFFFFFFFF); }
            else if (v.getId() == R.id.btn_priority_medium) { priority = "Medium"; btnMed.setBackgroundColor(0xFFE65100);  btnMed.setTextColor(0xFFFFFFFF); }
            else                                             { priority = "Low";    btnLow.setBackgroundColor(0xFF2E7D32);  btnLow.setTextColor(0xFFFFFFFF); }
        };
        btnHigh.setOnClickListener(priClick);
        btnMed.setOnClickListener(priClick);
        btnLow.setOnClickListener(priClick);

        tvDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                dueDate = String.format(Locale.getDefault(), "%d/%02d/%02d", y, m+1, d);
                tvDate.setText(dueDate); tvDate.setTextColor(0xFF1A1A2E);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvRem.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(requireContext(), (tp, h, min) -> {
                reminder = String.format(Locale.getDefault(), "%02d:%02d", h, min);
                tvRem.setText(reminder); tvRem.setTextColor(0xFF1A1A2E);
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        root.findViewById(R.id.btn_save_task).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) { etTitle.setError("Title is required"); return; }

            Task task = new Task();
            task.title        = title;
            task.description  = etDesc.getText().toString().trim();
            task.priority     = priority;
            task.category     = etCat.getText().toString().trim().isEmpty() ? "General" : etCat.getText().toString().trim();
            task.dueDate      = dueDate;
            task.reminderTime = reminder;
            task.status       = "In Progress";
            task.userId       = uid();
            long newId = dao().insert(task);

            if (!reminder.isEmpty()) scheduleReminder((int) newId, task.title, task.description, reminder);

            Toast.makeText(requireContext(), "Task saved!", Toast.LENGTH_SHORT).show();
            if (requireActivity() instanceof Mainactivity)
                ((Mainactivity) requireActivity()).navigateToTasks();
            else
                requireActivity().onBackPressed();
        });

        return root;
    }

    private void scheduleReminder(int id, String title, String desc, String time) {
        try {
            String[] p = time.split(":");
            Calendar t = Calendar.getInstance();
            t.set(Calendar.HOUR_OF_DAY, Integer.parseInt(p[0]));
            t.set(Calendar.MINUTE, Integer.parseInt(p[1]));
            t.set(Calendar.SECOND, 0);
            long delay = t.getTimeInMillis() - System.currentTimeMillis();
            if (delay <= 0) delay += TimeUnit.DAYS.toMillis(1);

            WorkManager.getInstance(requireContext()).enqueue(
                    new OneTimeWorkRequest.Builder(ReminderWorker.class)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(new Data.Builder()
                                    .putString(ReminderWorker.KEY_TITLE, title)
                                    .putString(ReminderWorker.KEY_DESC, desc).build())
                            .addTag("reminder_" + id).build());
        } catch (Exception ignored) {}
    }

    private void resetPri(TextView h, TextView m, TextView l) {
        h.setBackgroundColor(0xFFFFEBEE); h.setTextColor(0xFFC0392B);
        m.setBackgroundColor(0xFFFFF3E0); m.setTextColor(0xFFE65100);
        l.setBackgroundColor(0xFFE8F5E9); l.setTextColor(0xFF2E7D32);
    }
}
