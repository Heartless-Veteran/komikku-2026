package eu.kanade.tachiyomi.ui.reader.viewer

/**
 * Scale modes for manga page display.
 * Inspired by Perfect Viewer.
 */
enum class ScaleMode {
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
    
    companion object {
        fun fromPreference(value: Int): ScaleMode = values().getOrElse(value) { FIT_SCREEN }
    }
}