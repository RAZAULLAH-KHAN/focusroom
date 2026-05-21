package com.focusroom.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val id: String,
    val title: String,
    val membersCount: Int,
    val gradientColor: String,
    val isTrending: Boolean
)
