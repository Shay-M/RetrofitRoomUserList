package com.easysale.retrofitroomuserlist.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Entity(tableName = "users")
@Builder
@AllArgsConstructor
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String email;
    private String avatar;
    private boolean isDeleted = false;
}
