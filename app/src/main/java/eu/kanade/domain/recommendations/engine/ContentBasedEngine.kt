package eu.kanade.domain.recommendations.engine

import tachiyomi.domain.manga.model.Manga

/**
 * Content-based recommendation engine.
 * Recommends manga similar to what user liked based on features.
 */
class ContentBasedEngine {

    /**
     * Calculate similarity between two manga based on tags/genres.
     */
    fun calculateMangaSimilarity(manga1: Manga, manga2: Manga): Double {
        val tags1 = extractFeatures(manga1)
        val tags2 = extractFeatures(manga2)
        
        if (tags1.isEmpty() || tags2.isEmpty()) return 0.0
        
        // Jaccard similarity
        val intersection = tags1.intersect(tags2).size
        val union = tags1.union(tags2).size
        
        return if (union > 0) intersection.toDouble() / union else 0.0
    }

    /**
     * Get recommendations based on user's liked manga.
     */
    fun getRecommendations(
        userLikedManga: List<Manga>,
        candidateManga: List<Manga>,
        topN: Int = 10,
    ): List<ContentRecommendation> {
        if (userLikedManga.isEmpty()) return emptyList()
        
        return candidateManga
            .filter { candidate ->
                userLikedManga.none { it.id == candidate.id }
            }
            .map { candidate ->
                // Calculate average similarity to all liked manga
                val avgSimilarity = userLikedManga
                    .map { calculateMangaSimilarity(it, candidate) }
                    .average()
                
                ContentRecommendation(
                    manga = candidate,
                    score = avgSimilarity,
                    matchedFeatures = findMatchedFeatures(userLikedManga, candidate)
                )
            }
            .filter { it.score > SIMILARITY_THRESHOLD }
            .sortedByDescending { it.score }
            .take(topN)
    }

    private fun extractFeatures(manga: Manga): Set<String> {
        val features = mutableSetOf<String>()
        
        // Add genre
        features.addAll(manga.genre?.split(",")?.map { it.trim().lowercase() } ?: emptyList())
        
        // Add source as feature
        features.add("source_${manga.source}")
        
        // Add status
        features.add("status_${manga.status}")
        
        return features.filter { it.isNotBlank() }.toSet()
    }

    private fun findMatchedFeatures(
        likedManga: List<Manga>,
        candidate: Manga,
    ): List<String> {
        val candidateFeatures = extractFeatures(candidate)
        val likedFeatures = likedManga.flatMap { extractFeatures(it) }.toSet()
        
        return candidateFeatures.intersect(likedFeatures).toList()
    }

    data class ContentRecommendation(
        val manga: Manga,
        val score: Double,
        val matchedFeatures: List<String>,
    )

    companion object {
        private const val SIMILARITY_THRESHOLD = 0.2
    }
}