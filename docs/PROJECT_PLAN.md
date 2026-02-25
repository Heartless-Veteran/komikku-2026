# Komikku 2026 - Project Plan

## Overview
Enhanced manga reader forked from Komikku with Perfect Viewer-inspired features, AI recommendations, and modern UI enhancements.

## Completed Phases

### ✅ Phase 1: Perfect Viewer Reading Modes
**Status:** Merged to master

**Features:**
- 5 scale modes: FIT_SCREEN, FIT_WIDTH, FIT_HEIGHT, ORIGINAL_SIZE, SMART_FIT
- Per-manga scale mode persistence (viewerFlags bits 6-8)
- UI toggle in reader toolbar
- Smooth scale transitions

**Files:**
- `ScaleMode.kt` - Enum with SSIV integration
- `ReaderViewModel.kt` - Per-manga state management
- `ReaderActivity.kt` - UI integration

---

### ✅ Phase 2: AI Recommendations
**Status:** Merged to master

**Features:**
- Tag-based recommendation algorithm (70% genre match + 30% popularity)
- "For You" tab in Browse
- "Because you read X" section
- Reading history tracking
- Weekly background updates via WorkManager

**Files:**
- `RecommendationsRepositoryImpl.kt`
- `RecommendationsScreenContent.kt`
- `UpdateRecommendationsWorker.kt`
- SQLDelight schema for reading_history, manga_tags

---

### ✅ Phase 3: Gallery View
**Status:** Merged to master

**Features:**
- Perfect Viewer-style thumbnail strip
- Tap Gallery button to show/hide
- Horizontal scroll through pages
- Tap thumbnail to jump to page
- Page number overlays
- Auto-hide after selection

**Files:**
- `ThumbnailStrip.kt` - Composable implementation
- `ReaderViewModel.kt` - Visibility state
- `ReaderBottomBar.kt` - Gallery button

---

## In Progress

### 🔄 Phase 4: Dynamic Theming
**Status:** Spec complete, implementation pending

**Planned Features:**
- Extract dominant/vibrant colors from manga covers
- Apply colors to app bars, navigation, accents
- Theme modes: Vibrant, Dominant, Muted, Dark
- Optional reader background tinting
- Cache colors in database

**Spec:** `docs/specs/PHASE4_DYNAMIC_THEMING.md`

---

## Technical Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Database:** SQLDelight
- **Images:** Coil
- **DI:** Koin
- **Background:** WorkManager

## Repository Structure
```
komikku-2026/
├── app/                    # Main application
├── docs/                   # Documentation
│   ├── specs/             # Feature specifications
│   └── FEATURE_*.md       # Feature docs
├── i18n/                  # Internationalization
└── .github/               # CI/CD workflows
```

## Completed Issues
- #4 - Feature: Perfect Viewer Reading Modes
- #5 - Phase 1: Perfect Viewer Reading Modes
- #6 - Phase 2: AI Recommendations
- #7 - Phase 3: Gallery View

## Open Issues
- #8 - Phase 4: Dynamic Theming
- #15 - Dependency Dashboard (Renovate)

## Version History
- **0.1.0-alpha** (2026-02-24) - Initial fork
- **0.2.0-alpha** (2026-02-25) - Phase 1-3 complete

## Credits
- [Komikku](https://github.com/komikku-app/komikku) - Upstream project
- Perfect Viewer - Reading mode inspiration
- All upstream contributors