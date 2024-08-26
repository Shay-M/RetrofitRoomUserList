package com.easysale.retrofitroomuserlist.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private final UserDao userDao;
    private final ApiService apiService;
    private final AtomicInteger currentPage = new AtomicInteger(1);
    private final AtomicInteger totalPages = new AtomicInteger(1);
    private final Executor executor = Executors.newFixedThreadPool(4);
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final List<User> allUsers = new ArrayList<>();
    private final Context context;

    public UserRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        userDao = db.userDao();
        apiService = RetrofitClient.getApiService();
        loadUsersFromRoom(); // Attempt to load from Room first
    }

    // Load users from Room and then API if needed
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


    // Fetch users from API
    public void fetchUsers() {
        if (currentPage.get() > totalPages.get()) {
            Log.d(TAG, "fetchUsers: No more pages to load or no network available");
            return; // No more pages to load or no network
        }

        apiService.getUsers(currentPage.get()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    totalPages.set(userResponse.getTotalPages());
                    List<User> users = userResponse.getData();

                    // Adding only new data that does not exist in the database
                    List<User> newUsers = new ArrayList<>();
                    for (User user : users) {
                        if (!allUsers.contains(user)) {
                            newUsers.add(user);
                        }
                    }

                    allUsers.addAll(newUsers);

                    executor.execute(() -> userDao.insert(newUsers));

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

    // Add a single user
    public LiveData<Boolean> addUser(User user) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                userDao.insert(user);
                allUsers.add(user);
                usersLiveData.postValue(new ArrayList<>(allUsers));
                result.postValue(true); // Notify success
            } catch (Exception e) {
                Log.e(TAG, "Error adding user", e);
                result.postValue(false); // Notify failure
            }
        });
        return result;
    }

    // Add a list of users
//    public LiveData<Boolean> addUsers(List<User> users) {
//        MutableLiveData<Boolean> result = new MutableLiveData<>();
//        executor.execute(() -> {
//            try {
//                userDao.insert(users);
//                allUsers.addAll(users);
//                usersLiveData.postValue(new ArrayList<>(allUsers));
//                result.postValue(true); // Notify success
//            } catch (Exception e) {
//                Log.e(TAG, "Error adding users", e);
//                result.postValue(false); // Notify failure
//            }
//        });
//        return result;
//    }

    // Update a user
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

    // Delete a user
    public void deleteUser(User user) {
        executor.execute(() -> {
            userDao.delete(user);
            allUsers.remove(user);
            usersLiveData.postValue(new ArrayList<>(allUsers));
        });
    }

    // Delete all users
    public void deleteAllUsers() {
        executor.execute(() -> {
            userDao.deleteAllUsers();
            allUsers.clear();
            usersLiveData.postValue(new ArrayList<>(allUsers));
        });
    }

    // Get live data of users for UI updates
    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }


}
