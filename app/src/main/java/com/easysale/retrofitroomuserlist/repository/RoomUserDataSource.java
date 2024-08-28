package com.easysale.retrofitroomuserlist.repository;

import android.content.Context;

import com.easysale.retrofitroomuserlist.data.db.AppDatabase;
import com.easysale.retrofitroomuserlist.data.db.UserDao;
import com.easysale.retrofitroomuserlist.data.model.User;

import java.util.List;
import java.util.concurrent.Executor;

public class RoomUserDataSource {
    private final UserDao userDao;
    private final Executor executor;

    public RoomUserDataSource(Context context, Executor executor) {
        this.userDao = AppDatabase.getInstance(context).userDao();
        this.executor = executor;
    }

    public void loadUsersFromRoom(RoomUsersCallback callback) {
        executor.execute(() -> {
            List<User> usersFromRoom = userDao.getAllUsersSync();
            callback.onUsersLoaded(usersFromRoom);
        });
    }

    public void insertUsers(List<User> users) {
        executor.execute(() -> userDao.insert(users));
    }

    public void insertUser(User user) {
        executor.execute(() -> userDao.insert(user));
    }

    public void updateUser(User user) {
        executor.execute(() -> userDao.update(user));
    }

    public void deleteUser(User user) {
        executor.execute(() -> userDao.delete(user));
    }

    public void deleteAllUsers() {
        executor.execute(userDao::deleteAllUsers);
    }

    public interface RoomUsersCallback {
        void onUsersLoaded(List<User> users);
    }
}
