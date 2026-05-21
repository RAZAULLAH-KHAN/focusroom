package com.focusroom.app.core.network

import com.focusroom.app.data.model.RoomDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor() {

    suspend fun getActiveRooms(): List<RoomDto> {
        delay(800) // Simulate networking latency
        return listOf(
            RoomDto(
                id = "room_synthwave",
                title = "Synthwave Neon Lab",
                membersCount = 14,
                gradientColor = "#8B5CF6", // NeonPurple
                isTrending = true
            ),
            RoomDto(
                id = "room_lofi",
                title = "Lo-Fi & Code Chill",
                membersCount = 28,
                gradientColor = "#06B6D4", // NeonCyan
                isTrending = false
            ),
            RoomDto(
                id = "room_ambient",
                title = "Deep Space Ambient",
                membersCount = 9,
                gradientColor = "#EC4899", // SolidPink
                isTrending = false
            )
        )
    }
}
