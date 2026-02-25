package eu.kanade.tachiyomi.ui.reader.viewer

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 * Scale modes for manga page display.
 * Inspired by Perfect Viewer.
 */
enum class ScaleMode(
    val titleRes: Int? = null,
) {
    /**
     * Fit the entire page within the screen bounds.
     * Default behavior.
     */
    FIT_SCREEN,

    /**
     * Fit the page width to screen width.
     * Good for reading text-heavy manga.
     */
    FIT_WIDTH,

    /**
     * Fit the page height to screen height.
     * Good for tall pages.
     */
    FIT_HEIGHT,

    /**
     * Display at original size without scaling.
     * User must pan to see entire page.
     */
    ORIGINAL_SIZE,

    /**
     * Smart crop - automatically crop white/black margins.
     * Maximizes content visibility.
     */
    SMART_CROP;

    /**
     * Convert ScaleMode to SubsamplingScaleImageView scale type
     */
    fun toSubsamplingScaleType(): Int {
        return when (this) {
            FIT_SCREEN -> SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE
            FIT_WIDTH -> SubsamplingScaleImageView.SCALE_TYPE_FIT_WIDTH
            FIT_HEIGHT -> SubsamplingScaleImageView.SCALE_TYPE_FIT_HEIGHT
            ORIGINAL_SIZE -> SubsamplingScaleImageView.SCALE_TYPE_ORIGINAL_SIZE
            // CENTER_CROP crops to fill the view, effectively removing white/black margins
            SMART_CROP -> SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP
        }
    }

    companion object {
        /** Bits 6-8 of [tachiyomi.domain.manga.model.Manga.viewerFlags] used for per-manga scale mode. */
        const val MASK = 0x000001C0

        fun fromPreference(value: Int): ScaleMode = entries.getOrElse(value) { FIT_SCREEN }

        /**
         * Returns the [ScaleMode] encoded in the given [flags] (from [tachiyomi.domain.manga.model.Manga.viewerFlags]),
         * or null if no per-manga override has been set (i.e. the global preference should be used).
         *
         * Values are stored as (ordinal + 1) shifted left by 6 bits, so that 0 means "not set".
         */
        fun fromMangaFlags(flags: Long): ScaleMode? {
            val stored = ((flags and MASK.toLong()) shr 6).toInt()
            return if (stored == 0) null else entries.getOrNull(stored - 1)
        }

        val entriesList = entries.toList()
    }
}