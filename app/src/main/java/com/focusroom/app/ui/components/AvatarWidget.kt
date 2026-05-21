package com.focusroom.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusroom.app.core.theme.NeonCyan
import com.focusroom.app.core.theme.NeonPurple
import com.focusroom.app.core.theme.SolidPink
import com.focusroom.app.core.theme.TextMuted
import com.focusroom.app.domain.model.StatusVibe

@Composable
fun AvatarWidget(
    modifier: Modifier = Modifier,
    username: String,
    status: StatusVibe,
    sizeDp: Int = 80
) {
    // 1. Squash & Stretch dynamic spring scale
    var triggerToggle by remember { mutableStateOf(false) }
    
    // Toggle state trigger when status changes to execute pop animation
    LaunchedEffect(key1 = status) {
        triggerToggle = !triggerToggle
    }

    val squashScaleX by animateFloatAsState(
        targetValue = if (triggerToggle) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SquashX"
    )

    val squashScaleY by animateFloatAsState(
        targetValue = if (triggerToggle) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SquashY"
    )

    // 2. Rhythmic Vibing Head Bob (for VIBING state)
    val infiniteTransition = rememberInfiniteTransition(label = "VibeBob")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (status == StatusVibe.VIBING) -10f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bob"
    )

    // 3. Sleeping particles floating up
    val zzzOffset = remember { Animatable(0f) }
    LaunchedEffect(key1 = status) {
        if (status == StatusVibe.SLEEPING) {
            while (true) {
                zzzOffset.snapTo(0f)
                zzzOffset.animateTo(
                    targetValue = -30f,
                    animationSpec = tween(1500, easing = LinearEasing)
                )
            }
        }
    }

    val avatarColor = when (status) {
        StatusVibe.FOCUSING -> NeonPurple
        StatusVibe.SLACKING -> SolidPink
        StatusVibe.SLEEPING -> TextMuted
        StatusVibe.VIBING -> NeonCyan
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer(
                scaleX = squashScaleX,
                scaleY = squashScaleY,
                translationY = bobOffset
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Float indicator symbols above the avatar
            when (status) {
                StatusVibe.SLEEPING -> {
                    Text(
                        text = "Zzz",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .graphicsLayer(translationY = zzzOffset.value, translationX = 15f)
                    )
                }
                StatusVibe.VIBING -> {
                    Text(
                        text = "🎵",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .graphicsLayer(translationY = bobOffset / 2f, translationX = 15f)
                    )
                }
                StatusVibe.FOCUSING -> {
                    Text(
                        text = "⚡",
                        color = Color.Yellow,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .graphicsLayer(translationY = -15f)
                    )
                }
                StatusVibe.SLACKING -> {
                    Text(
                        text = "📱",
                        color = SolidPink,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .graphicsLayer(translationY = -15f, translationX = -12f)
                    )
                }
            }

            // The main vector body of our premium 2D avatar
            Canvas(modifier = Modifier.size(sizeDp.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val radius = canvasWidth / 2f

                // Outer Glowing Ring (if focusing/vibing)
                if (status == StatusVibe.FOCUSING || status == StatusVibe.VIBING) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(avatarColor.copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(radius, radius),
                            radius = radius * 1.3f
                        ),
                        radius = radius * 1.3f
                    )
                }

                // Body Base capsule
                drawRoundRect(
                    color = CardBackground,
                    topLeft = Offset(canvasWidth * 0.1f, canvasHeight * 0.2f),
                    size = Size(canvasWidth * 0.8f, canvasHeight * 0.7f),
                    cornerRadius = CornerRadius(28f, 28f)
                )

                // Colored Helmet outline / Avatar Frame
                drawRoundRect(
                    color = avatarColor,
                    topLeft = Offset(canvasWidth * 0.1f, canvasHeight * 0.2f),
                    size = Size(canvasWidth * 0.8f, canvasHeight * 0.7f),
                    cornerRadius = CornerRadius(28f, 28f),
                    style = Stroke(width = 6f)
                )

                // Face Visor panel
                drawRoundRect(
                    color = Color(0xFF0F1115),
                    topLeft = Offset(canvasWidth * 0.2f, canvasHeight * 0.35f),
                    size = Size(canvasWidth * 0.6f, canvasHeight * 0.4f),
                    cornerRadius = CornerRadius(20f, 20f)
                )

                // Expressive eyes drawing
                val eyeY = canvasHeight * 0.52f
                val eyeWidth = 8f
                val eyeHeight = 12f

                when (status) {
                    StatusVibe.FOCUSING -> {
                        // Locked in determined eyes: '>_<' style lines
                        // Left eye: >
                        drawLine(
                            color = NeonPurple,
                            start = Offset(canvasWidth * 0.32f, eyeY - 4),
                            end = Offset(canvasWidth * 0.40f, eyeY),
                            strokeWidth = 5f
                        )
                        drawLine(
                            color = NeonPurple,
                            start = Offset(canvasWidth * 0.32f, eyeY + 4),
                            end = Offset(canvasWidth * 0.40f, eyeY),
                            strokeWidth = 5f
                        )

                        // Right eye: <
                        drawLine(
                            color = NeonPurple,
                            start = Offset(canvasWidth * 0.68f, eyeY - 4),
                            end = Offset(canvasWidth * 0.60f, eyeY),
                            strokeWidth = 5f
                        )
                        drawLine(
                            color = NeonPurple,
                            start = Offset(canvasWidth * 0.68f, eyeY + 4),
                            end = Offset(canvasWidth * 0.60f, eyeY),
                            strokeWidth = 5f
                        )
                    }
                    StatusVibe.SLACKING -> {
                        // Distracted looking sideways eyes
                        drawCircle(
                            color = SolidPink,
                            radius = 6f,
                            center = Offset(canvasWidth * 0.45f, eyeY)
                        )
                        drawCircle(
                            color = SolidPink,
                            radius = 6f,
                            center = Offset(canvasWidth * 0.65f, eyeY)
                        )
                    }
                    StatusVibe.SLEEPING -> {
                        // Sleeping eyes: '- -' lines
                        drawLine(
                            color = TextMuted,
                            start = Offset(canvasWidth * 0.32f, eyeY),
                            end = Offset(canvasWidth * 0.44f, eyeY),
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = TextMuted,
                            start = Offset(canvasWidth * 0.56f, eyeY),
                            end = Offset(canvasWidth * 0.68f, eyeY),
                            strokeWidth = 4f
                        )
                    }
                    StatusVibe.VIBING -> {
                        // Happy closed eyes: '^ ^' curves
                        // Left eye
                        drawArc(
                            color = NeonCyan,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(canvasWidth * 0.3f, eyeY - 8),
                            size = Size(18f, 16f),
                            style = Stroke(width = 4f)
                        )
                        // Right eye
                        drawArc(
                            color = NeonCyan,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(canvasWidth * 0.56f, eyeY - 8),
                            size = Size(18f, 16f),
                            style = Stroke(width = 4f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = username,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Text(
            text = status.name.lowercase(),
            color = avatarColor.copy(alpha = 0.8f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
