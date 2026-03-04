package eu.kanade.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.kanade.domain.search.SavedSearchRepository
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding

/**
 * Screen that displays saved searches and allows managing them.
 */
@Composable
fun SavedSearchesScreen(
    savedSearches: List<SavedSearchRepository.SavedSearch>,
    onSearchClick: (String) -> Unit,
    onDeleteSearch: (String) -> Unit,
    onToggleNotification: (String, Boolean) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            eu.kanade.presentation.components.SearchToolbar(
                searchQuery = null,
                onChangeSearchQuery = {},
                navigateUp = navigateUp,
                titleContent = {
                    Text(
                        text = "Saved Searches",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        if (savedSearches.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(MaterialTheme.padding.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = MaterialTheme.padding.medium),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "No saved searches yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Save a search from the search screen to get notified of new results",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.padding.small),
                )
            }
        } else {
            LazyColumn(contentPadding = paddingValues) {
                items(savedSearches, key = { it.id }) { search ->
                    SavedSearchItem(
                        search = search,
                        onSearchClick = { onSearchClick(search.query) },
                        onDeleteClick = { onDeleteSearch(search.id) },
                        onToggleNotification = { enabled ->
                            onToggleNotification(search.id, enabled)
                        },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun SavedSearchItem(
    search: SavedSearchRepository.SavedSearch,
    onSearchClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSearchClick)
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = MaterialTheme.padding.small),
            tint = MaterialTheme.colorScheme.primary,
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = search.query,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (search.lastResultCount > 0) {
                Text(
                    text = "${search.lastResultCount} results found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Switch(
            checked = search.notifyOnNewResults,
            onCheckedChange = onToggleNotification,
        )

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete saved search",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}
