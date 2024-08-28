package com.easysale.retrofitroomuserlist.repository;

import androidx.annotation.NonNull;

import com.easysale.retrofitroomuserlist.data.api.ApiService;
import com.easysale.retrofitroomuserlist.data.api.RetrofitClient;
import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiUserDataSource {
    private final ApiService apiService;

    public ApiUserDataSource() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void fetchUsers(int page, ApiUsersCallback callback) {
        apiService.getUsers(page).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onUsersFetched(response.body());
                } else {
                    callback.onError(new Exception("Error fetching users, response not successful."));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface ApiUsersCallback {
        void onUsersFetched(UserResponse userResponse);

        void onError(Throwable throwable);
    }
}
