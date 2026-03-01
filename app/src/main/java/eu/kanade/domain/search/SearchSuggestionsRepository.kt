package eu.kanade.domain.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tachiyomi.core.common.preference.PreferenceStore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for providing search suggestions from various sources.
 */
@Singleton
class SearchSuggestionsRepository @Inject constructor(
    private val preferenceStore: PreferenceStore,
) {

    /**
     * Get trending searches (mock data - in production would come from analytics).
     */
    fun getTrendingSearches(): Flow<List<String>> = flow {
        // In production, this would fetch from a server or local analytics
        emit(
            listOf(
                "Solo Leveling",
                "Chainsaw Man",
                "Jujutsu Kaisen",
                "One Piece",
                "Blue Lock",
                "Spy x Family",
                "My Hero Academia",
                "Attack on Titan",
                "Demon Slayer",
                "Kaguya-sama",
            ),
        )
    }

    /**
     * Get library manga titles for suggestions.
     * This would be populated from the actual library in production.
     */
    fun getLibraryTitles(): Flow<List<String>> = flow {
        // In production, this would fetch from the database
        emit(emptyList())
    }

    /**
     * Get author/artist names for suggestions.
     */
    fun getAuthorNames(): Flow<List<String>> = flow {
        // In production, this would fetch unique authors from library
        emit(emptyList())
    }

    /**
     * Get combined suggestions based on query.
     */
    suspend fun getSuggestions(
        query: String,
        history: List<SearchHistoryItem>,
    ): SearchSuggestions {
        val trending = getTrendingSearches()
        val libraryTitles = getLibraryTitles()
        val authors = getAuthorNames()

        return SearchSuggestions(
            history = history.filter { it.query.contains(query, ignoreCase = true) },
            trending = trending,
            libraryTitles = libraryTitles.filter { it.contains(query, ignoreCase = true) },
            authors = authors.filter { it.contains(query, ignoreCase = true) },
        )
    }

    data class SearchSuggestions(
        val history: List<SearchHistoryItem>,
        val trending: List<String>,
        val libraryTitles: List<String>,
        val authors: List<String>,
    )
}