package eu.kanade.presentation.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage

/**
 * Thumbnail strip for quick page navigation.
 * Inspired by Perfect Viewer.
 */
@Composable
fun ThumbnailStrip(
    pages: List<ReaderPage>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    // Auto-scroll to current page when visible
    LaunchedEffect(visible, currentPage) {
        if (visible) {
            listState.animateScrollToItem(currentPage)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(surfaceColor)
                .clickable(onClick = onDismiss), // Dismiss on background click
        ) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(
                    items = pages,
                    key = { index, _ -> index },
                ) { index, page ->
                    val isSelected = index == currentPage
                    ThumbnailItem(
                        page = page,
                        isSelected = isSelected,
                        onClick = { onPageSelected(index) },
                        accentColor = accentColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThumbnailItem(
    page: ReaderPage,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(60.dp)
            .height(64.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Use imageUrl if available, fallback to url, or show page number
        val imageModel = page.imageUrl ?: page.url
        
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Page ${page.number}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
        }
        
        // Page number overlay (always visible for clarity)
        Text(
            text = "${page.number}",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                )
                .padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}