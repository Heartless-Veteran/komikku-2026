package eu.kanade.tachiyomi.ui.reader.setting

import android.content.Context
import eu.kanade.domain.manga.model.readerOrientation
import eu.kanade.domain.manga.model.readerScaleMode
import eu.kanade.domain.manga.model.readingMode
import eu.kanade.tachiyomi.data.database.models.Manga
import tachiyomi.core.common.preference.PreferenceStore
import tachiyomi.core.common.preference.getEnum

/**
 * Extended reader preferences stored per manga series.
 */
class PerMangaReaderPreferences(
    private val context: Context,
    private val preferenceStore: PreferenceStore,
) {
    // Key prefix for per-manga preferences
    private fun key(mangaId: Long, suffix: String) = "per_manga_${mangaId}_$suffix"

    /**
     * Reading direction (LTR, RTL, Vertical)
     */
    fun readingDirection(mangaId: Long) = preferenceStore.getEnum(
        key(mangaId, "reading_direction"),
        ReadingMode.DEFAULT,
    )

    /**
     * Scale mode (Fit Screen, Fit Width, etc.)
     */
    fun scaleMode(mangaId: Long) = preferenceStore.getEnum(
        key(mangaId, "scale_mode"),
        ReaderPreferences.ScaleMode.FIT_SCREEN,
    )

    /**
     * Background color (Black, Gray, White, Auto)
     */
    fun backgroundColor(mangaId: Long) = preferenceStore.getInt(
        key(mangaId, "background_color"),
        1, // Default gray
    )

    /**
     * Brightness override (0-100, -1 for system default)
     */
    fun brightness(mangaId: Long) = preferenceStore.getInt(
        key(mangaId, "brightness"),
        -1,
    )

    /**
     * Keep screen on
     */
    fun keepScreenOn(mangaId: Long) = preferenceStore.getBoolean(
        key(mangaId, "keep_screen_on"),
        true,
    )

    /**
     * Show page number indicator
     */
    fun showPageNumber(mangaId: Long) = preferenceStore.getBoolean(
        key(mangaId, "show_page_number"),
        true,
    )

    /**
     * Fullscreen mode
     */
    fun fullscreen(mangaId: Long) = preferenceStore.getBoolean(
        key(mangaId, "fullscreen"),
        true,
    )

    /**
     * Cutout/edge-to-edge short
     */
    fun cutoutShort(mangaId: Long) = preferenceStore.getBoolean(
        key(mangaId, "cutout_short"),
        true,
    )

    /**
     * Gets all preferences as a data class for a manga.
     */
    fun getAllForManga(mangaId: Long): MangaReaderSettings {
        return MangaReaderSettings(
            readingDirection = readingDirection(mangaId).get(),
            scaleMode = scaleMode(mangaId).get(),
            backgroundColor = backgroundColor(mangaId).get(),
            brightness = brightness(mangaId).get(),
            keepScreenOn = keepScreenOn(mangaId).get(),
            showPageNumber = showPageNumber(mangaId).get(),
            fullscreen = fullscreen(mangaId).get(),
            cutoutShort = cutoutShort(mangaId).get(),
        )
    }

    /**
     * Resets all preferences to defaults for a manga.
     */
    fun resetToDefaults(mangaId: Long) {
        readingDirection(mangaId).delete()
        scaleMode(mangaId).delete()
        backgroundColor(mangaId).delete()
        brightness(mangaId).delete()
        keepScreenOn(mangaId).delete()
        showPageNumber(mangaId).delete()
        fullscreen(mangaId).delete()
        cutoutShort(mangaId).delete()
    }

    data class MangaReaderSettings(
        val readingDirection: ReadingMode,
        val scaleMode: ReaderPreferences.ScaleMode,
        val backgroundColor: Int,
        val brightness: Int,
        val keepScreenOn: Boolean,
        val showPageNumber: Boolean,
        val fullscreen: Boolean,
        val cutoutShort: Boolean,
    )
}
