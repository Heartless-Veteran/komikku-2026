package eu.kanade.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.MangaCover
import tachiyomi.domain.source.model.Source
import tachiyomi.presentation.core.components.material.padding
import eu.kanade.presentation.library.components.MangaListItem

/**
 * Universal search screen that searches across library, history, and sources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    libraryResults: List<Manga>,
    historyResults: List<Manga>,
    sourceResults: Map<Source, List<Manga>>,
    isLoading: Boolean,
    onMangaClick: (Manga) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = {
                            onSearch(query)
                            keyboardController?.hide()
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text("Search library, history, sources...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Clear")
                                }
                            }
                        },
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Search suggestions could go here
            }

            // Results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = MaterialTheme.padding.small),
            ) {
                // Library Results
                if (libraryResults.isNotEmpty()) {
                    item {
                        ResultSectionHeader(
                            icon = Icons.Default.Book,
                            title = "Library",
                            count = libraryResults.size,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    items(libraryResults) { manga ->
                        MangaListItem(
                            title = manga.title,
                            coverData = MangaCover(
                                mangaId = manga.id,
                                sourceId = manga.source,
                                isMangaFavorite = manga.favorite,
                                ogUrl = manga.thumbnailUrl,
                                lastModified = manga.coverLastModified,
                            ),
                            badge = {},
                            onClick = { onMangaClick(manga) },
                            onLongClick = {},
                        )
                    }
                }

                // History Results
                if (historyResults.isNotEmpty()) {
                    item {
                        ResultSectionHeader(
                            icon = Icons.Default.History,
                            title = "History",
                            count = historyResults.size,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }

                    items(historyResults) { manga ->
                        MangaListItem(
                            title = manga.title,
                            coverData = MangaCover(
                                mangaId = manga.id,
                                sourceId = manga.source,
                                isMangaFavorite = manga.favorite,
                                ogUrl = manga.thumbnailUrl,
                                lastModified = manga.coverLastModified,
                            ),
                            badge = {},
                            onClick = { onMangaClick(manga) },
                            onLongClick = {},
                        )
                    }
                }

                // Source Results
                sourceResults.forEach { (source, mangas) ->
                    if (mangas.isNotEmpty()) {
                        item {
                            ResultSectionHeader(
                                icon = Icons.Default.Public,
                                title = source.name,
                                count = mangas.size,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        }

                        items(mangas) { manga ->
                            MangaListItem(
                                title = manga.title,
                                coverData = MangaCover(
                                    mangaId = manga.id,
                                    sourceId = manga.source,
                                    isMangaFavorite = manga.favorite,
                                    ogUrl = manga.thumbnailUrl,
                                    lastModified = manga.coverLastModified,
                                ),
                                badge = {},
                                onClick = { onMangaClick(manga) },
                                onLongClick = {},
                            )
                        }
                    }
                }

                // Empty state
                if (!isLoading && libraryResults.isEmpty() && historyResults.isEmpty() && sourceResults.isEmpty()) {
                    item {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(MaterialTheme.padding.medium),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.padding(end = MaterialTheme.padding.small),
        )
        Text(
            text = "$title ($count)",
            style = MaterialTheme.typography.titleSmall,
            color = color,
        )
    }
}