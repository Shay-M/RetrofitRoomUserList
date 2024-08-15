package com.easysale.retrofitroomuserlist.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;

@Data
@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey
    private int id;
    private String firstName;
    private String lastName;
    private String avatar;
}
