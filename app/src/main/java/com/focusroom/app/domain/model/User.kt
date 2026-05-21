package com.focusroom.app.domain.model

data class User(
    val id: String,
    val username: String,
    val streakDays: Int,
    val dailyFocusMinutes: Int,
    val points: Int,
    val activeRoomId: String?
)
