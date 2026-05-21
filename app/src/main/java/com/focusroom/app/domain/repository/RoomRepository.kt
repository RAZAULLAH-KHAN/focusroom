package com.focusroom.app.domain.repository

import com.focusroom.app.domain.model.FocusRoom
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun getActiveRooms(): Flow<List<FocusRoom>>
    suspend fun refreshRooms()
}
