package eu.kanade.presentation.reader.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * Reading timer overlay showing session time and daily progress.
 */
@Composable
fun ReadingTimerOverlay(
    sessionDuration: Long,
    dailyReadingTime: Long,
    goalMinutes: Int,
    streak: Int,
    goalReached: Boolean,
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableLongStateOf(sessionDuration) }

    // Update timer every second
    LaunchedEffect(sessionDuration) {
        while (true) {
            delay(1000)
            currentTime += 1000
        }
    }

    val totalDailyTime = dailyReadingTime + currentTime
    val goalMs = TimeUnit.MINUTES.toMillis(goalMinutes.toLong())
    val progress = (totalDailyTime.toFloat() / goalMs).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(12.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Session timer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = formatTime(currentTime),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Daily progress
            if (goalMinutes > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (progress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.3f,
                                ),
 ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (progress >= 1f) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White,
                            )
                        }
                    }
                    Text(
                        text = "${formatTime(totalDailyTime)} / ${goalMinutes}m",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Streak
            if (streak > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFFF5722),
                    )
                    Text(
                        text = "$streak day streak!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5722),
                    )
                }
            }

            // Goal reached celebration
            AnimatedVisibility(
                visible = goalReached && progress >= 1f,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = "🎉 Goal reached!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%02d:%02d", minutes, seconds)
    }
}