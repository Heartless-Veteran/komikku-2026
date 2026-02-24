## Feature: Dynamic Theming

### Overview
Extract colors from manga cover and apply to reader UI.

### Implementation
```kotlin
// Use Android Palette API
fun extractColors(coverBitmap: Bitmap): ColorScheme {
    val palette = Palette.from(coverBitmap).generate()
    
    return ColorScheme(
        primary = palette.getVibrantColor(defaultPrimary),
        secondary = palette.getMutedColor(defaultSecondary),
        background = palette.getDarkMutedColor(defaultBackground),
        surface = palette.getLightMutedColor(defaultSurface)
    )
}
```

### UI Elements to Theme
- Reader background
- Toolbar background
- Progress indicator
- FAB buttons
- Status bar

### Implementation
- Extract on manga load
- Cache colors in database
- Apply via MaterialTheme
- Fallback to default dark/light theme

### Settings
- Toggle: "Match theme to cover"
- Default: enabled