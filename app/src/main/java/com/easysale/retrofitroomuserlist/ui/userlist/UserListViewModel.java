package com.easysale.retrofitroomuserlist.ui.userlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.easysale.retrofitroomuserlist.data.api.ApiService;
import com.easysale.retrofitroomuserlist.data.api.RetrofitClient;
import com.easysale.retrofitroomuserlist.data.model.User;
import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class UserListViewModel extends ViewModel {
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private ApiService apiService;

    public UserListViewModel() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    public void fetchUsers(int page) {
        apiService.getUsers(page).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usersLiveData.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Handle error
            }
        });
    }
}
