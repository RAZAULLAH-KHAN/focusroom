package com.focusroom.app.core.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

sealed class WebSocketEvent {
    data class AvatarStateChanged(val userId: String, val username: String, val state: String, val positionX: Float, val positionY: Float) : WebSocketEvent()
    data class LofiTrackChanged(val trackTitle: String, val isPlaying: Boolean, val durationSeconds: Int, val positionSeconds: Int) : WebSocketEvent()
    data class UserJoined(val userId: String, val username: String, val state: String) : WebSocketEvent()
    data class UserLeft(val userId: String, val username: String) : WebSocketEvent()
    data class RoomStatsUpdated(val activeMembersCount: Int, val averageFocusMinutes: Int) : WebSocketEvent()
}

@Singleton
class WebSocketClient @Inject constructor(
    private val client: OkHttpClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _events = MutableSharedFlow<WebSocketEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<WebSocketEvent> = _events.asSharedFlow()

    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var isSimulationRunning = false

    fun connect(url: String = "ws://localhost:8080/focusroom") {
        if (isConnected) return

        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                Log.d("WebSocketClient", "Connected to remote server: $url")
                stopMockSimulation()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Parse websocket messages here (e.g. JSON matching states)
                // In production, parse JSON and emit event.
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.e("WebSocketClient", "WebSocket failure, switching to Self-Sufficient Local Mock Simulator", t)
                startMockSimulation()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                Log.d("WebSocketClient", "WebSocket closing: $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                Log.d("WebSocketClient", "WebSocket closed: $reason")
                startMockSimulation()
            }
        })
    }

    fun sendState(userId: String, username: String, state: String) {
        val message = "{\"type\":\"state_change\",\"userId\":\"$userId\",\"username\":\"$username\",\"state\":\"$state\"}"
        if (isConnected && webSocket != null) {
            webSocket?.send(message)
        } else {
            // Local broadcast simulation
            scope.launch {
                _events.emit(WebSocketEvent.AvatarStateChanged(
                    userId = userId,
                    username = username,
                    state = state,
                    positionX = 0.5f,
                    positionY = 0.5f
                ))
            }
        }
    }

    fun startMockSimulation() {
        if (isSimulationRunning) return
        isSimulationRunning = true
        Log.d("WebSocketClient", "Self-Sufficient Mock Simulation Started!")
        
        scope.launch {
            // Mock room initialization
            _events.emit(WebSocketEvent.RoomStatsUpdated(5, 125))
            _events.emit(WebSocketEvent.UserJoined("user_1", "Kai", "vibing"))
            _events.emit(WebSocketEvent.UserJoined("user_2", "Lia", "focused"))
            _events.emit(WebSocketEvent.UserJoined("user_3", "Aris", "slacking"))
            _events.emit(WebSocketEvent.UserJoined("user_4", "Zoe", "sleeping"))
            
            // Loop simulator
            var timePassed = 0
            val tracks = listOf("Rainy Night in Tokyo 🌧️", "Chillhop Study Beats 🎧", "Lofi Cafe Horizon 🌅", "Neon Dreams 🌌")
            var currentTrackIndex = 0
            
            while (isSimulationRunning) {
                delay(4000) // Trigger interactive events every 4 seconds
                timePassed += 4
                
                // Keep changing tracks every 40s
                if (timePassed % 40 == 0) {
                    currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                    _events.emit(WebSocketEvent.LofiTrackChanged(
                        trackTitle = tracks[currentTrackIndex],
                        isPlaying = true,
                        durationSeconds = 180,
                        positionSeconds = 0
                    ))
                } else {
                    // Update track progress
                    _events.emit(WebSocketEvent.LofiTrackChanged(
                        trackTitle = tracks[currentTrackIndex],
                        isPlaying = true,
                        durationSeconds = 180,
                        positionSeconds = (timePassed % 40)
                    ))
                }

                // Randomly shift status of buddies to animate micro-interactions
                when ((1..5).random()) {
                    1 -> {
                        val state = listOf("focused", "vibing", "slacking").random()
                        _events.emit(WebSocketEvent.AvatarStateChanged(
                            userId = "user_1",
                            username = "Kai",
                            state = state,
                            positionX = (0.2f..0.8f).randomFloat(),
                            positionY = (0.2f..0.8f).randomFloat()
                        ))
                    }
                    2 -> {
                        val state = listOf("focused", "sleeping", "vibing").random()
                        _events.emit(WebSocketEvent.AvatarStateChanged(
                            userId = "user_2",
                            username = "Lia",
                            state = state,
                            positionX = (0.2f..0.8f).randomFloat(),
                            positionY = (0.2f..0.8f).randomFloat()
                        ))
                    }
                    3 -> {
                        val state = listOf("slacking", "focused", "vibing").random()
                        _events.emit(WebSocketEvent.AvatarStateChanged(
                            userId = "user_3",
                            username = "Aris",
                            state = state,
                            positionX = (0.2f..0.8f).randomFloat(),
                            positionY = (0.2f..0.8f).randomFloat()
                        ))
                    }
                    4 -> {
                        val state = listOf("sleeping", "slacking", "focused").random()
                        _events.emit(WebSocketEvent.AvatarStateChanged(
                            userId = "user_4",
                            username = "Zoe",
                            state = state,
                            positionX = (0.2f..0.8f).randomFloat(),
                            positionY = (0.2f..0.8f).randomFloat()
                        ))
                    }
                }
            }
        }
    }

    private fun ClosedRange<Float>.randomFloat() = 
        (start + Math.random() * (endInclusive - start)).toFloat()

    fun stopMockSimulation() {
        isSimulationRunning = false
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnect called")
        webSocket = null
        isConnected = false
        stopMockSimulation()
    }
}
