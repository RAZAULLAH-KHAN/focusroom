package com.focusroom.app.domain.repository

import com.focusroom.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun updateFocusMinutes(userId: String, minutes: Int)
    suspend fun setAuthToken(token: String)
    fun getAuthToken(): Flow<String?>
}
