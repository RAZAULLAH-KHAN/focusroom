package com.focusroom.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.focusroom.app.data.local.UserDao
import com.focusroom.app.data.model.UserEntity
import com.focusroom.app.domain.model.User
import com.focusroom.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    private val authTokenKey = stringPreferencesKey("auth_token")

    override fun getUser(userId: String): Flow<User?> {
        return userDao.getUserById(userId).map { entity ->
            entity?.let {
                User(
                    id = it.id,
                    username = it.username,
                    streakDays = it.streakDays,
                    dailyFocusMinutes = it.dailyFocusMinutes,
                    points = it.points,
                    activeRoomId = it.activeRoomId
                )
            }
        }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(
            UserEntity(
                id = user.id,
                username = user.username,
                streakDays = user.streakDays,
                dailyFocusMinutes = user.dailyFocusMinutes,
                points = user.points,
                activeRoomId = user.activeRoomId
            )
        )
    }

    override suspend fun updateFocusMinutes(userId: String, minutes: Int) {
        userDao.updateFocusMinutes(userId, minutes)
    }

    override suspend fun setAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    override fun getAuthToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[authTokenKey]
        }
    }
}
