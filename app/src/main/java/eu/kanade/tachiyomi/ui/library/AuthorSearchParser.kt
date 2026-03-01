package eu.kanade.tachiyomi.ui.library

import eu.kanade.tachiyomi.ui.library.LibraryItem

/**
 * Parse author/artist prefix queries.
 * Supports: author:name, artist:name, a:name (both)
 */
object AuthorSearchParser {

    data class AuthorQuery(
        val searchTerm: String,
        val searchField: String, // "author", "artist", or "both"
    )

    fun parse(query: String): AuthorQuery? {
        val trimmed = query.trim()
        
        return when {
            trimmed.startsWith("author:", ignoreCase = true) -> {
                val term = trimmed.removePrefix("author:").trim()
                if (term.isNotBlank()) AuthorQuery(term, "author") else null
            }
            trimmed.startsWith("artist:", ignoreCase = true) -> {
                val term = trimmed.removePrefix("artist:").trim()
                if (term.isNotBlank()) AuthorQuery(term, "artist") else null
            }
            trimmed.startsWith("a:", ignoreCase = true) -> {
                val term = trimmed.removePrefix("a:").trim()
                if (term.isNotBlank()) AuthorQuery(term, "both") else null
            }
            else -> null
        }
    }

    fun matches(item: LibraryItem, query: AuthorQuery): Boolean {
        val manga = item.libraryManga.manga
        return when (query.searchField) {
            "author" -> manga.author?.contains(query.searchTerm, ignoreCase = true) == true
            "artist" -> manga.artist?.contains(query.searchTerm, ignoreCase = true) == true
            "both" -> manga.author?.contains(query.searchTerm, ignoreCase = true) == true ||
                    manga.artist?.contains(query.searchTerm, ignoreCase = true) == true
            else -> false
        }
    }
}