//package com.easysale.retrofitroomuserlist.repository;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Observer;
//import androidx.room.Room;
//import androidx.test.core.app.ApplicationProvider;
//
//
//import com.easysale.retrofitroomuserlist.data.db.AppDatabase;
//import com.easysale.retrofitroomuserlist.data.db.UserDao;
//import com.easysale.retrofitroomuserlist.data.model.User;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.testing.HiltAndroidRule;
//import dagger.hilt.android.testing.HiltAndroidTest;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//@HiltAndroidTest
////@SmallTest
//public class UserRepositoryTest {
//
//    @Rule
//    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
//
//    @Rule
//    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
//
//    @Inject
//    AppDatabase database;
//
//    @Inject
//    UserDao userDao;
//
//    @Inject
//    UserRepository userRepository;
//
//    @Before
//    public void setUp() {
//        hiltRule.inject();
//    }
//
//    @After
//    public void tearDown() {
//        database.close();
//    }
//
//    @Test
//    public void insertAndDeleteUser() throws InterruptedException {
//        // Insert a user
//        User user = new User(1, "John", "Doe", "JohnDoe@email.com", "avatar_url");
//        userDao.insert(user);
//
//        // Ensure the user is inserted
//        LiveData<List<User>> liveData = userDao.getAllUsers();
//        List<User> users = getValue(liveData);
//        assertEquals(1, users.size());
//        assertEquals(user.getId(), users.get(0).getId());
//
//        // Delete the user
//        userRepository.deleteUser(user);
//
//        // Ensure the user is deleted
//        users = getValue(liveData);
//        assertTrue(users.isEmpty());
//    }
//
//    @Test
//    public void insertAndDeleteAllUsers() throws InterruptedException {
//        // Insert multiple users
//        User user1 = new User(1, "John", "Doe", "JohnDoe@email.com", "avatar_url");
//        User user2 = new User(2, "Jane", "Doe", "JaneDoe@email.com", "avatar_url");
//        userDao.insert(user1);
//        userDao.insert(user2);
//
//        // Ensure the users are inserted
//        LiveData<List<User>> liveData = userDao.getAllUsers();
//        List<User> users = getValue(liveData);
//        assertEquals(2, users.size());
//
//        // Delete all users
//        userRepository.deleteAllUsers();
//
//        // Ensure all users are deleted
//        users = getValue(liveData);
//        assertTrue(users.isEmpty());
//    }
//
//    private <T> T getValue(LiveData<T> liveData) throws InterruptedException {
//        final Object[] data = new Object[1];
//        CountDownLatch latch = new CountDownLatch(1);
//        Observer<T> observer = new Observer<T>() {
//            @Override
//            public void onChanged(T t) {
//                data[0] = t;
//                latch.countDown();
//                liveData.removeObserver(this);
//            }
//        };
//        liveData.observeForever(observer);
//        latch.await(2, TimeUnit.SECONDS);
//        return (T) data[0];
//    }
//}
