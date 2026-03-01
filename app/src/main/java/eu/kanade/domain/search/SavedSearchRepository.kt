package eu.kanade.domain.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import tachiyomi.core.common.preference.PreferenceStore
import java.util.UUID

/**
 * Repository for saved searches with new result alerts.
 */
class SavedSearchRepository(
    private val preferenceStore: PreferenceStore,
) {

    fun getSavedSearches(): Flow<List<SavedSearch>> {
        return preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).changes()
            .map { set ->
                set.map { parseSavedSearch(it) }.sortedByDescending { it.createdAt }
            }
    }

    suspend fun saveSearch(query: String, filters: SearchFilters = SearchFilters()): SavedSearch {
        val id = UUID.randomUUID().toString()
        val savedSearch = SavedSearch(
            id = id,
            query = query,
            filters = filters,
            createdAt = System.currentTimeMillis(),
            lastCheckedAt = System.currentTimeMillis(),
            lastResultCount = 0,
            notifyOnNewResults = true,
        )

        val current = preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).get()
        val updated = current + encodeSavedSearch(savedSearch)
        preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).set(updated)

        return savedSearch
    }

    suspend fun deleteSearch(id: String) {
        val current = preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).get()
        val updated = current.filter { !it.startsWith("$id|") }.toSet()
        preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).set(updated)
    }

    suspend fun updateSearch(search: SavedSearch) {
        deleteSearch(search.id)
        val current = preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).get()
        val updated = current + encodeSavedSearch(search)
        preferenceStore.getStringSet(SAVED_SEARCHES_KEY, emptySet()).set(updated)
    }

    suspend fun updateLastChecked(id: String, resultCount: Int) {
        val search = getSavedSearches().first().find { it.id == id } ?: return
        updateSearch(
            search.copy(
                lastCheckedAt = System.currentTimeMillis(),
                lastResultCount = resultCount,
            ),
        )
    }

    suspend fun toggleNotifications(id: String, enabled: Boolean) {
        val search = getSavedSearches().first().find { it.id == id } ?: return
        updateSearch(search.copy(notifyOnNewResults = enabled))
    }

    private fun parseSavedSearch(encoded: String): SavedSearch {
        val parts = encoded.split("|", limit = 7)
        return SavedSearch(
            id = parts.getOrElse(0) { "" },
            query = parts.getOrElse(1) { "" },
            filters = SearchFilters(), // Simplified
            createdAt = parts.getOrElse(2) { "0" }.toLongOrNull() ?: 0,
            lastCheckedAt = parts.getOrElse(3) { "0" }.toLongOrNull() ?: 0,
            lastResultCount = parts.getOrElse(4) { "0" }.toIntOrNull() ?: 0,
            notifyOnNewResults = parts.getOrElse(5) { "true" }.toBoolean(),
        )
    }

    private fun encodeSavedSearch(search: SavedSearch): String {
        return "${search.id}|${search.query}|${search.createdAt}|${search.lastCheckedAt}|${search.lastResultCount}|${search.notifyOnNewResults}"
    }

    data class SavedSearch(
        val id: String,
        val query: String,
        val filters: SearchFilters,
        val createdAt: Long,
        val lastCheckedAt: Long,
        val lastResultCount: Int,
        val notifyOnNewResults: Boolean,
    ) {
        fun hasNewResults(currentCount: Int): Boolean {
            return currentCount > lastResultCount
        }

        fun getNewResultCount(currentCount: Int): Int {
            return (currentCount - lastResultCount).coerceAtLeast(0)
        }
    }

    data class SearchFilters(
        val sources: List<String> = emptyList(),
        val genres: List<String> = emptyList(),
        val status: String? = null,
    )

    companion object {
        private const val SAVED_SEARCHES_KEY = "saved_searches"
    }
}