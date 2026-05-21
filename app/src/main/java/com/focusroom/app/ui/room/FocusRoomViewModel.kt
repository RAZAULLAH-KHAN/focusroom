package com.focusroom.app.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusroom.app.core.network.WebSocketClient
import com.focusroom.app.domain.model.AvatarState
import com.focusroom.app.domain.model.StatusVibe
import com.focusroom.app.domain.usecase.ObserveRoomStateUseCase
import com.focusroom.app.domain.usecase.RoomDomainEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusRoomViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val observeRoomStateUseCase: ObserveRoomStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusRoomUiState())
    val uiState: StateFlow<FocusRoomUiState> = _uiState.asStateFlow()

    private var trackProgressJob: Job? = null

    init {
        // Connect to WebSocket client & launch local simulators if no server exists
        webSocketClient.connect()
        webSocketClient.startMockSimulation()

        // Handle live room events
        viewModelScope.launch {
            observeRoomStateUseCase().collectLatest { event ->
                handleRoomEvent(event)
            }
        }

        // Start local music time progression check
        startLocalMusicTimer()
    }

    fun setRoomInfo(roomId: String) {
        val title = when (roomId) {
            "room_synthwave" -> "Synthwave Neon Lab"
            "room_ambient" -> "Deep Space Ambient"
            else -> "Lo-Fi & Code Chill"
        }
        val grad = when (roomId) {
            "room_synthwave" -> "#8B5CF6"
            "room_ambient" -> "#EC4899"
            else -> "#06B6D4"
        }
        _uiState.value = _uiState.value.copy(
            roomId = roomId,
            roomTitle = title,
            roomGradColor = grad
        )
    }

    private fun handleRoomEvent(event: RoomDomainEvent) {
        when (event) {
            is RoomDomainEvent.AvatarStateUpdated -> {
                val updatedParticipants = _uiState.value.participants.toMutableMap()
                updatedParticipants[event.avatar.userId] = event.avatar
                _uiState.value = _uiState.value.copy(participants = updatedParticipants)
            }
            is RoomDomainEvent.LofiTrackUpdated -> {
                _uiState.value = _uiState.value.copy(
                    activeTrackTitle = event.trackTitle,
                    isTrackPlaying = event.isPlaying,
                    trackDurationSeconds = event.durationSeconds,
                    trackPositionSeconds = event.positionSeconds
                )
            }
            is RoomDomainEvent.ParticipantJoined -> {
                val updatedParticipants = _uiState.value.participants.toMutableMap()
                updatedParticipants[event.userId] = AvatarState(
                    userId = event.userId,
                    username = event.username,
                    status = event.state,
                    positionX = (0.2f..0.8f).randomFloat(),
                    positionY = (0.2f..0.8f).randomFloat()
                )
                _uiState.value = _uiState.value.copy(participants = updatedParticipants)
            }
            is RoomDomainEvent.ParticipantLeft -> {
                val updatedParticipants = _uiState.value.participants.toMutableMap()
                updatedParticipants.remove(event.userId)
                _uiState.value = _uiState.value.copy(participants = updatedParticipants)
            }
            is RoomDomainEvent.StatsUpdated -> {
                // Handle statistics internally if needed
            }
        }
    }

    fun changeMyStatus(status: StatusVibe) {
        val stateStr = when (status) {
            StatusVibe.FOCUSING -> "focused"
            StatusVibe.SLACKING -> "slacking"
            StatusVibe.SLEEPING -> "sleeping"
            StatusVibe.VIBING -> "vibing"
        }
        _uiState.value = _uiState.value.copy(myState = stateStr)
        
        // Broadcast through websockets to my friends in the room
        webSocketClient.sendState("current_user_raza", "Raza", stateStr)
    }

    fun togglePlayPause() {
        val nextPlaying = !_uiState.value.isTrackPlaying
        _uiState.value = _uiState.value.copy(isTrackPlaying = nextPlaying)
        
        // Broadcast media change
        // In full websocket production, this sends media payload
    }

    fun skipTrack() {
        val tracks = listOf("Rainy Night in Tokyo 🌧️", "Chillhop Study Beats 🎧", "Lofi Cafe Horizon 🌅", "Neon Dreams 🌌")
        val currentIndex = tracks.indexOf(_uiState.value.activeTrackTitle)
        val nextIndex = (currentIndex + 1) % tracks.size
        
        _uiState.value = _uiState.value.copy(
            activeTrackTitle = tracks[nextIndex],
            trackPositionSeconds = 0,
            isTrackPlaying = true
        )
    }

    private fun startLocalMusicTimer() {
        trackProgressJob?.cancel()
        trackProgressJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                if (state.isTrackPlaying) {
                    val nextPosition = (state.trackPositionSeconds + 1) % state.trackDurationSeconds
                    _uiState.value = state.copy(trackPositionSeconds = nextPosition)
                }
            }
        }
    }

    private fun ClosedRange<Float>.randomFloat() = 
        (start + Math.random() * (endInclusive - start)).toFloat()

    override fun onCleared() {
        super.onCleared()
        trackProgressJob?.cancel()
        webSocketClient.disconnect()
    }
}
