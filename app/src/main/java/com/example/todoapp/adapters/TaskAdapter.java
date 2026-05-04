package com.example.todoapp.adapters;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.db.Task;
import com.example.todoapp.db.TaskDao;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskChangedListener { void onChanged(); }

    private final List<Task>             tasks;
    private final TaskDao                dao;
    private final OnTaskChangedListener  listener;

    public TaskAdapter(List<Task> tasks, TaskDao dao, OnTaskChangedListener listener) {
        this.tasks    = tasks;
        this.dao      = dao;
        this.listener = listener;
    }

    @NonNull @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(0xFFFFFFFF);
        card.setPadding(dp(parent, 14), dp(parent, 14), dp(parent, 14), dp(parent, 14));
        card.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new TaskViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder h, int position) {
        Task task = tasks.get(position);
        int dp = dp(h.card);

        h.card.removeAllViews();

        // Row 1: title + priority
        LinearLayout row1 = hll(h.card);
        TextView tvTitle = tv(h.card, task.title, 0xFF1A1A2E, 14, true);
        if (task.isDone()) tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView tvPri = tv(h.card, task.priority, priColor(task.priority), 10, false);
        tvPri.setPadding(8*dp, 3*dp, 8*dp, 3*dp);
        tvPri.setBackgroundColor(priBg(task.priority));
        row1.addView(tvTitle); row1.addView(tvPri);
        h.card.addView(row1);

        // Description
        if (task.description != null && !task.description.isEmpty()) {
            TextView tvDesc = tv(h.card, task.description, 0xFF888888, 12, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 4*dp; tvDesc.setLayoutParams(lp);
            h.card.addView(tvDesc);
        }

        // Row 3: category / date / toggle
        LinearLayout row3 = hll(h.card);
        LinearLayout.LayoutParams r3p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        r3p.topMargin = 8*dp; row3.setLayoutParams(r3p);
        row3.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvCat = tv(h.card, task.category, 0xFF1A237E, 10, false);
        tvCat.setPadding(8*dp, 2*dp, 8*dp, 2*dp); tvCat.setBackgroundColor(0xFFE8EAF6);
        row3.addView(tvCat);

        if (notEmpty(task.dueDate))      row3.addView(meta(h.card, "📅 "+task.dueDate, dp));
        if (notEmpty(task.reminderTime)) row3.addView(meta(h.card, "⏰ "+task.reminderTime, dp));

        View sp = new View(h.card.getContext());
        sp.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        row3.addView(sp);

        TextView tvStatus = tv(h.card,
                task.isDone() ? "✓ Done" : "Mark Done",
                task.isDone() ? 0xFF2E7D32 : 0xFF1A237E, 10, false);
        tvStatus.setPadding(8*dp, 3*dp, 8*dp, 3*dp);
        tvStatus.setBackgroundColor(task.isDone() ? 0xFFE8F5E9 : 0xFFE8EAF6);
        tvStatus.setOnClickListener(v -> {
            task.status = task.isDone() ? "In Progress" : "Done";
            dao.update(task);
            listener.onChanged();
        });
        row3.addView(tvStatus);
        h.card.addView(row3);

        // Long-press to delete
        h.card.setOnLongClickListener(v -> {
            new AlertDialog.Builder(h.card.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Delete \"" + task.title + "\"?")
                    .setPositiveButton("Delete", (d, w) -> { dao.delete(task); listener.onChanged(); })
                    .setNegativeButton("Cancel", null).show();
            return true;
        });

        // Bottom margin between cards
        RecyclerView.LayoutParams cardLp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(10*dp, 0, 10*dp, 8*dp);
        h.card.setLayoutParams(cardLp);
    }

    @Override public int getItemCount() { return tasks.size(); }

    // ── helpers ──────────────────────────────────────────────────────────────
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout card;
        TaskViewHolder(LinearLayout card) { super(card); this.card = card; }
    }

    private int dp(View v) {
        return Math.round(v.getContext().getResources().getDisplayMetrics().density);
    }
    private int dp(ViewGroup vg, int n) {
        return Math.round(vg.getContext().getResources().getDisplayMetrics().density) * n;
    }
    private boolean notEmpty(String s) { return s != null && !s.isEmpty(); }

    private TextView tv(View ctx, String text, int color, int sp, boolean bold) {
        TextView tv = new TextView(ctx.getContext());
        tv.setText(text); tv.setTextColor(color); tv.setTextSize(sp);
        if (bold) tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }
    private TextView meta(View ctx, String text, int dp) {
        TextView tv = tv(ctx, text, 0xFF999999, 10, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginStart(8*dp); tv.setLayoutParams(lp); return tv;
    }
    private LinearLayout hll(View ctx) {
        LinearLayout ll = new LinearLayout(ctx.getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL); return ll;
    }
    private int priColor(String p) { return "High".equals(p)?0xFFC0392B:"Medium".equals(p)?0xFFE65100:0xFF2E7D32; }
    private int priBg(String p)    { return "High".equals(p)?0xFFFFEBEE:"Medium".equals(p)?0xFFFFF3E0:0xFFE8F5E9; }
}
