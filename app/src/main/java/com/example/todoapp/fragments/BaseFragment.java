package com.example.todoapp.fragments;

import androidx.fragment.app.Fragment;

import com.example.todoapp.db.AppDatabase;
import com.example.todoapp.db.SessionManager;
import com.example.todoapp.db.TaskDao;

public abstract class BaseFragment extends Fragment {
    protected TaskDao dao() { return AppDatabase.getInstance(requireContext()).taskDao(); }
    protected long uid()    { return new SessionManager(requireContext()).getUserId(); }
}
