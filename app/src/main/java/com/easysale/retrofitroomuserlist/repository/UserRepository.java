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
        loadUsersFromRoom();
    }

    // Load users from Room and then API if needed
    private void loadUsersFromRoom() {
        executor.execute(() -> {
            List<User> usersFromRoom = userDao.getAllUsersSync();
            if (usersFromRoom != null && !usersFromRoom.isEmpty()) {
                Log.d(TAG, "Loaded users from Room: " + usersFromRoom.size());
                // סינון משתמשים שנמחקו מתוך הרשימה המקומית
                allUsers.clear();
                for (User user : usersFromRoom) {
                    if (!user.isDeleted()) {
                        allUsers.add(user);
                    }
                }
                usersLiveData.postValue(new ArrayList<>(allUsers));
            } else {
                Log.d(TAG, "No users found in Room");
            }
            fetchUsers();
        });
    }


    // Fetch users from API
    public void fetchUsers() {
        if (currentPage.get() > totalPages.get()) {
            Log.d(TAG, "fetchUsers: No more pages to load or no network available.\nCurrent page: " + currentPage.get() + ", Total pages: " + totalPages.get());
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
                        if (!containsUser(allUsers, user)) {
                            newUsers.add(user);
                        }
                    }

                    allUsers.addAll(newUsers);

                    executor.execute(() -> userDao.insert(newUsers));

                    // Filter out deleted users before updating LiveData
                    List<User> nonDeletedUsers = new ArrayList<>();
                    for (User user : allUsers) {
                        if (!user.isDeleted()) {
                            nonDeletedUsers.add(user);
                        }
                    }

                    usersLiveData.postValue(new ArrayList<>(nonDeletedUsers));

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

    private boolean containsUser(List<User> users, User userToCheck) {
        for (User user : users) {
            if (user.getId() == userToCheck.getId()) {
                return true;
            }
        }
        return false;
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
    public LiveData<Boolean> updateUser(User user) {
        MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                userDao.update(user);
                int index = allUsers.indexOf(user);
                if (index != -1) {
                    allUsers.set(index, user);
                    usersLiveData.postValue(new ArrayList<>(allUsers));
                }
                updateResult.postValue(true);
            } catch (Exception e) {
                updateResult.postValue(false);
            }
        });
        return updateResult;
    }

    // Delete a user
    public LiveData<Boolean> deleteUser(User user) {
        MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                user.setDeleted(true);
                userDao.update(user);

                // Update the in-memory list and the LiveData
                allUsers.remove(user);

                List<User> nonDeletedUsers = new ArrayList<>();
                for (User u : allUsers) {
                    if (!u.isDeleted()) {
                        nonDeletedUsers.add(u);
                    }
                }

                usersLiveData.postValue(nonDeletedUsers);

                deleteResult.postValue(true);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting user", e);
                deleteResult.postValue(false);
            }
        });
        return deleteResult;
    }
/*    public LiveData<Boolean> deleteUser(User user) {
        MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                userDao.delete(user);
                allUsers.remove(user);
                usersLiveData.postValue(new ArrayList<>(allUsers));
                deleteResult.postValue(true);
            } catch (Exception e) {
                deleteResult.postValue(false);
            }
        });
        return deleteResult;
    }*/

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
