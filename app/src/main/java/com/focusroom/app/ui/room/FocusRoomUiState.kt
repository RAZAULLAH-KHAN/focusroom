package com.focusroom.app.ui.room

import com.focusroom.app.domain.model.AvatarState

data class FocusRoomUiState(
    val roomId: String = "",
    val roomTitle: String = "Lo-Fi & Code Chill",
    val roomGradColor: String = "#8B5CF6",
    val participants: Map<String, AvatarState> = emptyMap(),
    val activeTrackTitle: String = "Rainy Night in Tokyo 🌧️",
    val isTrackPlaying: Boolean = true,
    val trackDurationSeconds: Int = 180,
    val trackPositionSeconds: Int = 0,
    val myState: String = "focused",
    val isLoading: Boolean = false
) {
    val trackProgress: Float
        get() = if (trackDurationSeconds > 0) trackPositionSeconds.toFloat() / trackDurationSeconds else 0f
}
