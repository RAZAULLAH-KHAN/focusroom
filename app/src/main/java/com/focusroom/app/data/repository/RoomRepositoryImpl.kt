package com.focusroom.app.data.repository

import com.focusroom.app.core.network.ApiService
import com.focusroom.app.data.local.RoomDao
import com.focusroom.app.data.model.RoomEntity
import com.focusroom.app.domain.model.FocusRoom
import com.focusroom.app.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val roomDao: RoomDao,
    private val apiService: ApiService
) : RoomRepository {

    override fun getActiveRooms(): Flow<List<FocusRoom>> {
        return roomDao.getAllRooms().map { entities ->
            entities.map { entity ->
                FocusRoom(
                    id = entity.id,
                    title = entity.title,
                    membersCount = entity.membersCount,
                    gradientColor = entity.gradientColor,
                    isTrending = entity.isTrending
                )
            }
        }
    }

    override suspend fun refreshRooms() {
        val dtos = apiService.getActiveRooms()
        val entities = dtos.map { dto ->
            RoomEntity(
                id = dto.id,
                title = dto.title,
                membersCount = dto.membersCount,
                gradientColor = dto.gradientColor,
                isTrending = dto.isTrending
            )
        }
        roomDao.clearRooms()
        roomDao.insertRooms(entities)
    }
}
