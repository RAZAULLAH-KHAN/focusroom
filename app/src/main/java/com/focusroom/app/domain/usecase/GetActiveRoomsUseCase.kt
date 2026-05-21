package com.focusroom.app.domain.usecase

import com.focusroom.app.domain.model.FocusRoom
import com.focusroom.app.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetActiveRoomsUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    operator fun invoke(): Flow<List<FocusRoom>> {
        return repository.getActiveRooms()
            .onStart {
                try {
                    repository.refreshRooms()
                } catch (e: Exception) {
                    // Fail silently, fallback to cached Room entities
                }
            }
    }
}
