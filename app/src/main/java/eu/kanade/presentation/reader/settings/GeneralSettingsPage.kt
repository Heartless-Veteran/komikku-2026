package eu.kanade.presentation.reader.settings

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import eu.kanade.tachiyomi.ui.reader.viewer.ScaleMode
import eu.kanade.tachiyomi.util.system.hasDisplayCutout
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState

private val themes = listOf(
    MR.strings.black_background to 1,
    MR.strings.gray_background to 2,
    MR.strings.white_background to 0,
    MR.strings.automatic_background to 3,
)

private val flashColors = listOf(
    MR.strings.pref_flash_style_black to ReaderPreferences.FlashColor.BLACK,
    MR.strings.pref_flash_style_white to ReaderPreferences.FlashColor.WHITE,
    MR.strings.pref_flash_style_white_black to ReaderPreferences.FlashColor.WHITE_BLACK,
)

// KMK --> Gallery position options
private val galleryPositions = listOf(
    KMR.strings.gallery_position_top to ReaderPreferences.Companion.GalleryPosition.TOP,
    KMR.strings.gallery_position_bottom to ReaderPreferences.Companion.GalleryPosition.BOTTOM,
    KMR.strings.gallery_position_left to ReaderPreferences.Companion.GalleryPosition.LEFT,
    KMR.strings.gallery_position_right to ReaderPreferences.Companion.GalleryPosition.RIGHT,
)

private val galleryThumbnailSizes = listOf(
    KMR.strings.gallery_size_small to ReaderPreferences.Companion.GalleryThumbnailSize.SMALL,
    KMR.strings.gallery_size_medium to ReaderPreferences.Companion.GalleryThumbnailSize.MEDIUM,
    KMR.strings.gallery_size_large to ReaderPreferences.Companion.GalleryThumbnailSize.LARGE,
)

private val scaleModes = listOf(
    KMR.strings.scale_mode_fit_screen to ScaleMode.FIT_SCREEN,
    KMR.strings.scale_mode_fit_width to ScaleMode.FIT_WIDTH,
    KMR.strings.scale_mode_fit_height to ScaleMode.FIT_HEIGHT,
    KMR.strings.scale_mode_original to ScaleMode.ORIGINAL_SIZE,
    KMR.strings.scale_mode_smart_fit to ScaleMode.SMART_FIT,
)
// KMK <--

