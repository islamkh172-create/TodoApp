package com.example.todoapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;

public class StatsFragment extends BaseFragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View root = inf.inflate(R.layout.stats, c, false);
        update(root); return root;
    }

    @Override public void onResume() { super.onResume(); if (getView()!=null) update(getView()); }

    private void update(View root) {
        setCount(root, R.id.card_stat_completed,  dao().countStatus(uid(),"Done"));
        setCount(root, R.id.card_stat_in_progress,dao().countStatus(uid(),"In Progress"));
        setCount(root, R.id.card_stat_total,      dao().getAll(uid()).size());

        for (int id : new int[]{R.id.card_history_groceries,R.id.card_history_mockup,R.id.card_history_report}) {
            View v = root.findViewById(id); if (v!=null) v.setVisibility(View.GONE);
        }

        LinearLayout container = root.findViewById(R.id.stats_history_container);
        if (container == null) return;
        container.removeAllViews();
        int dp = Math.round(requireContext().getResources().getDisplayMetrics().density), n = 0;
        for (Task t : dao().getAll(uid())) {
            if (n++ >= 5) break;
            container.addView(historyRow(t, dp));
        }
    }

    private void setCount(View root, int cardId, int val) {
        LinearLayout card = root.findViewById(cardId);
        if (card == null) return;
        try { ((TextView) card.getChildAt(0)).setText(String.valueOf(val)); } catch (Exception ignored){}
    }

    private LinearLayout historyRow(Task t, int dp) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(0xFFFFFFFF);
        row.setPadding(14*dp,14*dp,14*dp,14*dp);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10*dp,0,10*dp,8*dp); row.setLayoutParams(lp);

        LinearLayout left = new LinearLayout(requireContext());
        left.setOrientation(LinearLayout.VERTICAL);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvT = new TextView(requireContext());
        tvT.setText(t.title); tvT.setTextColor(0xFF1A1A2E); tvT.setTextSize(13);
        tvT.setTypeface(null, android.graphics.Typeface.BOLD); left.addView(tvT);

        TextView tvC = new TextView(requireContext());
        tvC.setText(t.category); tvC.setTextColor(0xFF888888); tvC.setTextSize(11); left.addView(tvC);
        row.addView(left);

        TextView tvS = new TextView(requireContext());
        tvS.setText(t.status); tvS.setTextSize(10); tvS.setPadding(8*dp,3*dp,8*dp,3*dp);
        tvS.setBackgroundColor(t.isDone()?0xFFE8F5E9:0xFFE8EAF6);
        tvS.setTextColor(t.isDone()?0xFF2E7D32:0xFF1A237E);
        row.addView(tvS);
        return row;
    }
}