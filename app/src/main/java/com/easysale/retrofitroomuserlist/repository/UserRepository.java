package com.easysale.retrofitroomuserlist.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easysale.retrofitroomuserlist.data.api.ApiService;
import com.easysale.retrofitroomuserlist.data.db.UserDao;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class UserRepository {
    private static final String TAG = "UserRepository";
    private final UserDao userDao;
    private final ApiService apiService;
    private final AtomicInteger currentPage = new AtomicInteger(1);
    private final AtomicInteger totalPages = new AtomicInteger(1);
    private final Executor executor = Executors.newFixedThreadPool(4);
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final List<User> allUsers = new ArrayList<>();

    @Inject
    public UserRepository(UserDao userDao, ApiService apiService) {
        this.userDao = userDao;
        this.apiService = apiService;
        loadUsersFromRoom();
    }

    private void loadUsersFromRoom() {
        executor.execute(() -> {
            List<User> usersFromRoom = userDao.getAllUsersSync();
            if (usersFromRoom != null && !usersFromRoom.isEmpty()) {
                Log.d(TAG, "Loaded users from Room: " + usersFromRoom.size());
                allUsers.addAll(usersFromRoom);
                usersLiveData.postValue(usersFromRoom);
            } else {
                Log.d(TAG, "No users found in Room");
            }
            fetchUsers();
        });
    }

    public void fetchUsers() {
        if (currentPage.get() > totalPages.get()) {
            Log.d(TAG, "fetchUsers: No more pages to load");
            return;
        }

        apiService.getUsers(currentPage.get()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    totalPages.set(userResponse.getTotalPages());
                    List<User> users = userResponse.getData();

                    List<User> newUsers = new ArrayList<>();
                    for (User user : users) {
                        if (!allUsers.contains(user)) {
                            newUsers.add(user);
                        }
                    }

                    if (!newUsers.isEmpty()) {
                        Log.d(TAG, "Fetched users: " + newUsers.size());
                        insertUsersIntoDatabase(newUsers);
                        currentPage.incrementAndGet();
                    } else {
                        Log.d(TAG, "No new users found");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch users: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Fetch users failed", t);
            }
        });
    }

    private void insertUsersIntoDatabase(List<User> users) {
        executor.execute(() -> {
            userDao.insert(users);
            allUsers.addAll(users);
            usersLiveData.postValue(new ArrayList<>(allUsers));
        });
    }

    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    public void addUser(User user) {
        executor.execute(() -> {
            userDao.insert(user);
            allUsers.add(user);
            usersLiveData.postValue(new ArrayList<>(allUsers));
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
}
