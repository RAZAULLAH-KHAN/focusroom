package com.focusroom.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusroom.app.domain.model.User
import com.focusroom.app.domain.repository.UserRepository
import com.focusroom.app.domain.usecase.GetActiveRoomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getActiveRoomsUseCase: GetActiveRoomsUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        ensureDefaultUserProfile()
        loadDashboardContent()
    }

    private fun ensureDefaultUserProfile() {
        viewModelScope.launch {
            // Seed a mock user profile inside the database if empty
            val defaultUser = User(
                id = "current_user_raza",
                username = "Raza",
                streakDays = 8,
                dailyFocusMinutes = 145,
                points = 880,
                activeRoomId = null
            )
            userRepository.saveUser(defaultUser)
        }
    }

    private fun loadDashboardContent() {
        viewModelScope.launch {
            combine(
                userRepository.getUser("current_user_raza"),
                getActiveRoomsUseCase()
            ) { user, rooms ->
                val trending = rooms.firstOrNull { it.isTrending } ?: rooms.firstOrNull()
                DashboardUiState(
                    username = user?.username ?: "Raza",
                    dailyFocusMinutes = user?.dailyFocusMinutes ?: 145,
                    streakDays = user?.streakDays ?: 8,
                    activeFriendsCount = rooms.sumOf { it.membersCount },
                    recommendedRoom = trending,
                    roomsList = rooms,
                    isLoading = false
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }
}
