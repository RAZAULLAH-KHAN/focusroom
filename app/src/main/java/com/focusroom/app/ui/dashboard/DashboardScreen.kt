package com.focusroom.app.ui.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusroom.app.core.theme.BackgroundDark
import com.focusroom.app.core.theme.CardBackground
import com.focusroom.app.core.theme.NeonCyan
import com.focusroom.app.core.theme.NeonPurple
import com.focusroom.app.core.theme.SolidPink
import com.focusroom.app.core.theme.TextMuted
import com.focusroom.app.domain.model.FocusRoom
import com.focusroom.app.ui.components.PremiumCard

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onRoomClick: (roomId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = NeonPurple,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Welcome Header Section
                    Text(
                        text = "Hey, ${uiState.username} 👋",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your circle is locked in right now.",
                        color = TextMuted,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Bento Grid Layout
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card 1: Daily Streak/Stats
                        BentoCard(
                            modifier = Modifier.weight(1f),
                            title = "Daily Streak",
                            value = "${uiState.streakDays} Days",
                            subtext = "${uiState.dailyFocusMinutes}m focused today",
                            accentColor = NeonPurple,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = SolidPink
                                )
                            }
                        )
                        // Card 2: Friends Online
                        BentoCard(
                            modifier = Modifier.weight(1f),
                            title = "Buddies Online",
                            value = "${uiState.activeFriendsCount} Active",
                            subtext = "Working right now",
                            accentColor = NeonCyan,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Active Buddies",
                                    tint = NeonCyan
                                )
                            }
                        )
                    }
                }

                // Large Premium Featured Room Card with Press Micro-Interaction
                item {
                    uiState.recommendedRoom?.let { room ->
                        var isPressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "CardScale"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                                .clip(RoundedCornerShape(24.dp))
                                .background(CardBackground)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isPressed = true
                                            tryAwaitRelease()
                                            isPressed = false
                                            onRoomClick(room.id)
                                        }
                                    )
                                }
                                .padding(24.dp)
                        ) {
                            Column(modifier = Modifier.align(Alignment.TopStart)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonPurple.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🔥 RECOMMENDATION",
                                        color = NeonPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = room.title,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${room.membersCount} friends vibing inside",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }

                            // Action Indicator at bottom right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Join Room",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Available Rooms Header
                item {
                    Text(
                        text = "Explore Active FocusRooms",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // List of other rooms
                items(uiState.roomsList) { room ->
                    RoomListItem(
                        room = room,
                        onClick = { onRoomClick(room.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    icon: @Composable () -> Unit
) {
    PremiumCard(
        modifier = modifier.height(150.dp),
        cornerRadius = 24.dp
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                icon()
            }
            Column {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtext,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            // Glow stripe indicator on bottom edge
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
        }
    }
}

@Composable
fun RoomListItem(
    room: FocusRoom,
    onClick: () -> Unit
) {
    val indicatorColor = Color(android.graphics.Color.parseColor(room.gradientColor))

    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Colored indicator dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(indicatorColor)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = room.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${room.membersCount} active right now",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
            Text(
                text = "→",
                color = indicatorColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