@Composable
internal fun GeneralPage(screenModel: ReaderSettingsScreenModel) {
    val readerTheme by screenModel.preferences.readerTheme().collectAsState()

    val flashPageState by screenModel.preferences.flashOnPageChange().collectAsState()

    val flashMillisPref = screenModel.preferences.flashDurationMillis()
    val flashMillis by flashMillisPref.collectAsState()

    val flashIntervalPref = screenModel.preferences.flashPageInterval()
    val flashInterval by flashIntervalPref.collectAsState()

    val flashColorPref = screenModel.preferences.flashColor()
    val flashColor by flashColorPref.collectAsState()

    SettingsChipRow(MR.strings.pref_reader_theme) {
        themes.map { (labelRes, value) ->
            FilterChip(
                selected = readerTheme == value,
                onClick = { screenModel.preferences.readerTheme().set(value) },
                label = { Text(stringResource(labelRes)) },
            )
        }
    }

    CheckboxItem(
        label = stringResource(MR.strings.pref_show_page_number),
        pref = screenModel.preferences.showPageNumber(),
    )

    // SY -->
    val forceHorizontalSeekbar by screenModel.preferences.forceHorizontalSeekbar().collectAsState()
    CheckboxItem(
        label = stringResource(SYMR.strings.pref_force_horz_seekbar),
        pref = screenModel.preferences.forceHorizontalSeekbar(),
    )

    if (!forceHorizontalSeekbar) {
        CheckboxItem(
            label = stringResource(SYMR.strings.pref_show_vert_seekbar_landscape),
            pref = screenModel.preferences.landscapeVerticalSeekbar(),
        )

        CheckboxItem(
            label = stringResource(SYMR.strings.pref_left_handed_vertical_seekbar),
            pref = screenModel.preferences.leftVerticalSeekbar(),
        )
    }
    // SY <--

    CheckboxItem(
        label = stringResource(MR.strings.pref_fullscreen),
        pref = screenModel.preferences.fullscreen(),
    )

    val isFullscreen by screenModel.preferences.fullscreen().collectAsState()
    if (LocalActivity.current?.hasDisplayCutout() == true && isFullscreen) {
        CheckboxItem(
            label = stringResource(MR.strings.pref_cutout_short),
            pref = screenModel.preferences.drawUnderCutout(),
        )
    }

    CheckboxItem(
        label = stringResource(MR.strings.pref_keep_screen_on),
        pref = screenModel.preferences.keepScreenOn(),
    )

    CheckboxItem(
        label = stringResource(MR.strings.pref_read_with_long_tap),
        pref = screenModel.preferences.readWithLongTap(),
    )

    CheckboxItem(
        label = stringResource(MR.strings.pref_always_show_chapter_transition),
        pref = screenModel.preferences.alwaysShowChapterTransition(),
    )

    // SY -->
    /*CheckboxItem(
        label = stringResource(MR.strings.pref_page_transitions),
        pref = screenModel.preferences.pageTransitions(),
    ) SY <-- */

    CheckboxItem(
        label = stringResource(MR.strings.pref_flash_page),
        pref = screenModel.preferences.flashOnPageChange(),
    )

    if (flashPageState) {
        SliderItem(
            value = flashMillis / ReaderPreferences.MILLI_CONVERSION,
            valueRange = 1..15,
            label = stringResource(MR.strings.pref_flash_duration),
            valueString = stringResource(MR.strings.pref_flash_duration_summary, flashMillis),
            onChange = { flashMillisPref.set(it * ReaderPreferences.MILLI_CONVERSION) },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        SliderItem(
            value = flashInterval,
            valueRange = 1..10,
            label = stringResource(MR.strings.pref_flash_page_interval),
            valueString = pluralStringResource(MR.plurals.pref_pages, flashInterval, flashInterval),
            onChange = {
                flashIntervalPref.set(it)
            },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        SettingsChipRow(MR.strings.pref_flash_with) {
            flashColors.map { (labelRes, value) ->
                FilterChip(
                    selected = flashColor == value,
                    onClick = { flashColorPref.set(value) },
                    label = { Text(stringResource(labelRes)) },
                )
            }
        }
    }

    // SY -->
    CheckboxItem(
        label = stringResource(SYMR.strings.auto_webtoon_mode),
        pref = screenModel.preferences.useAutoWebtoon(),
    )
    // SY <--

    // KMK --> Gallery and Scale Mode Settings
    val galleryPosition by screenModel.preferences.galleryPosition().collectAsState()
    val galleryThumbnailSize by screenModel.preferences.galleryThumbnailSize().collectAsState()
    val galleryAutoHideDelay by screenModel.preferences.galleryAutoHideDelay().collectAsState()
    val useThumbnailStrip by screenModel.preferences.useThumbnailStripForNavigation().collectAsState()
    val defaultScaleMode by screenModel.preferences.defaultScaleMode().collectAsState()

    // Gallery Position
    SettingsChipRow(KMR.strings.gallery_position_title) {
        galleryPositions.map { (labelRes, value) ->
            FilterChip(
                selected = galleryPosition == value,
                onClick = { screenModel.preferences.galleryPosition().set(value) },
                label = { Text(stringResource(labelRes)) },
            )
        }
    }

    // Gallery Thumbnail Size
    SettingsChipRow(KMR.strings.gallery_thumbnail_size_title) {
        galleryThumbnailSizes.map { (labelRes, value) ->
            FilterChip(
                selected = galleryThumbnailSize == value,
                onClick = { screenModel.preferences.galleryThumbnailSize().set(value) },
                label = { Text(stringResource(labelRes)) },
            )
        }
    }

    // Gallery Auto-hide Delay
    SliderItem(
        value = galleryAutoHideDelay,
        valueRange = 1..30,
        label = stringResource(KMR.strings.gallery_auto_hide_delay_title),
        valueString = if (galleryAutoHideDelay >= 30) stringResource(KMR.strings.never) else pluralStringResource(
            MR.plurals.pref_pages,
            galleryAutoHideDelay,
            galleryAutoHideDelay,
        ),
        onChange = { screenModel.preferences.galleryAutoHideDelay().set(it) },
        pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    )

    // Use Thumbnail Strip for Navigation
    CheckboxItem(
        label = stringResource(KMR.strings.use_thumbnail_strip_nav_title),
        pref = screenModel.preferences.useThumbnailStripForNavigation(),
    )

    // Default Scale Mode
    SettingsChipRow(KMR.strings.default_scale_mode_title) {
        scaleModes.map { (labelRes, value) ->
            FilterChip(
                selected = defaultScaleMode == value,
                onClick = { screenModel.preferences.defaultScaleMode().set(value) },
                label = { Text(stringResource(labelRes)) },
            )
        }
    }
    // KMK <--
}
