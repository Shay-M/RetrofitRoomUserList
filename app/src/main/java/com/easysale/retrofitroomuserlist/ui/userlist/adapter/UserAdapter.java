package com.easysale.retrofitroomuserlist.ui.userlist.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.databinding.UserItemBinding;
import com.easysale.retrofitroomuserlist.ui.UserViewHolder;

import java.util.List;

import lombok.Setter;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    public static final String TAG = "UserAdapter";
    private List<User> mUserList;

    // Listener for user clicks
    @Setter
    private OnUserClickListener onUserClickListener;

    // Constructor to initialize the adapter with a list of users
    public UserAdapter(List<User> users) {
        mUserList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the list
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserItemBinding itemBinding = UserItemBinding.inflate(layoutInflater, parent, false);
        return new UserViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Get the user at the current position
        User user = mUserList.get(position);

        // Bind user data to the ViewHolder
        holder.bind(user);

        // Set up click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return mUserList != null ? mUserList.size() : 0;
    }

    // Update the list of users and notify the adapter of the changes
    public void setUserList(List<User> userList) {
        mUserList = userList;
        notifyDataSetChanged();
    }

    // Interface for handling user click events
    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}
