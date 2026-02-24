# Perfect Viewer Scale Modes Implementation

## Summary

This implementation adds Perfect Viewer-style scale modes to the Komikku 2026 manga reader app. Users can now cycle through different scaling options to customize how manga pages are displayed.

## Changes Made

### 1. ScaleMode Enum (`app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/ScaleMode.kt`)
- Added `toSubsamplingScaleType()` method to convert ScaleMode to SubsamplingScaleImageView scale types
- Added `entriesList` for easy access to all scale modes

### 2. PagerConfig (`app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/pager/PagerConfig.kt`)
- Added import for `ScaleMode`
- Added `getScaleType()` method that returns the appropriate SubsamplingScaleImageView scale type based on current ScaleMode setting

### 3. PagerPageHolder (`app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/pager/PagerPageHolder.kt`)
- Updated `setImage()` call to use `viewer.config.getScaleType()` instead of `viewer.config.imageScaleType`

### 4. ReaderBottomButton (`app/src/main/java/eu/kanade/tachiyomi/ui/reader/setting/ReaderBottomButton.kt`)
- Added `ScaleMode` button entry to the enum

### 5. ReaderBottomBar (`app/src/main/java/eu/kanade/presentation/reader/appbars/ReaderBottomBar.kt`)
- Added import for `ScaleMode` and `Icons.Outlined.AspectRatio`
- Added `scaleMode` and `onClickScaleMode` parameters
- Added ScaleMode toggle button that shows the current scale mode

### 6. ReaderAppBars (`app/src/main/java/eu/kanade/presentation/reader/appbars/ReaderAppBars.kt`)
- Added import for `ScaleMode`
- Added `scaleMode` and `onClickScaleMode` parameters
- Passed parameters through to ReaderBottomBar

### 7. ReaderViewModel (`app/src/main/java/eu/kanade/tachiyomi/ui/reader/ReaderViewModel.kt`)
- Added import for `ScaleMode`
- Added `scaleMode` to State data class (defaults to `ScaleMode.FIT_SCREEN`)
- Added initialization of scale mode from preferences in `init` block
- Added `toggleScaleMode()` method that cycles through all 5 scale modes

### 8. ReaderActivity (`app/src/main/java/eu/kanade/tachiyomi/ui/reader/ReaderActivity.kt`)
- Added import for `ScaleMode`
- Added `scaleMode` and `onClickScaleMode` parameters to ReaderAppBars
- Implemented scale mode toggle with toast feedback showing the new mode name

### 9. String Resources (`i18n/src/commonMain/moko-resources/base/strings.xml`)
- Added `scale_type` string resource for the button content description

## Scale Modes Available

1. **FIT_SCREEN** (default) - Fits the entire page within screen bounds using CENTER_INSIDE
2. **FIT_WIDTH** - Fits page width to screen width, good for text-heavy manga
3. **FIT_HEIGHT** - Fits page height to screen height, good for tall pages
4. **ORIGINAL_SIZE** - Displays at original size without scaling, user must pan
5. **SMART_CROP** - Automatically crops white/black margins to maximize content

## How to Use

1. Open any manga chapter in the reader
2. Tap the screen to show the menu
3. Tap the scale mode button (aspect ratio icon) in the bottom toolbar
4. Each tap cycles to the next scale mode
5. A toast notification shows the current scale mode name

## Testing Checklist

- [ ] Verify each scale mode works correctly
  - [ ] FIT_SCREEN shows full page within screen
  - [ ] FIT_WIDTH scales to match screen width
  - [ ] FIT_HEIGHT scales to match screen height
  - [ ] ORIGINAL_SIZE shows at 1:1 pixel ratio
  - [ ] SMART_CROP crops margins appropriately
- [ ] Check rotation handling (portrait/landscape)
- [ ] Test with different manga page sizes (tall, wide, standard)
- [ ] Verify preference is remembered between sessions
- [ ] Verify button only appears when ScaleMode is enabled in bottom buttons

## Branch

`feature/pager-scale-modes`

## Commits

1. `5d2b347be` - Implement Perfect Viewer scale modes for pager viewer
2. `f8c0ad355` - Add scale_type string resource for scale mode button
