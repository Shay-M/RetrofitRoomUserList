package com.easysale.retrofitroomuserlist.ui.userdetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.repository.UserRepository;

public class UserDetailViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public UserDetailViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void setUser(User user) {
        userLiveData.setValue(user);
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public LiveData<Boolean> updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public LiveData<Boolean> deleteUser(User user) {
        return userRepository.deleteUser(user);
    }
}

