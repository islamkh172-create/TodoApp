package com.example.todoapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private LinearLayout taskListContainer;
    private EditText inputSearch;
    private TextView filterAll, filterInProgress, filterDone;
    private String activeFilter = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks, container, false);

        taskListContainer = root.findViewById(R.id.task_list_container);
        inputSearch       = root.findViewById(R.id.input_search);
        filterAll         = root.findViewById(R.id.filter_all);
        filterInProgress  = root.findViewById(R.id.filter_in_progress);
        filterDone        = root.findViewById(R.id.filter_done);

        filterAll.setOnClickListener(v -> applyFilter("All"));
        filterInProgress.setOnClickListener(v -> applyFilter("In Progress"));
        filterDone.setOnClickListener(v -> applyFilter("Done"));

        inputSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) { renderTasks(); }
            public void afterTextChanged(Editable s) {}
        });

        root.findViewById(R.id.btn_add_task).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AddTaskFragment())
                        .addToBackStack(null)
                        .commit());

        renderTasks();
        return root;
    }

    @Override public void onResume() { super.onResume(); renderTasks(); }

    private void applyFilter(String filter) {
        activeFilter = filter;
        int active = 0xFF1A237E, inactive = 0xFFE8EAF6;
        int textActive = 0xFFFFFFFF, textInactive = 0xFF1A237E;
        filterAll.setBackgroundColor("All".equals(filter) ? active : inactive);
        filterAll.setTextColor("All".equals(filter) ? textActive : textInactive);
        filterInProgress.setBackgroundColor("In Progress".equals(filter) ? active : inactive);
        filterInProgress.setTextColor("In Progress".equals(filter) ? textActive : textInactive);
        filterDone.setBackgroundColor("Done".equals(filter) ? active : inactive);
        filterDone.setTextColor("Done".equals(filter) ? textActive : textInactive);
        renderTasks();
    }

    private void renderTasks() {
        if (taskListContainer == null) return;
        taskListContainer.removeAllViews();

        String query = inputSearch != null ? inputSearch.getText().toString().trim().toLowerCase() : "";
        List<Task> filtered = new ArrayList<>();
        for (Task t : TaskStore.getInstance().getAllTasks()) {
            boolean matchFilter = "All".equals(activeFilter) || t.getStatus().equalsIgnoreCase(activeFilter);
            boolean matchSearch = query.isEmpty()
                    || t.getTitle().toLowerCase().contains(query)
                    || t.getCategory().toLowerCase().contains(query);
            if (matchFilter && matchSearch) filtered.add(t);
        }

        if (filtered.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("No tasks yet. Tap '+ Add New Task' to get started!");
            empty.setTextColor(0xFF888888);
            empty.setPadding(40, 48, 40, 0);
            empty.setGravity(android.view.Gravity.CENTER);
            taskListContainer.addView(empty);
            return;
        }

        int dp = Math.round(requireContext().getResources().getDisplayMetrics().density);
        for (Task t : filtered) taskListContainer.addView(buildTaskCard(t, dp));
    }

    private LinearLayout buildTaskCard(Task task, int dp) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(0xFFFFFFFF);
        card.setPadding(14*dp, 14*dp, 14*dp, 14*dp);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(10*dp, 0, 10*dp, 8*dp);
        card.setLayoutParams(p);

        // Row1: title + priority
        LinearLayout row1 = new LinearLayout(requireContext());
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(task.getTitle());
        tvTitle.setTextColor(0xFF1A1A2E);
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        if (task.isDone()) tvTitle.setPaintFlags(tvTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        row1.addView(tvTitle);

        TextView tvPri = new TextView(requireContext());
        tvPri.setText(task.getPriority());
        tvPri.setTextSize(10);
        tvPri.setPadding(8*dp, 3*dp, 8*dp, 3*dp);
        switch (task.getPriority()) {
            case "High":   tvPri.setBackgroundColor(0xFFFFEBEE); tvPri.setTextColor(0xFFC0392B); break;
            case "Medium": tvPri.setBackgroundColor(0xFFFFF3E0); tvPri.setTextColor(0xFFE65100); break;
            default:       tvPri.setBackgroundColor(0xFFE8F5E9); tvPri.setTextColor(0xFF2E7D32); break;
        }
        row1.addView(tvPri);
        card.addView(row1);

        // Description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            TextView tvDesc = new TextView(requireContext());
            tvDesc.setText(task.getDescription());
            tvDesc.setTextColor(0xFF888888);
            tvDesc.setTextSize(12);
            LinearLayout.LayoutParams dp4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dp4.topMargin = 4*dp;
            tvDesc.setLayoutParams(dp4);
            card.addView(tvDesc);
        }

        // Row3: category + due + spacer + toggle
        LinearLayout row3 = new LinearLayout(requireContext());
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams r3p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        r3p.topMargin = 8*dp;
        row3.setLayoutParams(r3p);

        TextView tvCat = new TextView(requireContext());
        tvCat.setText(task.getCategory());
        tvCat.setTextSize(10);
        tvCat.setPadding(8*dp, 2*dp, 8*dp, 2*dp);
        tvCat.setBackgroundColor(0xFFE8EAF6);
        tvCat.setTextColor(0xFF1A237E);
        row3.addView(tvCat);

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            TextView tvDue = new TextView(requireContext());
            tvDue.setText("📅 " + task.getDueDate());
            tvDue.setTextColor(0xFF999999);
            tvDue.setTextSize(10);
            LinearLayout.LayoutParams dp8 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dp8.setMarginStart(8*dp);
            tvDue.setLayoutParams(dp8);
            row3.addView(tvDue);
        }

        if (task.getReminder() != null && !task.getReminder().isEmpty()) {
            TextView tvRem = new TextView(requireContext());
            tvRem.setText("⏰ " + task.getReminder());
            tvRem.setTextColor(0xFF999999);
            tvRem.setTextSize(10);
            LinearLayout.LayoutParams dp8r = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dp8r.setMarginStart(8*dp);
            tvRem.setLayoutParams(dp8r);
            row3.addView(tvRem);
        }

        View spacer = new View(requireContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        row3.addView(spacer);

        TextView tvStatus = new TextView(requireContext());
        tvStatus.setText(task.isDone() ? "✓ Done" : "Mark Done");
        tvStatus.setTextSize(10);
        tvStatus.setPadding(8*dp, 3*dp, 8*dp, 3*dp);
        tvStatus.setBackgroundColor(task.isDone() ? 0xFFE8F5E9 : 0xFFE8EAF6);
        tvStatus.setTextColor(task.isDone() ? 0xFF2E7D32 : 0xFF1A237E);
        tvStatus.setOnClickListener(v -> {
            task.setStatus(task.isDone() ? "In Progress" : "Done");
            TaskStore.getInstance().updateTask(task);
            renderTasks();
        });
        row3.addView(tvStatus);
        card.addView(row3);

        // Long press = delete
        card.setOnLongClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Delete \"" + task.getTitle() + "\"?")
                    .setPositiveButton("Delete", (d, w) -> {
                        TaskStore.getInstance().removeTask(task.getId());
                        renderTasks();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        return card;
    }
}
