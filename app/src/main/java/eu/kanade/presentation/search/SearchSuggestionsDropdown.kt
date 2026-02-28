package eu.kanade.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import eu.kanade.domain.search.SearchHistoryRepository
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

/**
 * Search suggestions dropdown with history, trending, and smart suggestions.
 */
@Composable
fun SearchSuggestionsDropdown(
    query: String,
    history: List<SearchHistoryRepository.SearchHistoryItem>,
    trending: List<String>,
    libraryTitles: List<String>,
    onSuggestionClick: (String) -> Unit,
    onHistoryItemDelete: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (query.isBlank() && history.isEmpty() && trending.isEmpty()) return

    val filteredHistory = history.filter { it.query.contains(query, ignoreCase = true) }
    val filteredTrending = trending.filter { it.contains(query, ignoreCase = true) }
    val filteredLibrary = libraryTitles.filter { it.contains(query, ignoreCase = true) }

    if (filteredHistory.isEmpty() && filteredTrending.isEmpty() && filteredLibrary.isEmpty() && query.isBlank()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.padding.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        LazyColumn {
            // Smart suggestions based on query
            if (query.isNotBlank()) {
                item {
                    SuggestionSectionHeader(
                        icon = Icons.Default.Search,
                        title = "Suggestions",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                // Library matches
                items(filteredLibrary.take(3)) { title ->
                    LibrarySuggestionItem(
                        title = title,
                        query = query,
                        onClick = { onSuggestionClick(title) },
                    )
                }

                // Trending matches
                items(filteredTrending.take(3)) { trend ->
                    TrendingSuggestionItem(
                        title = trend,
                        query = query,
                        onClick = { onSuggestionClick(trend) },
                    )
                }

                if (filteredLibrary.isNotEmpty() || filteredTrending.isNotEmpty()) {
                    item { HorizontalDivider() }
                }
            }

            // Recent searches
            if (filteredHistory.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = stringResource(MR.strings.recent_searches),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        IconButton(onClick = onClearHistory) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(MR.strings.action_remove),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                items(filteredHistory) { item ->
                    HistorySuggestionItem(
                        item = item,
                        query = query,
                        onClick = { onSuggestionClick(item.query) },
                        onDelete = { onHistoryItemDelete(item.query) },
                    )
                }
            }

            // Trending (when no query)
            if (query.isBlank() && trending.isNotEmpty()) {
                item {
                    SuggestionSectionHeader(
                        icon = Icons.Default.TrendingUp,
                        title = "Trending",
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }

                items(trending.take(5)) { trend ->
                    TrendingSuggestionItem(
                        title = trend,
                        query = "",
                        onClick = { onSuggestionClick(trend) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tint,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = tint,
        )
    }
}

@Composable
private fun LibrarySuggestionItem(
    title: String,
    query: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = highlightMatch(title, query),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "Library",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun TrendingSuggestionItem(
    title: String,
    query: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.tertiary,
        )

        Text(
            text = highlightMatch(title, query),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "Trending",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
private fun HistorySuggestionItem(
    item: SearchHistoryRepository.SearchHistoryItem,
    query: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = MaterialTheme.padding.medium, end = MaterialTheme.padding.small, vertical = MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = MaterialTheme.padding.medium),
        ) {
            Text(
                text = highlightMatch(item.query, query),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = item.getRelativeTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (item.resultCount > 0) {
            Text(
                text = "${item.resultCount}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = MaterialTheme.padding.small),
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(MR.strings.action_remove),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun highlightMatch(text: String, query: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        if (query.isBlank()) {
            append(text)
            return@buildAnnotatedString
        }

        val startIndex = text.indexOf(query, ignoreCase = true)
        if (startIndex == -1) {
            append(text)
            return@buildAnnotatedString
        }

        append(text.substring(0, startIndex))
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
            append(text.substring(startIndex, startIndex + query.length))
        }
        append(text.substring(startIndex + query.length))
    }
}