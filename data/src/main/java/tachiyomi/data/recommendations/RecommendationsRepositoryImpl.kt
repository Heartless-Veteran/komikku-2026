package tachiyomi.data.recommendations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import tachiyomi.data.DatabaseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mihon.domain.recommendation.model.BecauseYouReadRecommendation
import mihon.domain.recommendation.model.GenrePreference
import mihon.domain.recommendation.model.MangaTags
import mihon.domain.recommendation.model.ReadingHistory
import mihon.domain.recommendation.model.Recommendation
import mihon.domain.recommendation.model.RecommendationCache
import mihon.domain.recommendation.repository.RecommendationsRepository
import tachiyomi.core.common.util.system.logcat
import tachiyomi.data.manga.MangaMapper
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.min

class RecommendationsRepositoryImpl(
    private val handler: DatabaseHandler,
) : RecommendationsRepository {

    // Reading History operations
    
    override suspend fun getReadingHistory(mangaId: Long): ReadingHistory? {
        return handler.awaitOneOrNull { 
            recommendationsQueries.getReadingHistoryByMangaId(mangaId, ::mapReadingHistory) 
        }
    }

    override fun getAllReadingHistory(): Flow<List<ReadingHistory>> {
        return handler.subscribeToList { 
            recommendationsQueries.getAllReadingHistory(::mapReadingHistory) 
        }
    }

    override suspend fun upsertReadingHistory(
        mangaId: Long,
        chaptersRead: Int,
        totalChapters: Int,
        timeSpent: Long,
        lastRead: Date,
        rating: Float?,
    ) {
        handler.await {
            recommendationsQueries.upsertReadingHistory(
                mangaId = mangaId,
                chaptersRead = chaptersRead.toLong(),
                totalChapters = totalChapters.toLong(),
                timeSpent = timeSpent,
                lastRead = lastRead,
                rating = rating?.toDouble(),
            )
        }
    }

    override suspend fun getTopGenres(minTimeSpent: Long): List<GenrePreference> {
        return handler.awaitList {
            recommendationsQueries.getTopGenresFromHistory(minTimeSpent)
        }.mapNotNull { row ->
            val genres = row.genres?.split(",")?.map { it.trim() } ?: emptyList()
            val totalTime = row.total_time ?: 0
            genres.map { genre ->
                GenrePreference(
                    genre = genre,
                    weight = totalTime.toFloat() / 60000f, // Weight by minutes spent
                )
            }
        }.flatten()
            .groupBy { it.genre }
            .map { (genre, preferences) ->
                GenrePreference(
                    genre = genre,
                    weight = preferences.sumOf { it.weight.toDouble() }.toFloat(),
                )
            }
            .sortedByDescending { it.weight }
    }

    override suspend fun getRecentlyReadMangaIds(since: Date, limit: Int): List<Long> {
        return handler.awaitList {
            recommendationsQueries.getRecentlyReadMangaIds(since)
        }.take(limit)
    }

    override suspend fun getMostReadMangaIds(minTimeSpent: Long, limit: Int): List<Long> {
        return handler.awaitList {
            recommendationsQueries.getMostReadMangaIds(minTimeSpent, limit.toLong())
        }
    }

    // Manga Tags operations
    
    override suspend fun getMangaTags(mangaId: Long): MangaTags? {
        return handler.awaitOneOrNull { 
            recommendationsQueries.getMangaTagsByMangaId(mangaId, ::mapMangaTags) 
        }
    }

    override suspend fun upsertMangaTags(
        mangaId: Long,
        genres: List<String>,
        themes: List<String>,
        author: String,
        status: Int,
        popularity: Int,
    ) {
        handler.await {
            recommendationsQueries.upsertMangaTags(
                mangaId = mangaId,
                genres = genres.joinToString(","),
                themes = themes.joinToString(","),
                author = author,
                status = status.toLong(),
                popularity = popularity.toLong(),
            )
        }
    }

    override suspend fun getMangaWithSimilarGenres(
        genres: List<String>,
        excludeMangaIds: List<Long>,
        limit: Int,
    ): List<MangaTags> {
        // Get all manga tags and filter by genre similarity
        return handler.awaitList {
            recommendationsQueries.getAllMangaTags(::mapMangaTags)
        }.filter { it.mangaId !in excludeMangaIds }
            .map { tags ->
                val similarity = calculateGenreSimilarity(genres, tags.genres)
                tags to similarity
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    // Recommendations Cache operations
    
    override suspend fun getCachedRecommendations(mangaId: Long, limit: Int): List<RecommendationCache> {
        return handler.awaitList {
            recommendationsQueries.getCachedRecommendations(mangaId, limit.toLong()) { 
                id, _, recommendedMangaId, score, reason, generatedAt, _, _, _, _ ->
                RecommendationCache(
                    id = id,
                    mangaId = mangaId,
                    recommendedMangaId = recommendedMangaId,
                    score = score.toFloat(),
                    reason = reason ?: "",
                    generatedAt = generatedAt,
                )
            }
        }
    }

    override suspend fun insertRecommendationCache(
        mangaId: Long,
        recommendedMangaId: Long,
        score: Float,
        reason: String,
        generatedAt: Date,
    ) {
        handler.await {
            recommendationsQueries.insertRecommendationCache(
                mangaId = mangaId,
                recommendedMangaId = recommendedMangaId,
                score = score.toDouble(),
                reason = reason,
                generatedAt = generatedAt,
            )
        }
    }

    override suspend fun deleteOldRecommendations(olderThan: Date) {
        handler.await {
            recommendationsQueries.deleteOldRecommendations(olderThan)
        }
    }

    override suspend fun clearRecommendationsCache() {
        handler.await {
            recommendationsQueries.clearAllRecommendationsCache()
        }
    }

    override suspend fun isCacheFresh(mangaId: Long): Boolean {
        val cache = getCachedRecommendations(mangaId, 1)
        if (cache.isEmpty()) return false
        
        val oneWeekAgo = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
        return cache.first().generatedAt.after(oneWeekAgo)
    }

    // Recommendation Algorithm
    
    override suspend fun getPersonalizedRecommendations(limit: Int): List<Recommendation> {
        try {
            // Step 1: Get user's top genres from reading history
            val topGenres = getTopGenres(minTimeSpent = 60000)
            if (topGenres.isEmpty()) {
                return getPopularRecommendations(limit)
            }
            
            // Step 2: Get manga the user has already spent time on
            val readMangaIds = getMostReadMangaIds(minTimeSpent = 60000, limit = 100)
            
            // Step 3: Find manga with similar genres not yet read
            val candidates = getRecommendationCandidates(limit * 3)
            
            // Step 4: Score by genre overlap + popularity
            val scoredCandidates = candidates.map { candidate ->
                val genreScore = calculateGenreSimilarity(
                    topGenres.map { it.genre },
                    candidate.genres,
                )
                val popularityScore = min(candidate.popularity / 1000f, 1f) // Normalize popularity
                val finalScore = (genreScore * 0.7f) + (popularityScore * 0.3f)
                
                candidate to finalScore
            }.sortedByDescending { it.second }
            
            // Step 5: Return top recommendations
            return scoredCandidates.take(limit).map { (candidate, score) ->
                val reason = generateReason(topGenres.take(3).map { it.genre }, candidate.genres)
                Recommendation(
                    mangaId = candidate.mangaId,
                    title = "", // Will be populated by UI layer
                    thumbnailUrl = null,
                    score = score,
                    reason = reason,
                    genres = candidate.genres,
                    author = candidate.author.takeIf { it.isNotBlank() },
                    status = candidate.status,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logcat { "Error getting personalized recommendations: ${e.message}" }
            return emptyList()
        }
    }

    override suspend fun getBecauseYouReadRecommendations(
        limitPerSource: Int,
    ): List<BecauseYouReadRecommendation> {
        try {
            // Get recently read manga
            val oneMonthAgo = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
            val recentMangaIds = getRecentlyReadMangaIds(oneMonthAgo, limit = 5)
            
            return recentMangaIds.mapNotNull { mangaId ->
                val sourceTags = getMangaTags(mangaId) ?: return@mapNotNull null
                
                // Find similar manga
                val similarManga = getMangaWithSimilarGenres(
                    genres = sourceTags.genres,
                    excludeMangaIds = recentMangaIds,
                    limit = limitPerSource,
                )
                
                if (similarManga.isEmpty()) return@mapNotNull null
                
                // Get source manga details
                val sourceManga = handler.awaitOneOrNull {
                    mangasQueries.getMangaById(mangaId, MangaMapper::mapManga)
                }
                
                val sourceTitle = sourceManga?.title ?: return@mapNotNull null
                val sourceThumbnail = sourceManga?.thumbnailUrl
                
                val recommendations = similarManga.map { tags ->
                    val similarity = calculateGenreSimilarity(sourceTags.genres, tags.genres)
                    val reason = "Similar to ${sourceTitle}"
                    
                    Recommendation(
                        mangaId = tags.mangaId,
                        title = "", // Will be populated by UI layer
                        thumbnailUrl = null,
                        score = similarity,
                        reason = reason,
                        genres = tags.genres,
                        author = tags.author.takeIf { it.isNotBlank() },
                        status = tags.status,
                    )
                }
                
                BecauseYouReadRecommendation(
                    sourceMangaId = mangaId,
                    sourceMangaTitle = sourceTitle,
                    sourceMangaThumbnail = sourceThumbnail,
                    recommendations = recommendations,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logcat { "Error getting because you read recommendations: ${e.message}" }
            return emptyList()
        }
    }

    override suspend fun getRecommendationCandidates(limit: Int): List<MangaTags> {
        return handler.awaitList {
            recommendationsQueries.getRecommendationCandidates(limit.toLong()) { 
                mangaId, genres, themes, author, popularity, _, _, _, _ ->
                MangaTags(
                    id = 0,
                    mangaId = mangaId,
                    genres = genres?.split(",")?.map { it.trim() } ?: emptyList(),
                    themes = themes?.split(",")?.map { it.trim() } ?: emptyList(),
                    author = author ?: "",
                    status = 0,
                    popularity = popularity?.toInt() ?: 0,
                )
            }
        }
    }

    // Helper methods
    
    private fun calculateGenreSimilarity(genres1: List<String>, genres2: List<String>): Float {
        if (genres1.isEmpty() || genres2.isEmpty()) return 0f
        
        val set1 = genres1.map { it.lowercase() }.toSet()
        val set2 = genres2.map { it.lowercase() }.toSet()
        
        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size
        
        return if (union > 0) intersection.toFloat() / union.toFloat() else 0f
    }
    
    private fun generateReason(userGenres: List<String>, mangaGenres: List<String>): String {
        val matchingGenres = userGenres.filter { userGenre ->
            mangaGenres.any { it.lowercase() == userGenre.lowercase() }
        }
        
        return when {
            matchingGenres.isEmpty() -> "Popular in your reading list"
            matchingGenres.size == 1 -> "Because you like ${matchingGenres.first()}"
            matchingGenres.size == 2 -> "Matches your taste: ${matchingGenres.joinToString(" and ")}"
            else -> "Matches your taste: ${matchingGenres.take(2).joinToString(", ")}, and more"
        }
    }
    
    private suspend fun getPopularRecommendations(limit: Int): List<Recommendation> {
        // Fallback: return popular manga
        return handler.awaitList {
            recommendationsQueries.getRecommendationCandidates(limit.toLong()) { 
                mangaId, genres, _, author, popularity, _, _, _, _ ->
                Recommendation(
                    mangaId = mangaId,
                    title = "",
                    thumbnailUrl = null,
                    score = min((popularity?.toInt() ?: 0) / 1000f, 1f),
                    reason = "Popular among readers",
                    genres = genres?.split(",")?.map { it.trim() } ?: emptyList(),
                    author = author,
                    status = 0,
                )
            }
        }
    }
    
    private fun mapReadingHistory(
        id: Long,
        mangaId: Long,
        chaptersRead: Long,
        totalChapters: Long,
        timeSpent: Long,
        lastRead: Date?,
        rating: Double?,
    ): ReadingHistory {
        return ReadingHistory(
            id = id,
            mangaId = mangaId,
            chaptersRead = chaptersRead.toInt(),
            totalChapters = totalChapters.toInt(),
            timeSpent = timeSpent,
            lastRead = lastRead,
            rating = rating?.toFloat(),
        )
    }
    
    private fun mapMangaTags(
        id: Long,
        mangaId: Long,
        genres: String,
        themes: String?,
        author: String?,
        status: Long?,
        popularity: Long?,
    ): MangaTags {
        return MangaTags(
            id = id,
            mangaId = mangaId,
            genres = genres.split(",").map { it.trim() },
            themes = themes?.split(",")?.map { it.trim() } ?: emptyList(),
            author = author ?: "",
            status = status?.toInt() ?: 0,
            popularity = popularity?.toInt() ?: 0,
        )
    }
}
