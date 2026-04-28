package com.example.todoapp;

import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import java.util.*;

public class TasksFragment extends BaseFragment {

    private LinearLayout list;
    private EditText search;
    private TextView btnAll, btnProg, btnDone;
    private String filter = "All";

    private static final int MATCH = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int WRAP  = LinearLayout.LayoutParams.WRAP_CONTENT;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View root = inf.inflate(R.layout.tasks, c, false);
        list    = root.findViewById(R.id.task_list_container);
        search  = root.findViewById(R.id.input_search);
        btnAll  = root.findViewById(R.id.filter_all);
        btnProg = root.findViewById(R.id.filter_in_progress);
        btnDone = root.findViewById(R.id.filter_done);

        btnAll.setOnClickListener(v  -> applyFilter("All"));
        btnProg.setOnClickListener(v -> applyFilter("In Progress"));
        btnDone.setOnClickListener(v -> applyFilter("Done"));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){ render(); }
            public void afterTextChanged(Editable s){}
        });
        root.findViewById(R.id.btn_add_task).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddTaskFragment())
                        .addToBackStack(null).commit());
        render();
        return root;
    }

    @Override public void onResume() { super.onResume(); render(); }

    private void applyFilter(String f) {
        filter = f;
        for (TextView btn : new TextView[]{btnAll, btnProg, btnDone}) {
            String label = (String) btn.getText();
            boolean active = f.equalsIgnoreCase(label) || (f.equals("All") && label.equals("All"));
            // match by position
        }
        // simple direct approach:
        btnAll.setBackgroundColor("All".equals(f)?0xFF1A237E:0xFFE8EAF6);   btnAll.setTextColor("All".equals(f)?0xFFFFFFFF:0xFF1A237E);
        btnProg.setBackgroundColor("In Progress".equals(f)?0xFF1A237E:0xFFE8EAF6); btnProg.setTextColor("In Progress".equals(f)?0xFFFFFFFF:0xFF1A237E);
        btnDone.setBackgroundColor("Done".equals(f)?0xFF1A237E:0xFFE8EAF6); btnDone.setTextColor("Done".equals(f)?0xFFFFFFFF:0xFF1A237E);
        render();
    }

    private void render() {
        if (list == null) return;
        list.removeAllViews();
        String q = search.getText().toString().trim().toLowerCase();
        List<Task> result = new ArrayList<>();
        for (Task t : dao().getAll(uid()))
            if (("All".equals(filter) || t.status.equalsIgnoreCase(filter)) &&
                    (q.isEmpty() || t.title.toLowerCase().contains(q) || t.category.toLowerCase().contains(q)))
                result.add(t);

        if (result.isEmpty()) {
            TextView e = mkTv("No tasks yet. Tap '+ Add New Task' to get started!", 0xFF888888, 13, false);
            e.setPadding(40,48,40,0); e.setGravity(android.view.Gravity.CENTER);
            list.addView(e); return;
        }
        int dp = dp();
        for (Task t : result) list.addView(card(t, dp));
    }

    private LinearLayout card(Task task, int dp) {
        LinearLayout card = vll(0xFFFFFFFF, 14*dp, 14*dp, 14*dp, 14*dp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.setMargins(10*dp, 0, 10*dp, 8*dp); card.setLayoutParams(lp);

        // Row 1: title + priority badge
        LinearLayout row1 = hll(android.view.Gravity.CENTER_VERTICAL);
        TextView tvTitle = mkTv(task.title, 0xFF1A1A2E, 14, true);
        if (task.isDone()) tvTitle.setPaintFlags(tvTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP, 1f));
        TextView tvPri = mkTv(task.priority, priColor(task.priority), 10, false);
        tvPri.setPadding(8*dp, 3*dp, 8*dp, 3*dp); tvPri.setBackgroundColor(priBg(task.priority));
        row1.addView(tvTitle); row1.addView(tvPri); card.addView(row1);

        // Description
        if (task.description != null && !task.description.isEmpty()) {
            TextView d = mkTv(task.description, 0xFF888888, 12, false);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(WRAP, WRAP);
            dlp.topMargin = 4*dp; d.setLayoutParams(dlp); card.addView(d);
        }

        // Row 3: category / date / reminder / toggle
        LinearLayout row3 = hll(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams r3p = new LinearLayout.LayoutParams(MATCH, WRAP);
        r3p.topMargin = 8*dp; row3.setLayoutParams(r3p);

        TextView tvCat = mkTv(task.category, 0xFF1A237E, 10, false);
        tvCat.setPadding(8*dp,2*dp,8*dp,2*dp); tvCat.setBackgroundColor(0xFFE8EAF6);
        row3.addView(tvCat);

        if (notEmpty(task.dueDate))      row3.addView(meta("📅 "+task.dueDate, dp));
        if (notEmpty(task.reminderTime)) row3.addView(meta("⏰ "+task.reminderTime, dp));

        View sp = new View(requireContext());
        sp.setLayoutParams(new LinearLayout.LayoutParams(0,1,1f)); row3.addView(sp);

        TextView tvStatus = mkTv(task.isDone()?"✓ Done":"Mark Done",
                task.isDone()?0xFF2E7D32:0xFF1A237E, 10, false);
        tvStatus.setPadding(8*dp,3*dp,8*dp,3*dp);
        tvStatus.setBackgroundColor(task.isDone()?0xFFE8F5E9:0xFFE8EAF6);
        tvStatus.setOnClickListener(v -> {
            task.status = task.isDone() ? "In Progress" : "Done";
            dao().update(task); render();
        });
        row3.addView(tvStatus); card.addView(row3);

        card.setOnLongClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task").setMessage("Delete \""+task.title+"\"?")
                    .setPositiveButton("Delete",(d,w)->{ dao().delete(task); render(); })
                    .setNegativeButton("Cancel",null).show();
            return true;
        });
        return card;
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private int dp() { return Math.round(requireContext().getResources().getDisplayMetrics().density); }
    private boolean notEmpty(String s) { return s != null && !s.isEmpty(); }

    private TextView meta(String text, int dp) {
        TextView tv = mkTv(text, 0xFF999999, 10, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP, WRAP);
        lp.setMarginStart(8*dp); tv.setLayoutParams(lp); return tv;
    }
    private TextView mkTv(String text, int color, int sp, boolean bold) {
        TextView tv = new TextView(requireContext());
        tv.setText(text); tv.setTextColor(color); tv.setTextSize(sp);
        if (bold) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }
    private LinearLayout hll(int gravity) {
        LinearLayout ll = new LinearLayout(requireContext());
        ll.setOrientation(LinearLayout.HORIZONTAL); ll.setGravity(gravity); return ll;
    }
    private LinearLayout vll(int bg, int l, int t, int r, int b) {
        LinearLayout ll = new LinearLayout(requireContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(bg); ll.setPadding(l,t,r,b); return ll;
    }
    private int priColor(String p) { return "High".equals(p)?0xFFC0392B:"Medium".equals(p)?0xFFE65100:0xFF2E7D32; }
    private int priBg(String p)    { return "High".equals(p)?0xFFFFEBEE:"Medium".equals(p)?0xFFFFF3E0:0xFFE8F5E9; }
}