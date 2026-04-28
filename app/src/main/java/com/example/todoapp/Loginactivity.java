package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.db.*;

public class Loginactivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword;
    private SessionManager session;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.login);
        db = AppDatabase.getInstance(this);

        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);

        findViewById(R.id.btn_login).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.tv_go_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String user = inputUsername.getText().toString().trim();
        String pass = inputPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserEntity found = db.userDao().findByUsername(user);
        if (found != null && AuthHelper.verify(pass, found.passwordHash)) {
            session.saveSession(found.id, found.username);
            goToMain();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, Mainactivity.class));
        finish();
    }
}
