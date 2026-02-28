package eu.kanade.presentation.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import coil3.compose.AsyncImage
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences

/**
 * Thumbnail strip for quick page navigation with configurable position.
 * Inspired by Perfect Viewer.
 */
@Composable
fun ThumbnailStrip(
    pages: List<ReaderPage>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit,
    position: ReaderPreferences.Companion.GalleryPosition = ReaderPreferences.Companion.GalleryPosition.BOTTOM,
    modifier: Modifier = Modifier,
) {
    when (position) {
        ReaderPreferences.Companion.GalleryPosition.BOTTOM -> {
            HorizontalThumbnailStrip(
                pages = pages,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                visible = visible,
                onDismiss = onDismiss,
                modifier = modifier,
                enterAnimation = slideInVertically { it },
                exitAnimation = slideOutVertically { it },
            )
        }
        ReaderPreferences.Companion.GalleryPosition.TOP -> {
            HorizontalThumbnailStrip(
                pages = pages,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                visible = visible,
                onDismiss = onDismiss,
                modifier = modifier,
                enterAnimation = slideInVertically { -it },
                exitAnimation = slideOutVertically { -it },
            )
        }
        ReaderPreferences.Companion.GalleryPosition.LEFT -> {
            VerticalThumbnailStrip(
                pages = pages,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                visible = visible,
                onDismiss = onDismiss,
                modifier = modifier,
                enterAnimation = slideInHorizontally { -it },
                exitAnimation = slideOutHorizontally { -it },
            )
        }
        ReaderPreferences.Companion.GalleryPosition.RIGHT -> {
            VerticalThumbnailStrip(
                pages = pages,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                visible = visible,
                onDismiss = onDismiss,
                modifier = modifier,
                enterAnimation = slideInHorizontally { it },
                exitAnimation = slideOutHorizontally { it },
            )
        }
    }
}

@Composable
private fun HorizontalThumbnailStrip(
    pages: List<ReaderPage>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enterAnimation: androidx.compose.animation.EnterTransition,
    exitAnimation: androidx.compose.animation.ExitTransition,
) {
    val listState = rememberLazyListState()
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    LaunchedEffect(visible, currentPage) {
        if (visible) {
            listState.animateScrollToItem(currentPage)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterAnimation,
        exit = exitAnimation,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(surfaceColor)
                .clickable(onClick = onDismiss),
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
private fun VerticalThumbnailStrip(
    pages: List<ReaderPage>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enterAnimation: androidx.compose.animation.EnterTransition,
    exitAnimation: androidx.compose.animation.ExitTransition,
) {
    val listState = rememberLazyListState()
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    LaunchedEffect(visible, currentPage) {
        if (visible) {
            listState.animateScrollToItem(currentPage)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterAnimation,
        exit = exitAnimation,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .background(surfaceColor)
                .clickable(onClick = onDismiss),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(
                    items = pages,
                    key = { index, _ -> index },
                ) { index, page ->
                    val isSelected = index == currentPage
                    VerticalThumbnailItem(
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
        val imageModel = page.imageUrl ?: page.url
        
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Page ${page.number}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
        }
        
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

@Composable
private fun VerticalThumbnailItem(
    page: ReaderPage,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(64.dp)
            .height(60.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val imageModel = page.imageUrl ?: page.url
        
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Page ${page.number}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
        }
        
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