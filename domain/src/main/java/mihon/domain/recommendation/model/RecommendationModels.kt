package mihon.domain.recommendation.model

import java.util.Date

/**
 * Represents a user's reading history entry for recommendation purposes.
 */
data class ReadingHistory(
    val id: Long,
    val mangaId: Long,
    val chaptersRead: Int,
    val totalChapters: Int,
    val timeSpent: Long,
    val lastRead: Date?,
    val rating: Float?,
) {
    val progressPercent: Float
        get() = if (totalChapters > 0) {
            (chaptersRead.toFloat() / totalChapters) * 100
        } else {
            0f
        }
}

/**
 * Data class for tracking reading history updates.
 */
data class ReadingHistoryUpdate(
    val mangaId: Long,
    val chaptersRead: Int,
    val totalChapters: Int,
    val timeSpent: Long,
    val lastRead: Date,
    val rating: Float? = null,
)

/**
 * Represents manga tags/genres for recommendation matching.
 */
data class MangaTags(
    val id: Long,
    val mangaId: Long,
    val genres: List<String>,
    val themes: List<String>,
    val author: String,
    val status: Int,
    val popularity: Int,
)

/**
 * Data class for manga tag updates.
 */
data class MangaTagsUpdate(
    val mangaId: Long,
    val genres: List<String>,
    val themes: List<String> = emptyList(),
    val author: String = "",
    val status: Int = 0,
    val popularity: Int = 0,
)

/**
 * Represents a cached recommendation entry.
 */
data class RecommendationCache(
    val id: Long,
    val mangaId: Long,
    val recommendedMangaId: Long,
    val score: Float,
    val reason: String,
    val generatedAt: Date,
)

/**
 * Data class for creating recommendation cache entries.
 */
data class RecommendationCacheInsert(
    val mangaId: Long,
    val recommendedMangaId: Long,
    val score: Float,
    val reason: String,
    val generatedAt: Date,
)

/**
 * Represents a recommendation result with manga details.
 */
data class Recommendation(
    val mangaId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val score: Float,
    val reason: String,
    val genres: List<String>,
    val author: String?,
    val status: Int,
)

/**
 * Represents a "Because you read X" recommendation group.
 */
data class BecauseYouReadRecommendation(
    val sourceMangaId: Long,
    val sourceMangaTitle: String,
    val sourceMangaThumbnail: String?,
    val recommendations: List<Recommendation>,
)

/**
 * Genre preference extracted from reading history.
 */
data class GenrePreference(
    val genre: String,
    val weight: Float,
)
