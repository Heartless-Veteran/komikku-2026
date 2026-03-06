package eu.kanade.presentation.reader

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState

/**
 * Full-screen chapter gallery preview with adjustable grid layout.
 * Allows tapping a thumbnail to navigate to that page.
 */
@Composable
fun ChapterGalleryScreen(
    pages: List<ReaderPage>,
    currentPage: Int,
    readerPreferences: ReaderPreferences,
    onPageSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val columns by readerPreferences.galleryColumns().collectAsState()
    val thumbnailSize by readerPreferences.galleryThumbnailSize().collectAsState()

    BackHandler(onBack = onDismiss)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars), // Handle gesture navigation
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            GalleryTopBar(
                columns = columns,
                onColumnsChange = { newColumns -> readerPreferences.galleryColumns().set(newColumns) },
                thumbnailSize = thumbnailSize,
                onThumbnailSizeChange = { newSize -> readerPreferences.galleryThumbnailSize().set(newSize) },
                onDismiss = onDismiss,
            )

            // Grid
            GalleryGrid(
                pages = pages,
                currentPage = currentPage,
                columns = columns.count,
                thumbnailSize = thumbnailSize,
                onPageSelected = { index ->
                    onPageSelected(index)
                    onDismiss()
                },
            )
        }
    }
}

@Composable
private fun GalleryTopBar(
    columns: ReaderPreferences.Companion.GalleryColumns,
    onColumnsChange: (ReaderPreferences.Companion.GalleryColumns) -> Unit,
    thumbnailSize: ReaderPreferences.Companion.GalleryThumbnailSize,
    onThumbnailSizeChange: (ReaderPreferences.Companion.GalleryThumbnailSize) -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.semantics {
                contentDescription = "Close gallery"
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(KMR.strings.action_close_gallery),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = stringResource(KMR.strings.gallery_preview_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )

        // Column count toggles (2 / 3 / 4)
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            ReaderPreferences.Companion.GalleryColumns.entries.forEach { col ->
                val isSelected = columns == col
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .clickable { onColumnsChange(col) }
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${col.count}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        // Thumbnail size toggles (S / M / L)
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            listOf(
                ReaderPreferences.Companion.GalleryThumbnailSize.SMALL to "S",
                ReaderPreferences.Companion.GalleryThumbnailSize.MEDIUM to "M",
                ReaderPreferences.Companion.GalleryThumbnailSize.LARGE to "L",
            ).forEach { (size, label) ->
                val isSelected = thumbnailSize == size
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .clickable { onThumbnailSizeChange(size) }
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryGrid(
    pages: List<ReaderPage>,
    currentPage: Int,
    columns: Int,
    thumbnailSize: ReaderPreferences.Companion.GalleryThumbnailSize,
    onPageSelected: (Int) -> Unit,
) {
    val aspectRatio = when (thumbnailSize) {
        ReaderPreferences.Companion.GalleryThumbnailSize.SMALL -> 0.6f
        ReaderPreferences.Companion.GalleryThumbnailSize.MEDIUM -> 0.72f
        ReaderPreferences.Companion.GalleryThumbnailSize.LARGE -> 0.85f
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 4.dp,
            end = 4.dp,
            top = 4.dp,
            bottom = 88.dp, // Space for navigation bar/gesture area
        ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed(
            items = pages,
            key = { _, page -> page.url ?: page.index }, // Stable key
        ) { idx, page ->
            GalleryPageItem(
                page = page,
                pageNumber = idx + 1,
                isSelected = idx == currentPage,
                aspectRatio = aspectRatio,
                onClick = { onPageSelected(idx) },
            )
        }
    }
}

@Composable
private fun GalleryPageItem(
    page: ReaderPage,
    pageNumber: Int,
    isSelected: Boolean,
    aspectRatio: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) {
                    accentColor
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val imageModel = page.imageUrl ?: page.url
        if (imageModel != null) {
            SubcomposeAsyncImage(
                model = imageModel,
                contentDescription = "Page $pageNumber",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = {
                    // Error placeholder
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BrokenImage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }

        // Page number badge
        Text(
            text = "$pageNumber",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
