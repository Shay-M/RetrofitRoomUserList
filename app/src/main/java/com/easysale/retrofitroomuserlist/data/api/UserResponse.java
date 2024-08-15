package com.easysale.retrofitroomuserlist.data.api;

import com.easysale.retrofitroomuserlist.data.model.User;

import java.util.List;

import lombok.Data;

@Data
public class UserResponse {
    private List<User> data;
}