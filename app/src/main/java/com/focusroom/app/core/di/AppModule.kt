package com.focusroom.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.focusroom.app.core.network.ApiService
import com.focusroom.app.core.network.WebSocketClient
import com.focusroom.app.data.local.AppDatabase
import com.focusroom.app.data.local.RoomDao
import com.focusroom.app.data.local.UserDao
import com.focusroom.app.data.repository.RoomRepositoryImpl
import com.focusroom.app.data.repository.UserRepositoryImpl
import com.focusroom.app.domain.repository.RoomRepository
import com.focusroom.app.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "focusroom_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideRoomDao(database: AppDatabase): RoomDao {
        return database.roomDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("focusroom_preferences") }
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        dataStore: DataStore<Preferences>
    ): UserRepository {
        return UserRepositoryImpl(userDao, dataStore)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(
        roomDao: RoomDao,
        apiService: ApiService
    ): RoomRepository {
        return RoomRepositoryImpl(roomDao, apiService)
    }
}
