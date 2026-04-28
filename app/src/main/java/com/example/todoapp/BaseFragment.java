package com.example.todoapp;

import androidx.fragment.app.Fragment;
import com.example.todoapp.db.*;

public abstract class BaseFragment extends Fragment {
    protected TaskDao dao() { return AppDatabase.getInstance(requireContext()).taskDao(); }
    protected long uid()    { return new SessionManager(requireContext()).getUserId(); }
}