package mihon.domain.recommendation.interactor

import kotlinx.coroutines.flow.Flow
import mihon.domain.recommendation.model.BecauseYouReadRecommendation
import mihon.domain.recommendation.model.ReadingHistory
import mihon.domain.recommendation.model.Recommendation
import mihon.domain.recommendation.repository.RecommendationsRepository
import java.util.Date
import kotlin.math.min

/**
 * Interactor for getting AI-powered manga recommendations.
 */
class GetRecommendations(
    private val repository: RecommendationsRepository,
) {
    /**
     * Get personalized "For You" recommendations.
     * Uses collaborative filtering based on reading history.
     */
    suspend fun await(limit: Int = 10): List<Recommendation> {
        return repository.getPersonalizedRecommendations(limit)
    }
}

/**
 * Interactor for getting "Because you read X" recommendations.
 */
class GetBecauseYouReadRecommendations(
    private val repository: RecommendationsRepository,
) {
    /**
     * Get recommendations grouped by source manga.
     */
    suspend fun await(limitPerSource: Int = 5): List<BecauseYouReadRecommendation> {
        return repository.getBecauseYouReadRecommendations(limitPerSource)
    }
}

/**
 * Interactor for tracking reading history.
 */
class TrackReadingHistory(
    private val repository: RecommendationsRepository,
) {
    /**
     * Track a reading session.
     */
    suspend fun await(
        mangaId: Long,
        chaptersRead: Int,
        totalChapters: Int,
        timeSpent: Long,
        rating: Float? = null,
    ) {
        repository.upsertReadingHistory(
            mangaId = mangaId,
            chaptersRead = chaptersRead,
            totalChapters = totalChapters,
            timeSpent = timeSpent,
            lastRead = Date(),
            rating = rating,
        )
    }
}

/**
 * Interactor for getting reading history.
 */
class GetReadingHistory(
    private val repository: RecommendationsRepository,
) {
    /**
     * Get all reading history as a flow.
     */
    fun subscribe(): Flow<List<ReadingHistory>> {
        return repository.getAllReadingHistory()
    }
    
    /**
     * Get reading history for a specific manga.
     */
    suspend fun await(mangaId: Long): ReadingHistory? {
        return repository.getReadingHistory(mangaId)
    }
}

/**
 * Interactor for syncing manga tags.
 */
class SyncMangaTags(
    private val repository: RecommendationsRepository,
) {
    /**
     * Sync manga tags for recommendation matching.
     */
    suspend fun await(
        mangaId: Long,
        genres: List<String>,
        themes: List<String> = emptyList(),
        author: String = "",
        status: Int = 0,
        popularity: Int = 0,
    ) {
        repository.upsertMangaTags(
            mangaId = mangaId,
            genres = genres,
            themes = themes,
            author = author,
            status = status,
            popularity = popularity,
        )
    }
}

/**
 * Interactor for updating recommendations cache.
 */
class UpdateRecommendationsCache(
    private val repository: RecommendationsRepository,
) {
    /**
     * Update recommendations cache for a manga.
     */
    suspend fun await(
        mangaId: Long,
        recommendedMangaId: Long,
        score: Float,
        reason: String,
    ) {
        repository.insertRecommendationCache(
            mangaId = mangaId,
            recommendedMangaId = recommendedMangaId,
            score = score,
            reason = reason,
            generatedAt = Date(),
        )
    }
}

/**
 * Interactor for clearing old recommendations.
 */
class ClearOldRecommendations(
    private val repository: RecommendationsRepository,
) {
    /**
     * Clear recommendations older than specified days.
     */
    suspend fun await(olderThanDays: Int = 7) {
        val cutoffDate = Date(System.currentTimeMillis() - olderThanDays * 24 * 60 * 60 * 1000)
        repository.deleteOldRecommendations(cutoffDate)
    }
}
