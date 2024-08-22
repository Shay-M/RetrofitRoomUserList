package com.easysale.retrofitroomuserlist.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easysale.retrofitroomuserlist.data.api.ApiService;
import com.easysale.retrofitroomuserlist.data.api.RetrofitClient;
import com.easysale.retrofitroomuserlist.data.db.AppDatabase;
import com.easysale.retrofitroomuserlist.data.db.UserDao;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final List<User> allUsers = new ArrayList<>();
    private final UserDao userDao;
    private final ApiService apiService;
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final AtomicInteger currentPage = new AtomicInteger(1);
    private final AtomicInteger totalPages = new AtomicInteger(1);
    private final Executor executor = Executors.newSingleThreadExecutor();

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void fetchUsers() {
        if (currentPage.get() > totalPages.get()) {
            return; // No more pages to load
        }

        apiService.getUsers(currentPage.get()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    totalPages.set(userResponse.getTotalPages());
                    List<User> users = userResponse.getData();
                    allUsers.addAll(users);

                    // Insert data into Room using Executor
                    executor.execute(() -> userDao.insert(users));

                    usersLiveData.postValue(new ArrayList<>(allUsers));

                    currentPage.incrementAndGet();
                } else {
                    Log.e(TAG, "onResponse: Error fetching users, response not successful.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Failed to fetch users", t);
            }
        });
    }

    public void addUser(User user) {
        executor.execute(() -> {
            userDao.insert(user);
            allUsers.add(user);
            usersLiveData.postValue(new ArrayList<>(allUsers));
        });
    }

    public void updateUser(User user) {
        executor.execute(() -> {
            userDao.update(user);
            int index = allUsers.indexOf(user);
            if (index != -1) {
                allUsers.set(index, user);
                usersLiveData.postValue(new ArrayList<>(allUsers));
            }
        });
    }

    public void deleteUser(User user) {
        executor.execute(() -> {
            userDao.delete(user);
            allUsers.remove(user);
            usersLiveData.postValue(new ArrayList<>(allUsers));
        });
    }

    public void deleteAllUsers() {
        executor.execute(() -> {
            userDao.deleteAllUsers();
            allUsers.clear();
            usersLiveData.postValue(new ArrayList<>(allUsers));
        });
    }

    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }
}
