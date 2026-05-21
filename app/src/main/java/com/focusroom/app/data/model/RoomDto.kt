package com.focusroom.app.data.model

data class RoomDto(
    val id: String,
    val title: String,
    val membersCount: Int,
    val gradientColor: String,
    val isTrending: Boolean
)
