package eu.kanade.domain.recommendations.engine

import eu.kanade.domain.recommendations.engine.CollaborativeFilteringEngine.Recommendation
import eu.kanade.domain.recommendations.engine.ContentBasedEngine.ContentRecommendation
import tachiyomi.domain.manga.model.Manga

/**
 * Hybrid recommendation engine combining collaborative and content-based filtering.
 * 
 * Weight: 70% collaborative, 30% content-based
 * Falls back to content-based when no similar users found.
 */
class HybridRecommendationEngine(
    private val collaborativeEngine: CollaborativeFilteringEngine = CollaborativeFilteringEngine(),
    private val contentEngine: ContentBasedEngine = ContentBasedEngine(),
) {

    /**
     * Get hybrid recommendations for a user.
     */
    fun getRecommendations(
        userId: Long,
        userHistory: UserHistory,
        allUserData: AllUserData,
        candidateManga: List<Manga>,
        topN: Int = 10,
    ): List<HybridRecommendation> {
        // Get collaborative recommendations
        val collaborativeRecs = collaborativeEngine.getRecommendations(
            targetUserId = userId,
            targetUserInteractions = userHistory.interactions,
            allUsersInteractions = allUserData.userInteractions,
            allManga = candidateManga.associateBy { it.id },
            topN = topN * 2, // Get more for hybrid scoring
        )
        
        // Get content-based recommendations
        // Pre-filter candidates to exclude already interacted manga
        val targetMangaIds = userHistory.interactions.map { it.mangaId }.toSet()
        val filteredCandidates = candidateManga.filter { it.id !in targetMangaIds }
        val candidateMap = filteredCandidates.associateBy { it.id }
        
        val likedManga = userHistory.interactions
            .filter { it.score >= MIN_LIKED_SCORE_THRESHOLD }
            .mapNotNull { interaction -> candidateMap[interaction.mangaId] }
        
        val contentRecs = contentEngine.getRecommendations(
            userLikedManga = likedManga,
            candidateManga = filteredCandidates,
            topN = topN * 2,
        )
        
        // Combine scores with weighting
        val hybridScores = mutableMapOf<Long, HybridRecommendation>()
        
        // Add collaborative scores (70% weight)
        for (rec in collaborativeRecs) {
            rec.manga?.let { manga ->
                hybridScores[manga.id] = HybridRecommendation(
                    manga = manga,
                    collaborativeScore = rec.score * COLLABORATIVE_WEIGHT,
                    contentScore = 0.0,
                    confidence = rec.confidence,
                    reason = "Liked by ${(rec.confidence * 100).toInt()}% of similar users",
                )
            }
        }
        
        // Add content scores (30% weight)
        for (rec in contentRecs) {
            val existing = hybridScores[rec.manga.id]
            if (existing != null) {
                // Update existing with content score
                hybridScores[rec.manga.id] = existing.copy(
                    contentScore = rec.score * CONTENT_WEIGHT,
                    reason = "${existing.reason} • Similar to what you like"
                )
            } else {
                hybridScores[rec.manga.id] = HybridRecommendation(
                    manga = rec.manga,
                    collaborativeScore = 0.0,
                    contentScore = rec.score * CONTENT_WEIGHT,
                    confidence = rec.score,
                    reason = "Similar to: ${rec.matchedFeatures.take(3).joinToString(", ")}",
                )
            }
        }
        
        // Calculate final scores and sort
        return hybridScores.values
            .map { it.copy(finalScore = it.collaborativeScore + it.contentScore) }
            .sortedByDescending { it.finalScore }
            .take(topN)
    }

    data class UserHistory(
        val userId: Long,
        val interactions: List<CollaborativeFilteringEngine.UserMangaInteraction>,
    )

    data class AllUserData(
        val userInteractions: Map<Long, List<CollaborativeFilteringEngine.UserMangaInteraction>>,
    )

    data class HybridRecommendation(
        val manga: Manga,
        val collaborativeScore: Double,
        val contentScore: Double,
        val finalScore: Double = 0.0,
        val confidence: Double,
        val reason: String,
    )

    companion object {
        private const val COLLABORATIVE_WEIGHT = 0.7
        private const val CONTENT_WEIGHT = 0.3
        private const val MIN_LIKED_SCORE_THRESHOLD = 0.7
    }
}