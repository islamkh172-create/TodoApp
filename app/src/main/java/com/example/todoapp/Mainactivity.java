package com.example.todoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.todoapp.db.SessionManager;

public class Mainactivity extends AppCompatActivity {

    private static final int REQ_NOTIF = 1001;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        // Init TaskStore with current user
        TaskStore.getInstance().init(this, session.getUserId());

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
            }
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null) loadFragment(new TasksFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if      (id == R.id.nav_tasks)      fragment = new TasksFragment();
            else if (id == R.id.nav_categories) fragment = new CategoriesFragment();
            else if (id == R.id.nav_stats)      fragment = new StatsFragment();
            else if (id == R.id.nav_profile)    fragment = new ProfileFragment();
            else return false;
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

    public void navigateToTasks() {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_tasks);
    }

    public int getCurrentUserId()     { return session.getUserId(); }
    public String getCurrentUsername(){ return session.getUsername(); }
}
