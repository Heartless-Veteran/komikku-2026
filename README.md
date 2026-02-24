# Komikku 2026 - Enhanced Manga Reader

A fork of [Komikku](https://github.com/komikku-app/komikku) with enhanced reading features.

## New Features

### Perfect Viewer Reading Modes
- **Fit Screen** - Fit entire page to screen (default)
- **Fit Width** - Fit page width to screen width
- **Fit Height** - Fit page height to screen height  
- **Original Size** - No scaling, original resolution
- **Smart Crop** - Auto-crop white/black margins

### AI Recommendations
- Personalized manga suggestions based on reading history
- Similar manga finder
- Tag-based matching algorithm

### Gallery View
- Grid view of all pages in chapter
- Quick navigation to any page
- Thumbnail previews

### Dynamic Theming
- Theme colors extracted from manga cover
- Applies to reader background and UI

## Original Komikku Features
- Extension system for manga sources
- Local manga reading
- Library management
- Download for offline reading
- And more...

## Tech Stack
- Kotlin
- Jetpack Compose
- SQLDelight
- Coil (image loading)

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## Project Structure
- `app/` - Main Android app
- `docs/` - Feature documentation
- See `PROJECT_PLAN.md` for roadmap

## Credits
- Original [Komikku](https://github.com/komikku-app/komikku) by the Komikku team
- Perfect Viewer for inspiration on reading modes

## License
Same as original Komikku (Apache 2.0)
