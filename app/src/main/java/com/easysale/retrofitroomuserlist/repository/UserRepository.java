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

    // Data sources for Room and API
    private final RoomUserDataSource roomUserDataSource;
    private final ApiUserDataSource apiUserDataSource;

    // Page management
    private final AtomicInteger currentPage = new AtomicInteger(1);
    private final AtomicInteger totalPages = new AtomicInteger(1);

    // LiveData for observing user data changes
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();

    // List to store all users
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

    /**
     * Load users from Room and then fetch from API if needed.
     * Updates the LiveData with the loaded users.
     */
    public void loadUsers() {
        roomUserDataSource.loadUsersFromRoom(usersFromRoom -> {
            if (usersFromRoom != null && !usersFromRoom.isEmpty()) {
                Log.d(TAG, "Loaded users from Room: " + usersFromRoom.size());

                // Filter out deleted users and update LiveData
                List<User> nonDeletedUsers = filterDeletedUsers(usersFromRoom);
                usersLiveData.postValue(new ArrayList<>(nonDeletedUsers));

                // Update allUsers with the latest data from Room
                allUsers.clear();
                allUsers.addAll(usersFromRoom);

            } else {
                Log.d(TAG, "No users found in Room");
            }
            fetchUsers();
        });
    }

    /**
     * Fetch users from API.
     * Stores the fetched users in Room and updates LiveData.
     */
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

                // Filter new users that are not in allUsers and add them to allUsers
                List<User> newUsers = new ArrayList<>();
                for (User user : users) {
                    if (!allUsers.contains(user)) {
                        newUsers.add(user);
                    }
                }

                allUsers.addAll(newUsers);
                roomUserDataSource.insertUsers(newUsers);

                // Filter out deleted users and update LiveData
                List<User> nonDeletedUsers = filterDeletedUsers(allUsers);
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

    /**
     * Filters out deleted users from the provided list.
     *
     * @param users The list of users to filter.
     * @return A list of non-deleted users.
     */
    private List<User> filterDeletedUsers(List<User> users) {
        List<User> nonDeletedUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.isDeleted()) {
                nonDeletedUsers.add(user);
            }
        }
        return nonDeletedUsers;
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

        // Filter out deleted users and update LiveData
        List<User> nonDeletedUsers = filterDeletedUsers(allUsers);
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
