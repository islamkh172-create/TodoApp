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

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null) load(new TasksFragment());

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if      (id == R.id.nav_tasks)      load(new TasksFragment());
            else if (id == R.id.nav_categories) load(new CategoriesFragment());
            else if (id == R.id.nav_stats)      load(new StatsFragment());
            else if (id == R.id.nav_profile)    load(new ProfileFragment());
            else return false;
            return true;
        });
    }

    private void load(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f).commit();
    }

    public void navigateToTasks() { findViewById(R.id.bottom_nav);
        ((BottomNavigationView) findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_tasks); }

    public String getCurrentUsername() { return session.getUsername(); }
}