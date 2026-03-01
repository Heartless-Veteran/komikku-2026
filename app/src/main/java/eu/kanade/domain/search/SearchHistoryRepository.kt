package eu.kanade.domain.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tachiyomi.core.common.preference.PreferenceStore

/**
 * Repository for search history.
 * Uses PreferenceStore for simple storage (last 20 searches).
 */
class SearchHistoryRepository(
    private val preferenceStore: PreferenceStore,
) {

    fun getSearchHistory(): Flow<List<SearchHistoryItem>> {
        return preferenceStore.getStringSet(SEARCH_HISTORY_KEY, emptySet())
            .changes()
            .map { set ->
                set.map { parseHistoryItem(it) }
                    .sortedByDescending { it.timestamp }
                    .take(MAX_HISTORY_ITEMS)
            }
    }

    suspend fun addSearch(query: String, resultCount: Int = 0) {
        if (query.isBlank()) return
        
        val current = preferenceStore.getStringSet(SEARCH_HISTORY_KEY, emptySet()).get()
        val newItem = createHistoryItemString(query, System.currentTimeMillis(), resultCount)
        
        // Remove existing entry with same query (to move to top)
        val filtered = current.filter { !it.startsWith("$query|") }.toMutableSet()
        filtered.add(newItem)
        
        // Keep only last MAX_HISTORY_ITEMS
        val trimmed = filtered.map { parseHistoryItem(it) }
            .sortedByDescending { it.timestamp }
            .take(MAX_HISTORY_ITEMS)
            .map { createHistoryItemString(it.query, it.timestamp, it.resultCount) }
            .toSet()
        
        preferenceStore.getStringSet(SEARCH_HISTORY_KEY, emptySet()).set(trimmed)
    }

    suspend fun removeSearch(query: String) {
        val current = preferenceStore.getStringSet(SEARCH_HISTORY_KEY, emptySet()).get()
        val filtered = current.filter { !it.startsWith("$query|") }.toSet()
        preferenceStore.getStringSet(SEARCH_HISTORY_KEY, emptySet()).set(filtered)
    }

    suspend fun clearHistory() {
        preferenceStore.getStringSet(SEARCH_HISTORY_KEY, emptySet()).set(emptySet())
    }

    private fun parseHistoryItem(item: String): SearchHistoryItem {
        val parts = item.split("|", limit = 3)
        return SearchHistoryItem(
            query = parts.getOrElse(0) { "" },
            timestamp = parts.getOrElse(1) { "0" }.toLongOrNull() ?: 0,
            resultCount = parts.getOrElse(2) { "0" }.toIntOrNull() ?: 0,
        )
    }

    private fun createHistoryItemString(query: String, timestamp: Long, resultCount: Int): String {
        return "$query|$timestamp|$resultCount"
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_ITEMS = 20
    }
}