package eu.kanade.presentation.source

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.kanade.domain.source.service.SourceHealthMonitor

/**
 * Visual indicator for source health status.
 */
@Composable
fun SourceHealthIndicator(
    status: SourceHealthMonitor.HealthStatus,
    modifier: Modifier = Modifier,
) {
    val (color, icon) = when (status) {
        SourceHealthMonitor.HealthStatus.HEALTHY -> 
            Color(0xFF4CAF50) to Icons.Default.CheckCircle
        SourceHealthMonitor.HealthStatus.DEGRADED -> 
            Color(0xFFFFC107) to Icons.Default.Warning
        SourceHealthMonitor.HealthStatus.UNSTABLE -> 
            Color(0xFFFF9800) to Icons.Default.Warning
        SourceHealthMonitor.HealthStatus.DOWN -> 
            Color(0xFFF44336) to Icons.Default.Error
        SourceHealthMonitor.HealthStatus.UNKNOWN -> 
            MaterialTheme.colorScheme.outline to null
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = status.name,
                tint = Color.White,
                modifier = Modifier.size(8.dp)
            )
        }
    }
}
