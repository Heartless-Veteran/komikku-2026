package eu.kanade.domain.brightness

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import tachiyomi.core.common.preference.PreferenceStore
import java.util.Calendar

/**
 * Repository for smart brightness management.
 */
class SmartBrightnessRepository(
    private val preferenceStore: PreferenceStore,
) {

    fun autoBrightnessEnabled() = preferenceStore.getBoolean("auto_brightness_enabled", false)
    fun brightnessPerManga() = preferenceStore.getBoolean("brightness_per_manga", true)

    /**
     * Calculate brightness based on time of day.
     * Returns value from -100 (darkest) to +100 (brightest)
     */
    fun calculateBrightnessForTime(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 6..8 -> 50 // Morning: bright
            in 9..16 -> 100 // Day: brightest
            in 17..19 -> 50 // Evening: medium
            in 20..22 -> -20 // Night: dim
            else -> -50 // Late night: darkest
        }
    }

    /**
     * Get appropriate brightness for current time.
     */
    fun getCurrentBrightness(): Flow<Int> {
        return combine(
            autoBrightnessEnabled().changes(),
            brightnessPerManga().changes(),
        ) { autoEnabled, _ ->
            if (autoEnabled) {
                calculateBrightnessForTime()
            } else {
                0 // Default brightness
            }
        }
    }

    /**
     * Get saved brightness for specific manga.
     */
    fun getMangaBrightness(mangaId: Long): Flow<Int> {
        return preferenceStore.getInt("manga_brightness_$mangaId", 0).changes()
    }

    /**
     * Save brightness for specific manga.
     */
    suspend fun setMangaBrightness(mangaId: Long, brightness: Int) {
        preferenceStore.getInt("manga_brightness_$mangaId", 0).set(brightness)
    }

    /**
     * Get time-based brightness description.
     */
    fun getTimeDescription(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 6..8 -> "Morning"
            in 9..16 -> "Day"
            in 17..19 -> "Evening"
            in 20..22 -> "Night"
            else -> "Late night"
        }
    }
}