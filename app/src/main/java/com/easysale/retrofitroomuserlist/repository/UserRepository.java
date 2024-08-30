package com.easysale.retrofitroomuserlist.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final RoomUserDataSource roomUserDataSource;
    private final ApiUserDataSource apiUserDataSource;
    private final AtomicInteger currentPage = new AtomicInteger(1);
    private final AtomicInteger totalPages = new AtomicInteger(1);
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final List<User> allUsers = new ArrayList<>();

    public UserRepository(Context context) {
        Executor executor = Executors.newFixedThreadPool(4);
        roomUserDataSource = new RoomUserDataSource(context, executor);
        apiUserDataSource = new ApiUserDataSource();
    }

    // Constructor for testing with mocks
    public UserRepository(RoomUserDataSource roomUserDataSource, ApiUserDataSource apiUserDataSource) {
        this.roomUserDataSource = roomUserDataSource;
        this.apiUserDataSource = apiUserDataSource;
    }

    // Load users from Room and then fetch from API if needed
    public void loadUsers() {
        roomUserDataSource.loadUsersFromRoom(usersFromRoom -> {
            if (usersFromRoom != null && !usersFromRoom.isEmpty()) {
                Log.d(TAG, "Loaded users from Room: " + usersFromRoom.size());
                allUsers.clear();
                for (User user : usersFromRoom) {
                    if (!user.isDeleted()) {
                        allUsers.add(user);
                    }
                }
                usersLiveData.postValue(new ArrayList<>(allUsers));
                allUsers.clear();
                allUsers.addAll(usersFromRoom);
            } else {
                Log.d(TAG, "No users found in Room");
            }
            fetchUsers();
        });
    }

    // Fetch users from API
    public void fetchUsers() {
        if (currentPage.get() > totalPages.get()) {
            Log.d(TAG, "fetchUsers: No more pages to load.\nCurrent page: " + currentPage.get() + ", Total pages: " + totalPages.get());
            return;
        }
        Log.d(TAG, "Fetching users for page: " + currentPage.get());

        apiUserDataSource.fetchUsers(currentPage.get(), new ApiUserDataSource.ApiUsersCallback() {
            @Override
            public void onUsersFetched(UserResponse userResponse) {
                totalPages.set(userResponse.getTotalPages());
                List<User> users = userResponse.getData();

                List<User> newUsers = new ArrayList<>();
                for (User user : users) {
                    if (!isUserInListAndDeleted(allUsers, user)) {
                        newUsers.add(user);
                    }
                }

                List<User> nonDeletedUsers = new ArrayList<>();
                for (User user : newUsers) {
                    if (!user.isDeleted()) {
                        nonDeletedUsers.add(user);
                    }
                }

//                allUsers.addAll(nonDeletedUsers);
                roomUserDataSource.insertUsers(nonDeletedUsers);


                usersLiveData.postValue(new ArrayList<>(nonDeletedUsers));
                currentPage.incrementAndGet();

                Log.d(TAG, "Users fetched: " + users.size());
                Log.d(TAG, "All users size after adding: " + allUsers.size());
                Log.d(TAG, "Total new users added: " + newUsers.size());
                Log.d(TAG, "Moving to next page: " + currentPage.get());


                fetchUsers();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Error fetching users", throwable);
            }
        });
    }

    private boolean isUserInListAndDeleted(List<User> users, User userToCheck) {
        for (User user : users) {
            if (user.getId() == userToCheck.getId()) return true;

        }
        return false;
    }

    // Add a user
    public LiveData<Boolean> addUser(User user) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        roomUserDataSource.insertUser(user);
        allUsers.add(user);
        usersLiveData.postValue(new ArrayList<>(allUsers));
        result.postValue(true);
        return result;
    }

    // Update a user
    public LiveData<Boolean> updateUser(User user) {
        MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
        roomUserDataSource.updateUser(user);
        int index = allUsers.indexOf(user);
        if (index != -1) {
            allUsers.set(index, user);
            usersLiveData.postValue(new ArrayList<>(allUsers));
        }
        updateResult.postValue(true);
        return updateResult;
    }

    // Delete a user
    public LiveData<Boolean> deleteUser(User user) {
        MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
        user.setDeleted(true);
        roomUserDataSource.updateUser(user);

        List<User> nonDeletedUsers = new ArrayList<>();
        for (User u : allUsers) {
            if (!u.isDeleted()) {
                nonDeletedUsers.add(u);
            }
        }
        usersLiveData.postValue(nonDeletedUsers);
        deleteResult.postValue(true);
        return deleteResult;
    }

    // Delete all users
    public void deleteAllUsers() {
        roomUserDataSource.deleteAllUsers();
        allUsers.clear();
        usersLiveData.postValue(new ArrayList<>(allUsers));
    }

    // Get live data of users for UI updates
    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }
}
