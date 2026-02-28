package eu.kanade.domain.readingstats

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tachiyomi.core.common.preference.PreferenceStore
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for tracking reading sessions and statistics.
 */
@Singleton
class ReadingStatsRepository @Inject constructor(
    private val preferenceStore: PreferenceStore,
) {

    // Session tracking
    private var sessionStartTime: Long = 0
    private var isTracking = false

    // Preferences
    fun readingGoalEnabled() = preferenceStore.getBoolean("reading_goal_enabled", false)
    fun readingGoalMinutes() = preferenceStore.getInt("reading_goal_minutes", 30)
    fun readingStreakEnabled() = preferenceStore.getBoolean("reading_streak_enabled", false)

    // Daily stats
    fun getDailyReadingTime(): Flow<Long> {
        return preferenceStore.getLong("daily_reading_time_${getTodayKey()}", 0).changes()
    }

    fun getReadingStreak(): Flow<Int> {
        return preferenceStore.getInt("reading_streak", 0).changes()
    }

    fun getLastReadDate(): Flow<Long> {
        return preferenceStore.getLong("last_read_date", 0).changes()
    }

    // Start a reading session
    fun startSession() {
        if (!isTracking) {
            sessionStartTime = System.currentTimeMillis()
            isTracking = true
        }
    }

    // End a reading session and save time
    suspend fun endSession() {
        if (isTracking && sessionStartTime > 0) {
            val sessionDuration = System.currentTimeMillis() - sessionStartTime
            addReadingTime(sessionDuration)
            updateStreak()
            isTracking = false
            sessionStartTime = 0
        }
    }

    // Add reading time to today's total
    private suspend fun addReadingTime(durationMs: Long) {
        val todayKey = getTodayKey()
        val currentTime = preferenceStore.getLong("daily_reading_time_$todayKey", 0).get()
        preferenceStore.getLong("daily_reading_time_$todayKey", 0).set(currentTime + durationMs)

        // Also add to total
        val totalTime = preferenceStore.getLong("total_reading_time", 0).get()
        preferenceStore.getLong("total_reading_time", 0).set(totalTime + durationMs)
    }

    // Update reading streak
    private suspend fun updateStreak() {
        val lastRead = preferenceStore.getLong("last_read_date", 0).get()
        val today = getTodayKey()
        val yesterday = today - 1

        val currentStreak = when {
            lastRead == today -> preferenceStore.getInt("reading_streak", 0).get() // Already read today
            lastRead == yesterday -> {
                // Continued streak
                preferenceStore.getInt("reading_streak", 0).get() + 1
            }
            else -> 1 // New streak
        }

        preferenceStore.getInt("reading_streak", 0).set(currentStreak)
        preferenceStore.getLong("last_read_date", 0).set(today)
    }

    // Check if daily goal is reached
    suspend fun isGoalReached(): Boolean {
        if (!readingGoalEnabled().get()) return false

        val goalMinutes = readingGoalMinutes().get()
        val dailyTime = preferenceStore.getLong("daily_reading_time_${getTodayKey()}", 0).get()
        return dailyTime >= TimeUnit.MINUTES.toMillis(goalMinutes.toLong())
    }

    // Get formatted daily reading time
    fun getFormattedDailyTime(): Flow<String> {
        return getDailyReadingTime().map { ms ->
            formatDuration(ms)
        }
    }

    // Get current session duration
    fun getCurrentSessionDuration(): Long {
        return if (isTracking && sessionStartTime > 0) {
            System.currentTimeMillis() - sessionStartTime
        } else 0
    }

    // Reset daily stats (call at midnight)
    suspend fun resetDailyStats() {
        val todayKey = getTodayKey()
        preferenceStore.getLong("daily_reading_time_$todayKey", 0).set(0)
    }

    private fun getTodayKey(): Long {
        return System.currentTimeMillis() / TimeUnit.DAYS.toMillis(1)
    }

    private fun formatDuration(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "${TimeUnit.MILLISECONDS.toSeconds(ms)}s"
        }
    }

    companion object {
        const val PREFS_NAME = "reading_stats"
    }
}