package com.easysale.retrofitroomuserlist.ui.userlist;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easysale.retrofitroomuserlist.data.api.ApiService;
import com.easysale.retrofitroomuserlist.data.api.RetrofitClient;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserListViewModel extends ViewModel {
    private static final String TAG = "UserListViewModel";
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final ApiService apiService;

    public UserListViewModel() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    // Method to fetch all users from all pages
    public void fetchAllUsers() {
        Log.d(TAG, "fetchAllUsers: Fetching all users...");
        List<User> allUsers = new ArrayList<>();
        AtomicInteger currentPage = new AtomicInteger(1);
        AtomicInteger totalPages = new AtomicInteger(1);

        Callback<UserResponse> responseCallback = new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    Log.d(TAG, "onResponse: Fetched page " + currentPage.get() + " of " + userResponse.getTotalPages());
                    totalPages.set(userResponse.getTotalPages());
                    allUsers.addAll(userResponse.getData());

                    if (currentPage.incrementAndGet() <= totalPages.get()) {
                        // Fetch the next page
                        Log.d(TAG, "onResponse: Fetching next page " + currentPage.get());
                        apiService.getUsers(currentPage.get()).enqueue(this);
                    } else {
                        // All pages are loaded, post the result
                        Log.d(TAG, "onResponse: All pages loaded, total users fetched: " + allUsers.size());
                        usersLiveData.postValue(allUsers);
                    }
                } else {
                    Log.e(TAG, "onResponse: Error fetching users, response not successful. Code: " + response.code());
                    try {
                        Log.e(TAG, "onResponse: Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to fetch users", t);
            }
        };

        // Start loading the first page
        Log.d(TAG, "fetchAllUsers: Fetching first page...");
        apiService.getUsers(currentPage.get()).enqueue(responseCallback);
    }
}