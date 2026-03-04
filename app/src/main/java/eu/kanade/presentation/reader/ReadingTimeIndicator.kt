package eu.kanade.presentation.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Displays estimated reading time remaining.
 */
@Composable
fun ReadingTimeIndicator(
    minutesRemaining: Int?,
    modifier: Modifier = Modifier,
) {
    if (minutesRemaining == null) return

    val text = when {
        minutesRemaining < 1 -> "< 1 min"
        minutesRemaining == 1 -> "1 min remaining"
        minutesRemaining < 60 -> "$minutesRemaining min remaining"
        else -> "${minutesRemaining / 60}h ${minutesRemaining % 60}m remaining"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 6.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
