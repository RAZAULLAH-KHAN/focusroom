package com.focusroom.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.focusroom.app.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET dailyFocusMinutes = :minutes WHERE id = :userId")
    suspend fun updateFocusMinutes(userId: String, minutes: Int)

    @Query("UPDATE users SET streakDays = :streak WHERE id = :userId")
    suspend fun updateStreak(userId: String, streak: Int)
}
