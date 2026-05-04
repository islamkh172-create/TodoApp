package com.example.todoapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.todoapp.R;
import com.example.todoapp.db.Task;

public class CategoriesFragment extends BaseFragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View root = inf.inflate(R.layout.categories, c, false);
        updateCounts(root);

        int[]    ids   = {R.id.card_category_work, R.id.card_category_personal, R.id.card_category_school, R.id.card_category_health};
        String[] names = {"Work", "Personal", "School", "Health"};
        for (int i = 0; i < ids.length; i++) {
            String name = names[i];
            View card = root.findViewById(ids[i]);
            if (card != null) card.setOnClickListener(v -> showTasks(name));
        }

        TextView btnNew = root.findViewById(R.id.btn_new_category);
        if (btnNew != null) btnNew.setOnClickListener(v -> newCategoryDialog());
        return root;
    }

    private void updateCounts(View root) {
        int[][]  pairs = {{R.id.card_category_work,0},{R.id.card_category_personal,1},{R.id.card_category_school,2},{R.id.card_category_health,3}};
        String[] names = {"Work","Personal","School","Health"};
        for (int[] p : pairs) {
            LinearLayout card = root.findViewById(p[0]);
            if (card == null) continue;
            try {
                LinearLayout inner = (LinearLayout) card.getChildAt(1);
                ((TextView) inner.getChildAt(1)).setText(dao().countCategory(uid(), names[p[1]]) + " tasks");
            } catch (Exception ignored) {}
        }
    }

    private void showTasks(String cat) {
        StringBuilder sb = new StringBuilder("Tasks in " + cat + ":\n\n");
        for (Task t : dao().getAll(uid()))
            if (t.category.equalsIgnoreCase(cat))
                sb.append("• ").append(t.title).append(" [").append(t.status).append("]\n");
        if (sb.toString().endsWith(":\n\n")) sb.append("No tasks yet.");
        new AlertDialog.Builder(requireContext())
                .setTitle(cat).setMessage(sb).setPositiveButton("OK", null).show();
    }

    private void newCategoryDialog() {
        EditText et = new EditText(requireContext()); et.setHint("Category name");
        new AlertDialog.Builder(requireContext()).setTitle("New Category").setView(et)
                .setPositiveButton("Create", (d, w) -> {
                    String n = et.getText().toString().trim();
                    if (!n.isEmpty()) Toast.makeText(requireContext(), "\""+n+"\" created", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null).show();
    }
}
