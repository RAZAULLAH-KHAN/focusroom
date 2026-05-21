package com.focusroom.app.domain.usecase

import com.focusroom.app.core.network.WebSocketClient
import com.focusroom.app.core.network.WebSocketEvent
import com.focusroom.app.domain.model.AvatarState
import com.focusroom.app.domain.model.StatusVibe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

sealed class RoomDomainEvent {
    data class AvatarStateUpdated(val avatar: AvatarState) : RoomDomainEvent()
    data class LofiTrackUpdated(val trackTitle: String, val isPlaying: Boolean, val durationSeconds: Int, val positionSeconds: Int) : RoomDomainEvent()
    data class ParticipantJoined(val userId: String, val username: String, val state: StatusVibe) : RoomDomainEvent()
    data class ParticipantLeft(val userId: String, val username: String) : RoomDomainEvent()
    data class StatsUpdated(val activeMembersCount: Int, val averageFocusMinutes: Int) : RoomDomainEvent()
}

class ObserveRoomStateUseCase @Inject constructor(
    private val webSocketClient: WebSocketClient
) {
    operator fun invoke(): Flow<RoomDomainEvent> {
        return webSocketClient.events.mapNotNull { event ->
            when (event) {
                is WebSocketEvent.AvatarStateChanged -> {
                    RoomDomainEvent.AvatarStateUpdated(
                        AvatarState(
                            userId = event.userId,
                            username = event.username,
                            status = mapToStatusVibe(event.state),
                            positionX = event.positionX,
                            positionY = event.positionY
                        )
                    )
                }
                is WebSocketEvent.LofiTrackChanged -> {
                    RoomDomainEvent.LofiTrackUpdated(
                        trackTitle = event.trackTitle,
                        isPlaying = event.isPlaying,
                        durationSeconds = event.durationSeconds,
                        positionSeconds = event.positionSeconds
                    )
                }
                is WebSocketEvent.UserJoined -> {
                    RoomDomainEvent.ParticipantJoined(
                        userId = event.userId,
                        username = event.username,
                        state = mapToStatusVibe(event.state)
                    )
                }
                is WebSocketEvent.UserLeft -> {
                    RoomDomainEvent.ParticipantLeft(
                        userId = event.userId,
                        username = event.username
                    )
                }
                is WebSocketEvent.RoomStatsUpdated -> {
                    RoomDomainEvent.StatsUpdated(
                        activeMembersCount = event.activeMembersCount,
                        averageFocusMinutes = event.averageFocusMinutes
                    )
                }
            }
        }
    }

    private fun mapToStatusVibe(state: String): StatusVibe {
        return when (state.lowercase()) {
            "focused", "focusing" -> StatusVibe.FOCUSING
            "slacking", "slack" -> StatusVibe.SLACKING
            "sleeping", "sleep" -> StatusVibe.SLEEPING
            "vibing", "vibe" -> StatusVibe.VIBING
            else -> StatusVibe.FOCUSING
        }
    }
}
