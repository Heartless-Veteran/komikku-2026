package eu.kanade.presentation.browse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.kanade.domain.manga.interactor.SmartMangaSearch

/**
 * Smart search bar with natural language support.
 */
@Composable
fun SmartSearchBar(
    onSearch: (SmartMangaSearch.SearchParameters) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val smartSearch = remember { SmartMangaSearch() }
    
    // Cache parsed query to avoid recomputing on every recomposition
    val parsedParams by remember(query) {
        derivedStateOf {
            if (query.isNotBlank()) smartSearch.parseQuery(query) else null
        }
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    "Try: 'action with female lead' or 'like One Piece but shorter'",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        parsedParams?.let { onSearch(it) }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                }
            },
            singleLine = true,
        )
        
        // Show parsed interpretation using cached result
        parsedParams?.let { params ->
            if (!params.isEmpty()) {
                Text(
                    text = formatParsedQuery(params),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                )
            }
        }
    }
}

private fun formatParsedQuery(params: SmartMangaSearch.SearchParameters): String {
    val parts = mutableListOf<String>()
    
    if (params.genres.isNotEmpty()) {
        parts.add("Genres: ${params.genres.joinToString(", ")}")
    }
    if (params.themes.isNotEmpty()) {
        parts.add("Themes: ${params.themes.joinToString(", ")}")
    }
    if (params.excludeGenres.isNotEmpty()) {
        parts.add("Exclude: ${params.excludeGenres.joinToString(", ")}")
    }
    if (params.status != null) {
        parts.add("Status: ${params.status}")
    }
    if (params.minChapters != null) {
        parts.add("Min chapters: ${params.minChapters}")
    }
    if (params.maxChapters != null) {
        parts.add("Max chapters: ${params.maxChapters}")
    }
    if (params.similarTo != null) {
        parts.add("Similar to: ${params.similarTo}")
    }
    
    return if (parts.isEmpty()) "Searching for: ${params.keywords.joinToString(" ")}"
    else parts.joinToString(" • ")
}