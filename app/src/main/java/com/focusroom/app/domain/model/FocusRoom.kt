package com.focusroom.app.domain.model

data class FocusRoom(
    val id: String,
    val title: String,
    val membersCount: Int,
    val gradientColor: String,
    val isTrending: Boolean
)
