## Feature: Perfect Viewer Reading Modes

### Current Komikku Behavior
- Pager: Page-by-page swipe
- Webtoon: Vertical scroll
- Basic zoom with double-tap

### Perfect Viewer Features to Add

#### 1. Scaling Modes
```kotlin
enum class ScaleMode {
    FIT_SCREEN,      // Fit both width and height
    FIT_WIDTH,       // Fit to screen width
    FIT_HEIGHT,      // Fit to screen height
    ORIGINAL_SIZE,   // No scaling
    SMART_CROP       // Auto-crop margins
}
```

#### 2. Implementation Location
- File: `app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/pager/PagerViewer.kt`
- Config: `app/src/main/java/eu/kanade/tachiyomi/ui/reader/setting/ReaderPreferences.kt`

#### 3. UI Changes
- Add scale mode button to reader toolbar
- Add to reader settings menu
- Remember per-manga preference

#### 4. Smart Crop
- Detect white/black margins
- Auto-crop for cleaner reading
- Toggle in settings