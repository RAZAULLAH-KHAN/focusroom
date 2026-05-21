package com.focusroom.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val streakDays: Int,
    val dailyFocusMinutes: Int,
    val points: Int,
    val activeRoomId: String?
)
