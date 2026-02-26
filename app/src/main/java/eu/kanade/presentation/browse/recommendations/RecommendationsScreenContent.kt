package eu.kanade.presentation.browse.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.manga.components.MangaCover
import tachiyomi.i18n.MR
import kotlinx.collections.immutable.ImmutableList
import mihon.domain.recommendation.model.BecauseYouReadRecommendation
import mihon.domain.recommendation.model.Recommendation
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun RecommendationsScreenContent(
    forYouRecommendations: ImmutableList<Recommendation>,
    becauseYouReadRecommendations: ImmutableList<BecauseYouReadRecommendation>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onMangaClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = if (isLoading) Alignment.Center else Alignment.TopStart,
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                ErrorContent(
                    error = error,
                    onRefresh = onRefresh,
                )
            }
            forYouRecommendations.isEmpty() && becauseYouReadRecommendations.isEmpty() -> {
                EmptyContent(onRefresh = onRefresh)
            }
            else -> {
                RecommendationsList(
                    forYouRecommendations = forYouRecommendations,
                    becauseYouReadRecommendations = becauseYouReadRecommendations,
                    onMangaClick = onMangaClick,
                    onRefresh = onRefresh,
                )
            }
        }
    }
}

@Composable
private fun RecommendationsList(
    forYouRecommendations: ImmutableList<Recommendation>,
    becauseYouReadRecommendations: ImmutableList<BecauseYouReadRecommendation>,
    onMangaClick: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = MaterialTheme.padding.medium),
    ) {
        // Header with refresh button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.padding.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(MR.strings.recommendations_for_you),
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(MR.strings.action_refresh),
                    )
                }
            }
        }
        
        // "For You" section
        if (forYouRecommendations.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(MaterialTheme.padding.medium))
                Text(
                    text = stringResource(MR.strings.recommendations_personalized),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                )
                Spacer(modifier = Modifier.height(MaterialTheme.padding.small))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = MaterialTheme.padding.medium),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                ) {
                    items(
                        items = forYouRecommendations,
                        key = { it.mangaId },
                    ) { recommendation ->
                        ForYouCard(
                            recommendation = recommendation,
                            onClick = { onMangaClick(recommendation.mangaId) },
                        )
                    }
                }
            }
        }
        
        // "Because you read" sections
        becauseYouReadRecommendations.forEach { group ->
            item {
                Spacer(modifier = Modifier.height(MaterialTheme.padding.large))
                BecauseYouReadSection(
                    group = group,
                    onMangaClick = onMangaClick,
                )
            }
        }
        
        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(MaterialTheme.padding.large))
        }
    }
}

@Composable
private fun ForYouCard(
    recommendation: Recommendation,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column {
            MangaCover.Book(
                data = recommendation.thumbnailUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                scale = ContentScale.Crop,
            )
            
            Column(
                modifier = Modifier.padding(MaterialTheme.padding.small),
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                
                Text(
                    text = recommendation.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
                
                if (recommendation.genres.isNotEmpty()) {
                    Text(
                        text = recommendation.genres.take(2).joinToString(", "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BecauseYouReadSection(
    group: BecauseYouReadRecommendation,
    onMangaClick: (Long) -> Unit,
) {
    Column {
        // Header showing source manga
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.padding.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangaCover.Book(
                data = group.sourceMangaThumbnail,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.width(MaterialTheme.padding.small))
            Column {
                Text(
                    text = stringResource(MR.strings.recommendations_because_you_read),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = group.sourceMangaTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        
        Spacer(modifier = Modifier.height(MaterialTheme.padding.small))
        
        // Horizontal list of recommendations
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.padding.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        ) {
            items(
                items = group.recommendations,
                key = { it.mangaId },
            ) { recommendation ->
                RecommendationSmallCard(
                    recommendation = recommendation,
                    onClick = { onMangaClick(recommendation.mangaId) },
                )
            }
        }
    }
}

@Composable
private fun RecommendationSmallCard(
    recommendation: Recommendation,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column {
            MangaCover.Book(
                data = recommendation.thumbnailUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                scale = ContentScale.Crop,
            )
            
            Column(
                modifier = Modifier.padding(MaterialTheme.padding.small),
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                
                Text(
                    text = recommendation.reason,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.padding.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Recommend,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.padding.medium))
        Text(
            text = stringResource(MR.strings.recommendations_empty_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.padding.small))
        Text(
            text = stringResource(MR.strings.recommendations_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.padding.medium))
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(MR.strings.action_refresh),
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.padding.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(MR.strings.recommendations_error_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.padding.small))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.padding.medium))
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(MR.strings.action_refresh),
            )
        }
    }
}
