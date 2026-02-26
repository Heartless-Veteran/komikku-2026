package eu.kanade.domain.manga.interactor

import tachiyomi.domain.manga.model.Manga

/**
 * Natural language search for manga.
 * Parses user queries into structured filters.
 */
class SmartMangaSearch {

    /**
     * Parse natural language query into search parameters.
     */
    fun parseQuery(query: String): SearchParameters {
        val lowerQuery = query.lowercase()
        
        return SearchParameters(
            keywords = extractKeywords(lowerQuery),
            genres = extractGenres(lowerQuery),
            themes = extractThemes(lowerQuery),
            excludeGenres = extractExcludeGenres(lowerQuery),
            status = extractStatus(lowerQuery),
            minChapters = extractMinChapters(lowerQuery),
            maxChapters = extractMaxChapters(lowerQuery),
            similarTo = extractSimilarTo(lowerQuery),
        )
    }

    private fun extractKeywords(query: String): List<String> {
        // Remove known genre/theme words, keep descriptive terms
        val stopWords = GENRE_KEYWORDS + THEME_KEYWORDS + listOf(
            "with", "without", "like", "similar", "to", "something",
            "find", "me", "manga", "anime", "show", "get"
        )
        
        return query.split(" ")
            .filter { it.length > 2 }
            .filter { it !in stopWords }
            .filter { !it.matches(Regex("\\d+")) } // Remove pure numbers
    }

    private fun extractGenres(query: String): List<String> {
        return GENRE_KEYWORDS.filter { query.contains(it) }
    }

    private fun extractThemes(query: String): List<String> {
        return THEME_KEYWORDS.filter { query.contains(it) }
    }

    private fun extractExcludeGenres(query: String): List<String> {
        // Look for "without X" or "no X" patterns
        val excludePattern = Regex("(?:without|no|not)\\s+(\\w+)")
        return excludePattern.findAll(query)
            .map { it.groupValues[1] }
            .filter { it in GENRE_KEYWORDS || it in THEME_KEYWORDS }
            .toList()
    }

    private fun extractStatus(query: String): String? {
        return when {
            query.contains("ongoing") || query.contains("publishing") -> "ongoing"
            query.contains("completed") || query.contains("finished") -> "completed"
            query.contains("hiatus") -> "hiatus"
            query.contains("cancelled") -> "cancelled"
            else -> null
        }
    }

    private fun extractMinChapters(query: String): Int? {
        // Look for "at least X chapters" or "X+ chapters"
        val minPattern = Regex("(?:at least|more than|over|\\+)\\s*(\\d+)")
        return minPattern.find(query)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun extractMaxChapters(query: String): Int? {
        // Look for "less than X chapters" or "under X" or "X- chapters"
        val maxPattern = Regex("(?:less than|under|fewer than|shorter than|\\-)\\s*(\\d+)")
        return maxPattern.find(query)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun extractSimilarTo(query: String): String? {
        // Look for "like X" or "similar to X"
        val similarPattern = Regex("(?:like|similar to)\\s+([\\w\\s]+?)(?:\\s+(?:but|and|with|without)|\\s*$)")
        return similarPattern.find(query)?.groupValues?.get(1)?.trim()
    }

    data class SearchParameters(
        val keywords: List<String> = emptyList(),
        val genres: List<String> = emptyList(),
        val themes: List<String> = emptyList(),
        val excludeGenres: List<String> = emptyList(),
        val status: String? = null,
        val minChapters: Int? = null,
        val maxChapters: Int? = null,
        val similarTo: String? = null,
    ) {
        fun isEmpty(): Boolean = 
            keywords.isEmpty() && genres.isEmpty() && themes.isEmpty()
    }

    companion object {
        private val GENRE_KEYWORDS = listOf(
            "action", "adventure", "comedy", "drama", "fantasy", "horror",
            "mystery", "romance", "sci-fi", "slice of life", "sports", "thriller",
            "isekai", "mecha", "psychological", "supernatural", "shounen", "shoujo",
            "seinen", "josei"
        )

        private val THEME_KEYWORDS = listOf(
            "school", "magic", "demons", "vampires", "zombies", "ghosts",
            "robots", "aliens", "time travel", "reincarnation", "survival",
            "martial arts", "music", "cooking", "games", "virtual reality",
            "post-apocalyptic", "cyberpunk", "steampunk", "detective",
            "female protagonist", "male protagonist", "anti-hero", "villain"
        )
    }
}