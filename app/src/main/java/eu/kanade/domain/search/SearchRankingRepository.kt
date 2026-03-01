package eu.kanade.domain.search

import tachiyomi.domain.manga.model.Manga

/**
 * Repository for ranking search results intelligently.
 */
class SearchRankingRepository {

    /**
     * Rank and deduplicate search results from multiple sources.
     */
    fun rankResults(
        results: Map<String, List<Manga>>, // source name to results
        libraryManga: List<Manga>,
        query: String,
    ): RankedResults {
        val allResults = mutableListOf<RankedManga>()
        val seenUrls = mutableSetOf<String>()

        // Process each source's results
        results.forEach { (sourceName, mangas) ->
            mangas.forEach { manga ->
                // Skip duplicates
                if (manga.url in seenUrls) {
                    // Add source to existing result
                    val existing = allResults.find { it.manga.url == manga.url }
                    existing?.sources?.add(sourceName)
                    return@forEach
                }

                seenUrls.add(manga.url)

                // Calculate score
                val score = calculateScore(manga, query, libraryManga, sourceName)

                allResults.add(
                    RankedManga(
                        manga = manga,
                        score = score,
                        sources = mutableListOf(sourceName),
                        isInLibrary = libraryManga.any { it.url == manga.url },
                    ),
                )
            }
        }

        // Sort by score descending
        val sorted = allResults.sortedByDescending { it.score }

        return RankedResults(
            results = sorted,
            totalCount = sorted.size,
            libraryCount = sorted.count { it.isInLibrary },
        )
    }

    /**
     * Calculate relevance score for a manga.
     */
    private fun calculateScore(
        manga: Manga,
        query: String,
        libraryManga: List<Manga>,
        sourceName: String,
    ): Float {
        var score = 0f
        val lowerQuery = query.lowercase()
        val title = manga.title.lowercase()

        // Title match score (0-50)
        score += when {
            title == lowerQuery -> 50f // Exact match
            title.startsWith(lowerQuery) -> 40f // Starts with query
            title.contains(lowerQuery) -> 30f // Contains query
            else -> calculateSimilarity(title, lowerQuery) * 20f // Fuzzy match
        }

        // Source reliability (0-20)
        score += when (sourceName.lowercase()) {
            "mangadex" -> 20f
            "mangakakalot", "manganato" -> 15f
            else -> 10f
        }

        // Library bonus (0 or -10)
        // Deprioritize if already in library
        if (libraryManga.any { it.url == manga.url }) {
            score -= 10f
        }

        // TODO: Use chapter count for popularity scoring once Manga model includes it

        return score.coerceIn(0f, 100f)
    }

    /**
     * Calculate string similarity using Levenshtein distance.
     */
    private fun calculateSimilarity(s1: String, s2: String): Float {
        val maxLength = maxOf(s1.length, s2.length)
        if (maxLength == 0) return 1f

        val distance = levenshteinDistance(s1, s2)
        return 1f - (distance.toFloat() / maxLength)
    }

    /**
     * Levenshtein distance between two strings.
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..<m) dp[i][0] = i
        for (j in 0..<n) dp[0][j] = j

        for (i in 1..<m) {
            for (j in 1..<n) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    dp[i][j] = 1 + minOf(
                        dp[i - 1][j],     // deletion
                        dp[i][j - 1],     // insertion
                        dp[i - 1][j - 1], // substitution
                    )
                }
            }
        }

        return dp[m][n]
    }

    data class RankedManga(
        val manga: Manga,
        val score: Float,
        val sources: MutableList<String>,
        val isInLibrary: Boolean,
    ) {
        fun getConfidenceBadge(): String {
            return when {
                score >= 80 -> "Best match"
                score >= 60 -> "Great match"
                score >= 40 -> "Good match"
                else -> ""
            }
        }
    }

    data class RankedResults(
        val results: List<RankedManga>,
        val totalCount: Int,
        val libraryCount: Int,
    )
}