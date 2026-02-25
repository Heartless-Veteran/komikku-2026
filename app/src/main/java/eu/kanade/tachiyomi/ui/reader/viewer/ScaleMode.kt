package eu.kanade.tachiyomi.ui.reader.viewer

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import dev.icerock.moko.resources.StringResource
import tachiyomi.i18n.MR

/**
 * Scale modes for manga page display.
 * Inspired by Perfect Viewer.
 */
enum class ScaleMode(
    val titleRes: StringResource,
) {
    /**
     * Fit the entire page within the screen bounds.
     * Default behavior.
     */
    FIT_SCREEN(MR.strings.scale_type_fit_screen),

    /**
     * Fit the page width to screen width.
     * Good for reading text-heavy manga.
     */
    FIT_WIDTH(MR.strings.scale_type_fit_width),

    /**
     * Fit the page height to screen height.
     * Good for tall pages.
     */
    FIT_HEIGHT(MR.strings.scale_type_fit_height),

    /**
     * Display at original size without scaling.
     * User must pan to see entire page.
     */
    ORIGINAL_SIZE(MR.strings.scale_type_original_size),

    /**
     * Smart fit - auto-adjusts scale based on image orientation.
     * Maximises content visibility.
     */
    SMART_FIT(MR.strings.scale_type_smart_fit);

    /**
     * Convert ScaleMode to SubsamplingScaleImageView scale type.
     */
    fun toSubsamplingScaleType(): Int {
        return when (this) {
            FIT_SCREEN -> SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE
            FIT_WIDTH -> SubsamplingScaleImageView.SCALE_TYPE_FIT_WIDTH
            FIT_HEIGHT -> SubsamplingScaleImageView.SCALE_TYPE_FIT_HEIGHT
            ORIGINAL_SIZE -> SubsamplingScaleImageView.SCALE_TYPE_ORIGINAL_SIZE
            SMART_FIT -> SubsamplingScaleImageView.SCALE_TYPE_SMART_FIT
        }
    }

    companion object {
        fun fromPreference(value: Int): ScaleMode = entries.getOrElse(value) { FIT_SCREEN }

        val entriesList = entries.toList()
    }
}