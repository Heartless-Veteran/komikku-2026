package eu.kanade.domain.search

/**
 * Represents a single search history entry.
 */
data class SearchHistoryItem(
    val query: String,
    val timestamp: Long,
    val resultCount: Int,
) {
    fun getRelativeTime(): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            diff < 604_800_000 -> "${diff / 86_400_000}d ago"
            else -> "${diff / 604_800_000}w ago"
        }
    }
}