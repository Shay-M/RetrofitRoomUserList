package com.easysale.retrofitroomuserlist.ui.userlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.repository.UserRepository;
import com.easysale.retrofitroomuserlist.utils.messages.MessageDisplayer;
import com.easysale.retrofitroomuserlist.utils.messages.SnackBarMessage;

import java.util.List;

import lombok.Getter;


public class UserListViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    @Getter
    private final LiveData<List<User>> usersLiveData;

    public UserListViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        usersLiveData = userRepository.getUsersLiveData();
    }

    public void fetchUsers() {
        userRepository.loadUsers();
    }

    public LiveData<Boolean> addUser(User user) {
        return userRepository.addUser(user);
    }


}

