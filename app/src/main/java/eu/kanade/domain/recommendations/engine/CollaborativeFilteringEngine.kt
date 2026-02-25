package eu.kanade.domain.recommendations.engine

import eu.kanade.tachiyomi.data.database.models.manga
import tachiyomi.domain.manga.model.Manga

/**
 * Collaborative filtering recommendation engine.
 * Finds users with similar taste and recommends what they liked.
 */
class CollaborativeFilteringEngine {

    /**
     * Calculate similarity between two users based on their manga ratings/interactions.
     */
    fun calculateUserSimilarity(
        user1Interactions: List<UserMangaInteraction>,
        user2Interactions: List<UserMangaInteraction>,
    ): Double {
        // Find common manga
        val commonManga = user1Interactions.map { it.mangaId }
            .intersect(user2Interactions.map { it.mangaId }.toSet())
        
        if (commonManga.size < 3) return 0.0 // Need at least 3 common items
        
        // Calculate cosine similarity
        val user1Vector = commonManga.map { mangaId ->
            user1Interactions.find { it.mangaId == mangaId }?.score ?: 0.0
        }
        val user2Vector = commonManga.map { mangaId ->
            user2Interactions.find { it.mangaId == mangaId }?.score ?: 0.0
        }
        
        return cosineSimilarity(user1Vector, user2Vector)
    }

    /**
     * Get recommendations for a user based on similar users' preferences.
     */
    fun getRecommendations(
        targetUserId: Long,
        targetUserInteractions: List<UserMangaInteraction>,
        allUsersInteractions: Map<Long, List<UserMangaInteraction>>,
        allManga: Map<Long, Manga>,
        topN: Int = 10,
    ): List<Recommendation> {
        // Calculate similarity with all other users
        val similarities = allUsersInteractions
            .filter { it.key != targetUserId }
            .map { (userId, interactions) ->
                userId to calculateUserSimilarity(targetUserInteractions, interactions)
            }
            .filter { it.second > SIMILARITY_THRESHOLD }
            .sortedByDescending { it.second }
            .take(TOP_SIMILAR_USERS)
        
        // Get manga liked by similar users but not read by target
        val targetMangaIds = targetUserInteractions.map { it.mangaId }.toSet()
        
        val candidateScores = mutableMapOf<Long, Double>()
        val candidateCounts = mutableMapOf<Long, Int>()
        
        for ((similarUserId, similarity) in similarities) {
            val similarUserInteractions = allUsersInteractions[similarUserId] ?: continue
            
            for (interaction in similarUserInteractions) {
                if (interaction.mangaId in targetMangaIds) continue // Skip already read
                
                // Weight by similarity and user score
                val weightedScore = similarity * interaction.score
                candidateScores[interaction.mangaId] = 
                    candidateScores.getOrDefault(interaction.mangaId, 0.0) + weightedScore
                candidateCounts[interaction.mangaId] = 
                    candidateCounts.getOrDefault(interaction.mangaId, 0) + 1
            }
        }
        
        // Normalize by count and sort
        return candidateScores
            .map { (mangaId, totalScore) ->
                val count = candidateCounts[mangaId] ?: 1
                val normalizedScore = totalScore / count
                Recommendation(
                    manga = allManga[mangaId],
                    score = normalizedScore,
                    confidence = count.toDouble() / similarities.size,
                )
            }
            .filter { it.manga != null }
            .sortedByDescending { it.score }
            .take(topN)
    }

    private fun cosineSimilarity(vec1: List<Double>, vec2: List<Double>): Double {
        val dotProduct = vec1.zip(vec2).sumOf { (a, b) -> a * b }
        val norm1 = Math.sqrt(vec1.sumOf { it * it })
        val norm2 = Math.sqrt(vec2.sumOf { it * it })
        
        return if (norm1 > 0 && norm2 > 0) dotProduct / (norm1 * norm2) else 0.0
    }

    data class UserMangaInteraction(
        val userId: Long,
        val mangaId: Long,
        val score: Double, // Normalized 0-1 based on read %, bookmark, rating
        val timestamp: Long,
    )

    data class Recommendation(
        val manga: Manga?,
        val score: Double,
        val confidence: Double, // How many similar users liked it
    )

    companion object {
        private const val SIMILARITY_THRESHOLD = 0.3
        private const val TOP_SIMILAR_USERS = 20
    }
}