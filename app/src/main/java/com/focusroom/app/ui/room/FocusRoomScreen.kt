package com.focusroom.app.ui.room

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusroom.app.core.theme.BackgroundDark
import com.focusroom.app.core.theme.CardBackground
import com.focusroom.app.core.theme.NeonCyan
import com.focusroom.app.core.theme.NeonPurple
import com.focusroom.app.core.theme.SolidPink
import com.focusroom.app.core.theme.TextLight
import com.focusroom.app.core.theme.TextMuted
import com.focusroom.app.domain.model.StatusVibe
import com.focusroom.app.ui.components.AvatarWidget
import com.focusroom.app.ui.components.LofiPlayerWidget
import com.focusroom.app.ui.components.PremiumCard

@Composable
fun FocusRoomScreen(
    viewModel: FocusRoomViewModel,
    roomId: String,
    onBackClick: () -> Unit
) {
    // Bind room parameters to state
    LaunchedEffect(key1 = roomId) {
        viewModel.setRoomInfo(roomId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val indicatorColor = Color(android.graphics.Color.parseColor(uiState.roomGradColor))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Header: title, close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Column {
                        Text(
                            text = uiState.roomTitle,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(indicatorColor)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "${uiState.participants.size + 1} locked in",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Premium Slang Vibe Status Pill Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPill(
                    label = "Deep Focus",
                    isSelected = uiState.myState == "focused",
                    activeColor = NeonPurple,
                    onClick = { viewModel.changeMyStatus(StatusVibe.FOCUSING) }
                )
                StatusPill(
                    label = "Vibe Mode",
                    isSelected = uiState.myState == "vibing",
                    activeColor = NeonCyan,
                    onClick = { viewModel.changeMyStatus(StatusVibe.VIBING) }
                )
                StatusPill(
                    label = "On Break",
                    isSelected = uiState.myState == "slacking",
                    activeColor = SolidPink,
                    onClick = { viewModel.changeMyStatus(StatusVibe.SLACKING) }
                )
                StatusPill(
                    label = "Resting",
                    isSelected = uiState.myState == "sleeping",
                    activeColor = TextMuted,
                    onClick = { viewModel.changeMyStatus(StatusVibe.SLEEPING) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The 2D Virtual Board Room
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
            ) {
                // Background grid lines to give a 2D floor vibe
                CanvasFloorGrid()

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val maxWidthPx = constraints.maxWidth
                    val maxHeightPx = constraints.maxHeight

                    // Render my own avatar at static center bottom
                    val myVibe = when (uiState.myState) {
                        "vibing" -> StatusVibe.VIBING
                        "slacking" -> StatusVibe.SLACKING
                        "sleeping" -> StatusVibe.SLEEPING
                        else -> StatusVibe.FOCUSING
                    }
                    
                    AvatarWidget(
                        username = "Raza (You)",
                        status = myVibe,
                        sizeDp = 70,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                    )

                    // Render others with spring-animated coordinates!
                    uiState.participants.values.forEach { friend ->
                        // Smoothly animate friend positions using spring transitions!
                        val animX by animateFloatAsState(
                            targetValue = friend.positionX,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            ),
                            label = "FriendX"
                        )
                        val animY by animateFloatAsState(
                            targetValue = friend.positionY,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            ),
                            label = "FriendY"
                        )

                        // Map normalized float coordinates (0.0 to 1.0) into coordinate offsets
                        val xDp = (animX * (maxWidthPx / 3.4f)).dp
                        val yDp = (animY * (maxHeightPx / 3.8f)).dp

                        AvatarWidget(
                            username = friend.username,
                            status = friend.status,
                            sizeDp = 64,
                            modifier = Modifier.offset(x = xDp, y = yDp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Embedded Lofi player widget at bottom
            LofiPlayerWidget(
                trackTitle = uiState.activeTrackTitle,
                isPlaying = uiState.isTrackPlaying,
                progress = uiState.trackProgress,
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onSkipClick = { viewModel.skipTrack() }
            )
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.2f) else CardBackground)
            .border(
                1.dp,
                if (isSelected) activeColor else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun CanvasFloorGrid() {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val gridLines = 8
        val gridColor = Color(0x06FFFFFF)

        for (i in 1..gridLines) {
            val x = (width / gridLines) * i
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x, height),
                strokeWidth = 2f
            )
            val y = (height / gridLines) * i
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(width, y),
                strokeWidth = 2f
            )
        }
    }
}
