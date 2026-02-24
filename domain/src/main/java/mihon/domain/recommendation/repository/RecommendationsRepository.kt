package mihon.domain.recommendation.repository

import kotlinx.coroutines.flow.Flow
import mihon.domain.recommendation.model.BecauseYouReadRecommendation
import mihon.domain.recommendation.model.GenrePreference
import mihon.domain.recommendation.model.MangaTags
import mihon.domain.recommendation.model.ReadingHistory
import mihon.domain.recommendation.model.Recommendation
import mihon.domain.recommendation.model.RecommendationCache
import java.util.Date

/**
 * Repository interface for managing recommendation-related data.
 */
interface RecommendationsRepository {

    // Reading History operations
    
    /**
     * Get reading history for a specific manga.
     */
    suspend fun getReadingHistory(mangaId: Long): ReadingHistory?
    
    /**
     * Get all reading history entries.
     */
    fun getAllReadingHistory(): Flow<List<ReadingHistory>>
    
    /**
     * Upsert reading history entry.
     */
    suspend fun upsertReadingHistory(
        mangaId: Long,
        chaptersRead: Int,
        totalChapters: Int,
        timeSpent: Long,
        lastRead: Date,
        rating: Float? = null,
    )
    
    /**
     * Get top genres from reading history weighted by time spent.
     */
    suspend fun getTopGenres(minTimeSpent: Long = 60000): List<GenrePreference>
    
    /**
     * Get recently read manga IDs.
     */
    suspend fun getRecentlyReadMangaIds(since: Date, limit: Int = 10): List<Long>
    
    /**
     * Get most read manga IDs by time spent.
     */
    suspend fun getMostReadMangaIds(minTimeSpent: Long = 60000, limit: Int = 10): List<Long>

    // Manga Tags operations
    
    /**
     * Get tags for a specific manga.
     */
    suspend fun getMangaTags(mangaId: Long): MangaTags?
    
    /**
     * Upsert manga tags.
     */
    suspend fun upsertMangaTags(
        mangaId: Long,
        genres: List<String>,
        themes: List<String> = emptyList(),
        author: String = "",
        status: Int = 0,
        popularity: Int = 0,
    )
    
    /**
     * Get manga with similar genres.
     */
    suspend fun getMangaWithSimilarGenres(
        genres: List<String>,
        excludeMangaIds: List<Long>,
        limit: Int = 20,
    ): List<MangaTags>

    // Recommendations Cache operations
    
    /**
     * Get cached recommendations for a manga.
     */
    suspend fun getCachedRecommendations(mangaId: Long, limit: Int = 10): List<RecommendationCache>
    
    /**
     * Insert recommendation into cache.
     */
    suspend fun insertRecommendationCache(
        mangaId: Long,
        recommendedMangaId: Long,
        score: Float,
        reason: String,
        generatedAt: Date = Date(),
    )
    
    /**
     * Delete old recommendations.
     */
    suspend fun deleteOldRecommendations(olderThan: Date)
    
    /**
     * Clear all recommendations cache.
     */
    suspend fun clearRecommendationsCache()
    
    /**
     * Check if cache is fresh (less than 7 days old).
     */
    suspend fun isCacheFresh(mangaId: Long): Boolean

    // Recommendation Algorithm
    
    /**
     * Get personalized recommendations based on reading history.
     * Implements collaborative filtering algorithm.
     */
    suspend fun getPersonalizedRecommendations(limit: Int = 10): List<Recommendation>
    
    /**
     * Get "Because you read X" recommendations.
     */
    suspend fun getBecauseYouReadRecommendations(limitPerSource: Int = 5): List<BecauseYouReadRecommendation>
    
    /**
     * Get recommendation candidates for scoring.
     */
    suspend fun getRecommendationCandidates(limit: Int = 50): List<MangaTags>
}
