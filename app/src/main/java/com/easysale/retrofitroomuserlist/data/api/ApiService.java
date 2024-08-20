package com.easysale.retrofitroomuserlist.data.api;


import com.easysale.retrofitroomuserlist.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
//    @GET("users")
//    Call<UserResponse> getUsers(@Query("page") int page);

    @GET("users")
    Call<UserResponse> getUsers(@Query("page") int page);

}
