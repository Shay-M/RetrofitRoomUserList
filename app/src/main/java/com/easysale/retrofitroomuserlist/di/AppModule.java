package com.easysale.retrofitroomuserlist.di;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.easysale.retrofitroomuserlist.data.api.ApiService;
import com.easysale.retrofitroomuserlist.data.api.Url;
import com.easysale.retrofitroomuserlist.data.db.AppDatabase;
import com.easysale.retrofitroomuserlist.data.db.UserDao;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public static ApiService provideApiService() {
        return new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Provides
    @Singleton
    public static AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "user_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public static UserDao provideUserDao(AppDatabase database) {
        return database.userDao();
    }

    @Provides
    @Singleton
    public static Context provideContext(Application application) {
        return application.getApplicationContext();
    }
}
