# Phase 4: Dynamic Theming from Cover Colors

## Overview
Extract dominant colors from manga covers and use them to theme the app UI dynamically.

## Features

### 1. Cover Color Extraction
- Extract dominant/vibrant colors from manga cover images
- Use Palette API or similar for color analysis
- Cache extracted colors to avoid re-processing

### 2. Dynamic Theme Application
- Apply cover colors to:
  - App bars (top/bottom)
  - Navigation elements
  - Accent colors (buttons, sliders)
  - Background tints
- Smooth color transitions when switching manga

### 3. Theme Modes
- **Vibrant**: Use most vibrant color from cover
- **Dominant**: Use most dominant color
- **Muted**: Use muted/background color
- **Dark**: Adapt for dark mode compatibility

### 4. Reader Theming
- Optional: Tint reader background to match cover
- Optional: Colored page indicators

## Technical Approach

### Color Extraction
```kotlin
// Using Android Palette API
val palette = Palette.from(bitmap).generate()
val vibrant = palette.vibrantSwatch?.rgb
val dominant = palette.dominantSwatch?.rgb
```

### Theme Application
```kotlin
// Dynamic color scheme
val dynamicColorScheme = rememberCoverColorScheme(coverColor)
MaterialTheme(colorScheme = dynamicColorScheme) {
    // App content
}
```

### Caching
- Store extracted colors in database (manga table)
- Update when cover changes
- Fall back to default theme if no color available

## Files to Modify
- `MangaCover.kt` - Add color extraction
- `Manga.kt` - Add color fields
- `Theme.kt` - Dynamic color scheme generation
- `MainActivity.kt` - Apply dynamic theme
- `ReaderActivity.kt` - Optional reader theming

## Settings
- Enable/disable dynamic theming
- Theme mode selection (vibrant/dominant/muted)
- Reader theming toggle

## References
- Material Design 3 dynamic color
- Android Palette API
- Coil transformations for color extraction