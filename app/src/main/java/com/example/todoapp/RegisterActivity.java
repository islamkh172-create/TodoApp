package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.db.*;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputFullName, inputUsername, inputEmail, inputPassword, inputConfirm;
    private AppDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        db      = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        inputFullName = findViewById(R.id.input_full_name);
        inputUsername = findViewById(R.id.input_reg_username);
        inputEmail    = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_reg_password);
        inputConfirm  = findViewById(R.id.input_confirm_password);

        findViewById(R.id.btn_register).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tv_go_login).setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String fullName = inputFullName.getText().toString().trim();
        String username = inputUsername.getText().toString().trim();
        String email    = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirm  = inputConfirm.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Full name, username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            inputConfirm.setError("Passwords do not match");
            return;
        }
        if (db.userDao().findByUsername(username) != null) {
            inputUsername.setError("Username already taken");
            return;
        }

        UserEntity user = new UserEntity();
        user.fullName     = fullName;
        user.username     = username;
        user.email        = email;
        user.passwordHash = AuthHelper.hashPassword(password);

        long newId = db.userDao().insert(user);
        session.saveSession((int) newId, username);

        Toast.makeText(this, "Account created! Welcome, " + fullName, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, Mainactivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
