package com.example.todoapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class CategoriesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.categories, container, false);

        // Update task counts dynamically from TaskStore
        updateCategoryCounts(root);

        // Category card clicks → filter TasksFragment by category
        int[] cardIds = {
                R.id.card_category_work,
                R.id.card_category_personal,
                R.id.card_category_school,
                R.id.card_category_health
        };
        String[] names = {"Work", "Personal", "School", "Health"};

        for (int i = 0; i < cardIds.length; i++) {
            String name = names[i];
            View card = root.findViewById(cardIds[i]);
            if (card != null) {
                card.setOnClickListener(v -> showCategoryTasks(name));
            }
        }

        // New category button
        TextView btnNew = root.findViewById(R.id.btn_new_category);
        if (btnNew != null) {
            btnNew.setOnClickListener(v -> showNewCategoryDialog());
        }

        return root;
    }

    private void updateCategoryCounts(View root) {
        TaskStore store = TaskStore.getInstance();
        String[] names = {"Work", "Personal", "School", "Health"};
        int[] countViewIds = new int[4]; // We'll find TextViews by traversal

        int[][] pairs = {
                {R.id.card_category_work,     0},
                {R.id.card_category_personal, 1},
                {R.id.card_category_school,   2},
                {R.id.card_category_health,   3}
        };

        for (int[] pair : pairs) {
            LinearLayout card = root.findViewById(pair[0]);
            if (card == null) continue;
            String catName = names[pair[1]];
            int count = store.countByCategory(catName);
            // The second child is the inner LinearLayout; its second child is the count TextView
            try {
                LinearLayout inner = (LinearLayout) card.getChildAt(1);
                TextView countTv  = (TextView) inner.getChildAt(1);
                countTv.setText(count + " tasks");
            } catch (Exception ignored) {}
        }
    }

    private void showCategoryTasks(String category) {
        StringBuilder sb = new StringBuilder("Tasks in " + category + ":\n\n");
        for (Task t : TaskStore.getInstance().getAllTasks()) {
            if (t.getCategory().equalsIgnoreCase(category)) {
                sb.append("• ").append(t.getTitle())
                        .append(" [").append(t.getStatus()).append("]\n");
            }
        }
        if (sb.toString().equals("Tasks in " + category + ":\n\n")) {
            sb.append("No tasks yet.");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(category)
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showNewCategoryDialog() {
        EditText et = new EditText(requireContext());
        et.setHint("Category name");
        new AlertDialog.Builder(requireContext())
                .setTitle("New Category")
                .setView(et)
                .setPositiveButton("Create", (d, w) -> {
                    String name = et.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Category \"" + name + "\" created", Toast.LENGTH_SHORT).show();
                        // In a real app: persist the new category
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}