package com.example.todoapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.stats, container, false);
        updateStats(root);
        return root;
    }

    @Override public void onResume() { super.onResume(); if (getView() != null) updateStats(getView()); }

    private void updateStats(View root) {
        TaskStore store = TaskStore.getInstance();
        setStatCard(root, R.id.card_stat_completed,  store.countByStatus("Done"));
        setStatCard(root, R.id.card_stat_in_progress, store.countByStatus("In Progress"));
        setStatCard(root, R.id.card_stat_total,       store.getAllTasks().size());

        // Hide placeholder static rows
        for (int id : new int[]{R.id.card_history_groceries, R.id.card_history_mockup, R.id.card_history_report}) {
            View v = root.findViewById(id);
            if (v != null) v.setVisibility(View.GONE);
        }

        // Dynamic recent tasks
        LinearLayout container = root.findViewById(R.id.stats_history_container);
        if (container == null) return;
        container.removeAllViews();
        int limit = 0;
        for (Task t : store.getAllTasks()) {
            if (limit++ >= 5) break;
            container.addView(buildHistoryRow(t));
        }
    }

    private void setStatCard(View root, int cardId, int value) {
        LinearLayout card = root.findViewById(cardId);
        if (card == null) return;
        try { ((TextView) card.getChildAt(0)).setText(String.valueOf(value)); } catch (Exception ignored) {}
    }

    private LinearLayout buildHistoryRow(Task task) {
        int dp = Math.round(requireContext().getResources().getDisplayMetrics().density);
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(0xFFFFFFFF);
        row.setPadding(14*dp, 14*dp, 14*dp, 14*dp);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(10*dp, 0, 10*dp, 8*dp);
        row.setLayoutParams(rp);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout left = new LinearLayout(requireContext());
        left.setOrientation(LinearLayout.VERTICAL);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(task.getTitle());
        tvTitle.setTextColor(0xFF1A1A2E);
        tvTitle.setTextSize(13);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        left.addView(tvTitle);

        TextView tvCat = new TextView(requireContext());
        tvCat.setText(task.getCategory());
        tvCat.setTextColor(0xFF888888);
        tvCat.setTextSize(11);
        left.addView(tvCat);
        row.addView(left);

        TextView tvStatus = new TextView(requireContext());
        tvStatus.setText(task.getStatus());
        tvStatus.setTextSize(10);
        tvStatus.setPadding(8*dp, 3*dp, 8*dp, 3*dp);
        tvStatus.setBackgroundColor(task.isDone() ? 0xFFE8F5E9 : 0xFFE8EAF6);
        tvStatus.setTextColor(task.isDone() ? 0xFF2E7D32 : 0xFF1A237E);
        row.addView(tvStatus);
        return row;
    }
}
