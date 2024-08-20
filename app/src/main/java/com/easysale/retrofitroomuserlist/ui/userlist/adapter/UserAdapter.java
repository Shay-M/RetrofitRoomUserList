package com.easysale.retrofitroomuserlist.ui.userlist.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.databinding.UserItemBinding;
import com.easysale.retrofitroomuserlist.ui.UserViewHolder;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    public static final String TAG = "UserAdapter";
    private List<User> mUserList;

    public UserAdapter(List<User> users) {
        mUserList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserItemBinding itemBinding = UserItemBinding.inflate(layoutInflater, parent, false);
        return new UserViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUserList != null ? mUserList.size() : 0;
    }

    public void setUserList(List<User> userList) {
        mUserList = userList;
        notifyDataSetChanged();
    }
}
