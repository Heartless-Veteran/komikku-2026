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
            SMART_CROP -> SubsamplingScaleImageView.SCALE_TYPE_SMART_CROP
        }
    }

    companion object {
        fun fromPreference(value: Int): ScaleMode = entries.getOrElse(value) { FIT_SCREEN }

        val entriesList = entries.toList()
    }
}