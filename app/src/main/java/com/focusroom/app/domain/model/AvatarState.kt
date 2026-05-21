package com.focusroom.app.domain.model

enum class StatusVibe {
    FOCUSING, // Locked in
    SLACKING, // Distracted/minimizing
    SLEEPING, // Went to sleep/offline
    VIBING    // Lo-fi player open & enjoying
}

data class AvatarState(
    val userId: String,
    val username: String,
    val status: StatusVibe,
    val positionX: Float, // Normalized X position (0.0f - 1.0f) in 2D canvas room
    val positionY: Float  // Normalized Y position (0.0f - 1.0f) in 2D canvas room
)
