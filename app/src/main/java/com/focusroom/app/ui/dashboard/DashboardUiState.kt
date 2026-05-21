package com.focusroom.app.ui.dashboard

import com.focusroom.app.domain.model.FocusRoom

data class DashboardUiState(
    val username: String = "Developer",
    val dailyFocusMinutes: Int = 0,
    val activeFriendsCount: Int = 0,
    val streakDays: Int = 0,
    val recommendedRoom: FocusRoom? = null,
    val roomsList: List<FocusRoom> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
