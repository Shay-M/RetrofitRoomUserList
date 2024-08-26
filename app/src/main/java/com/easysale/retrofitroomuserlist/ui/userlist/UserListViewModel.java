package com.easysale.retrofitroomuserlist.ui.userlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;

public class UserListViewModel extends ViewModel {

    private final UserRepository userRepository;

    @Inject
    public UserListViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<List<User>> getUsersLiveData() {
        return userRepository.getUsersLiveData();
    }

    public void fetchUsers() {
        userRepository.fetchUsers();
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public void deleteUser(User user) {
        userRepository.deleteUser(user);
    }

    public void deleteAllUsers() {
        userRepository.deleteAllUsers();
    }
}
