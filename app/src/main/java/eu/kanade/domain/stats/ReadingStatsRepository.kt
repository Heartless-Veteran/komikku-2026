package eu.kanade.domain.stats

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import tachiyomi.domain.history.interactor.GetHistory
import tachiyomi.domain.manga.interactor.GetLibraryManga

/**
 * Calculates and provides reading statistics.
 */
class ReadingStatsRepository(
    private val getHistory: GetHistory,
    private val getLibraryManga: GetLibraryManga,
) {
    /**
     * Gets comprehensive reading statistics.
     */
    fun getStats(): Flow<ReadingStats> {
        return combine(
            getHistory.subscribe("", null, null, null),
            getLibraryManga.subscribe(),
        ) { history, libraryManga ->
            calculateStats(history, libraryManga)
        }
    }

    private fun calculateStats(
        history: List<tachiyomi.domain.history.model.HistoryWithRelations>,
        libraryManga: List<tachiyomi.domain.manga.model.LibraryManga>,
    ): ReadingStats {
        val now = Instant.now()
        
        // Total chapters read
        val totalChaptersRead = history.size
        
        // Total reading time (minutes)
        val totalReadingTimeMinutes = history.sumOf { it.readDuration } / 60000
        
        // Reading streak (consecutive days with reading)
        val streak = calculateStreak(history)
        
        // Weekly activity (last 7 days)
        val weeklyActivity = (0..6).map { daysAgo ->
            val date = now.minus(daysAgo.toLong(), ChronoUnit.DAYS)
            val dayStart = date.truncatedTo(ChronoUnit.DAYS)
            val dayEnd = dayStart.plus(1, ChronoUnit.DAYS)
            
            val chaptersThatDay = history.count { entry ->
                val readAt = entry.readAt ?: return@count false
                val readTime = Instant.ofEpochMilli(readAt.time)
                !readTime.isBefore(dayStart) && readTime.isBefore(dayEnd)
            }
            DayActivity(
                date = dayStart.atZone(ZoneId.systemDefault()).toLocalDate(),
                chaptersRead = chaptersThatDay,
            )
        }.reversed()
        
        // Top manga by reading time
        val topManga = history
            .groupBy { it.mangaId }
            .map { (mangaId, entries) ->
                MangaReadingStats(
                    mangaId = mangaId,
                    mangaTitle = entries.first().ogTitle,
                    chaptersRead = entries.size,
                    totalTimeMinutes = entries.sumOf { it.readDuration } / 60000,
                )
            }
            .sortedByDescending { it.totalTimeMinutes }
            .take(10)
        
        return ReadingStats(
            totalChaptersRead = totalChaptersRead,
            totalReadingTimeMinutes = totalReadingTimeMinutes,
            streakDays = streak,
            librarySize = libraryManga.size,
            weeklyActivity = weeklyActivity,
            topManga = topManga,
        )
    }

    private fun calculateStreak(
        history: List<tachiyomi.domain.history.model.HistoryWithRelations>,
    ): Int {
        if (history.isEmpty()) return 0
        
        val readDates = history
            .mapNotNull { it.readAt }
            .map { Instant.ofEpochMilli(it.time).truncatedTo(ChronoUnit.DAYS) }
            .distinct()
            .sortedDescending()
        
        if (readDates.isEmpty()) return 0
        
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val yesterday = today.minus(1, ChronoUnit.DAYS)
        
        // Check if read today or yesterday
        if (readDates.first() != today && readDates.first() != yesterday) {
            return 0 // Streak broken
        }
        
        var streak = 1
        for (i in 1 until readDates.size) {
            val expectedDate = readDates[i - 1].minus(1, ChronoUnit.DAYS)
            if (readDates[i] == expectedDate) {
                streak++
            } else {
                break
            }
        }
        
        return streak
    }

    data class ReadingStats(
        val totalChaptersRead: Int,
        val totalReadingTimeMinutes: Long,
        val streakDays: Int,
        val librarySize: Int,
        val weeklyActivity: List<DayActivity>,
        val topManga: List<MangaReadingStats>,
    )

    data class DayActivity(
        val date: java.time.LocalDate,
        val chaptersRead: Int,
    )

    data class MangaReadingStats(
        val mangaId: Long,
        val mangaTitle: String,
        val chaptersRead: Int,
        val totalTimeMinutes: Long,
    )
}
