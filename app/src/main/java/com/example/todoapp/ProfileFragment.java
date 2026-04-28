package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.todoapp.db.*;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile, container, false);

        // Show real user info
        SessionManager session = new SessionManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        UserEntity user = db.userDao().findById(session.getUserId());

        TextView tvName  = root.findViewById(R.id.tv_profile_name);
        TextView tvEmail = root.findViewById(R.id.tv_profile_email);
        if (user != null) {
            tvName.setText(user.fullName != null && !user.fullName.isEmpty() ? user.fullName : user.username);
            tvEmail.setText(user.email != null && !user.email.isEmpty() ? user.email : user.username + "@todoapp.com");
        } else {
            tvName.setText(session.getUsername());
            tvEmail.setText(session.getUsername() + "@todoapp.com");
        }

        // Task count badge
        TextView tvTaskCount = root.findViewById(R.id.tv_task_count);
        int total = TaskStore.getInstance().getAllTasks().size();
        int done  = TaskStore.getInstance().countByStatus("Done");
        tvTaskCount.setText(done + "/" + total + " tasks done");

        // Language buttons
        TextView btnEng = root.findViewById(R.id.btn_language_english);
        TextView btnFr  = root.findViewById(R.id.btn_language_french);
        TextView btnAr  = root.findViewById(R.id.btn_language_arabic);
        View.OnClickListener langClick = v -> {
            resetLang(btnEng, btnFr, btnAr);
            ((TextView) v).setBackgroundColor(0xFF1A237E);
            ((TextView) v).setTextColor(0xFFFFFFFF);
            Toast.makeText(requireContext(), ((TextView) v).getText() + " selected", Toast.LENGTH_SHORT).show();
        };
        btnEng.setOnClickListener(langClick);
        btnFr.setOnClickListener(langClick);
        btnAr.setOnClickListener(langClick);

        // Share
        root.findViewById(R.id.btn_share_task).setOnClickListener(v -> shareTask());

        // Logout
        root.findViewById(R.id.btn_logout).setOnClickListener(v -> showLogoutDialog());

        return root;
    }

    private void resetLang(TextView... btns) {
        for (TextView b : btns) { b.setBackgroundColor(0xFFE8EAF6); b.setTextColor(0xFF1A237E); }
    }

    private void shareTask() {
        StringBuilder sb = new StringBuilder("My Tasks:\n");
        for (Task t : TaskStore.getInstance().getAllTasks())
            sb.append("• ").append(t.getTitle()).append(" (").append(t.getStatus()).append(")\n");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(i, "Share tasks via..."));
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (d, w) -> {
                    new SessionManager(requireContext()).clearSession();
                    Intent intent = new Intent(requireActivity(), Loginactivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
