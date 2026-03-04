package eu.kanade.domain.history.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tachiyomi.domain.history.interactor.GetHistoryByMangaId
import tachiyomi.domain.history.model.History
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates reading time estimates based on user's reading history.
 */
@Singleton
class ReadingTimeEstimator @Inject constructor(
    private val getHistoryByMangaId: GetHistoryByMangaId,
) {
    /**
     * Gets average reading speed in pages per minute for a manga.
     * Returns null if not enough data.
     */
    fun getAverageReadingSpeed(mangaId: Long): Flow<Float?> {
        return getHistoryByMangaId.subscribe(mangaId)
            .map { historyList ->
                calculateReadingSpeed(historyList)
            }
    }

    /**
     * Estimates time remaining in current chapter (in minutes).
     */
    fun estimateTimeRemaining(
        readingSpeedPpm: Float?,
        currentPage: Int,
        totalPages: Int,
    ): Int? {
        if (readingSpeedPpm == null || readingSpeedPpm <= 0) return null
        val remainingPages = totalPages - currentPage
        if (remainingPages <= 0) return 0
        return (remainingPages / readingSpeedPpm).toInt().coerceAtLeast(1)
    }

    /**
     * Estimates time to read a chapter (in minutes).
     */
    fun estimateChapterTime(
        readingSpeedPpm: Float?,
        pageCount: Int,
    ): Int? {
        if (readingSpeedPpm == null || readingSpeedPpm <= 0) return null
        return (pageCount / readingSpeedPpm).toInt().coerceAtLeast(1)
    }

    private fun calculateReadingSpeed(historyList: List<History>): Float? {
        val validEntries = historyList.filter { it.timeRead > 0 && it.chapterId > 0 }
        if (validEntries.size < 2) return null // Need at least 2 data points

        val totalTime = validEntries.sumOf { it.timeRead }
        val avgTimePerChapter = totalTime / validEntries.size

        // Estimate: average chapter ~20 pages (will refine with actual page counts)
        val estimatedPagesPerChapter = 20f
        val avgMinutesPerChapter = avgTimePerChapter / 60000f // Convert ms to minutes

        return if (avgMinutesPerChapter > 0) {
            estimatedPagesPerChapter / avgMinutesPerChapter
        } else null
    }
}
