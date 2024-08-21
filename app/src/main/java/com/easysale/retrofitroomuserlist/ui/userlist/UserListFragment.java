package com.easysale.retrofitroomuserlist.ui.userlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easysale.retrofitroomuserlist.databinding.FragmentUserListBinding;
import com.easysale.retrofitroomuserlist.ui.userlist.adapter.UserAdapter;

import java.util.ArrayList;

public class UserListFragment extends Fragment {
    private FragmentUserListBinding binding;
    private UserListViewModel userListViewModel;
    private UserAdapter userAdapter;

    public UserListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userListViewModel = new ViewModelProvider(this).get(UserListViewModel.class);

        setupRecyclerView();
        observeData();

        // Load the first batch of users
        userListViewModel.fetchUsers();

        // Setup scroll listener to load more users when reaching the end of the list
        binding.recyclerViewUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == userAdapter.getItemCount() - 1) {
                    // Reached the end of the list, load more users
                    userListViewModel.fetchUsers();
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(new ArrayList<>());
        binding.recyclerViewUsers.setAdapter(userAdapter);
    }

    private void observeData() {
        userListViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), users -> {
            userAdapter.setUserList(users);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Prevent memory leaks
    }
}

