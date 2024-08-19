package com.easysale.retrofitroomuserlist.ui;


import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.databinding.UserItemBinding;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final UserItemBinding binding;

    public UserViewHolder(UserItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(User user) {
        binding.setUser(user);
        Log.d("TAG", "user: " + user);
        binding.executePendingBindings();
    }
}
