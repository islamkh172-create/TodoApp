package com.example.todoapp.db;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {
    private static final String PREF_NAME = "todo_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final int NO_USER = -1;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(int userId, String username) {
        prefs.edit().putInt(KEY_USER_ID, userId).putString(KEY_USERNAME, username).apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getInt(KEY_USER_ID, NO_USER) != NO_USER;
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, NO_USER);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
}
