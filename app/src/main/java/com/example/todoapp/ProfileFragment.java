package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import com.example.todoapp.db.*;

public class ProfileFragment extends BaseFragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View root = inf.inflate(R.layout.profile, c, false);

        SessionManager session = new SessionManager(requireContext());
        UserEntity user = AppDatabase.getInstance(requireContext()).userDao().findById((int) uid());

        TextView tvName  = root.findViewById(R.id.tv_profile_name);
        TextView tvEmail = root.findViewById(R.id.tv_profile_email);
        if (user != null) {
            tvName.setText(user.fullName != null && !user.fullName.isEmpty() ? user.fullName : user.username);
            tvEmail.setText(user.email   != null && !user.email.isEmpty()   ? user.email   : user.username + "@todoapp.com");
        } else {
            tvName.setText(session.getUsername());
            tvEmail.setText(session.getUsername() + "@todoapp.com");
        }

        int total = dao().getAll(uid()).size();
        int done  = dao().countStatus(uid(), "Done");
        ((TextView) root.findViewById(R.id.tv_task_count)).setText(done + "/" + total + " tasks done");

        // Language buttons
        TextView btnEng = root.findViewById(R.id.btn_language_english);
        TextView btnFr  = root.findViewById(R.id.btn_language_french);
        TextView btnAr  = root.findViewById(R.id.btn_language_arabic);
        View.OnClickListener lang = v -> {
            for (TextView b : new TextView[]{btnEng,btnFr,btnAr}) { b.setBackgroundColor(0xFFE8EAF6); b.setTextColor(0xFF1A237E); }
            ((TextView)v).setBackgroundColor(0xFF1A237E); ((TextView)v).setTextColor(0xFFFFFFFF);
            Toast.makeText(requireContext(), ((TextView)v).getText()+" selected", Toast.LENGTH_SHORT).show();
        };
        btnEng.setOnClickListener(lang); btnFr.setOnClickListener(lang); btnAr.setOnClickListener(lang);

        root.findViewById(R.id.btn_share_task).setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder("My Tasks:\n");
            for (Task t : dao().getAll(uid())) sb.append("• ").append(t.title).append(" (").append(t.status).append(")\n");
            Intent i = new Intent(Intent.ACTION_SEND); i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(i,"Share tasks via..."));
        });

        root.findViewById(R.id.btn_logout).setOnClickListener(v ->
                new AlertDialog.Builder(requireContext()).setTitle("Log Out").setMessage("Are you sure?")
                        .setPositiveButton("Log Out",(d,w)->{ session.clearSession();
                            Intent i = new Intent(requireActivity(), Loginactivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i); })
                        .setNegativeButton("Cancel",null).show());

        return root;
    }
}