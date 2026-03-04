# Changelog

All notable changes to Komikku 2026 will be documented in this file.

## [Unreleased] - Alpha

### Added

#### Reader Features
- **Full Chapter Gallery Preview** — Full-screen grid gallery with 2/3/4 column layout, adjustable thumbnail sizes, and instant page jumping
- **Reading Time Estimation** — Learns your reading speed and displays estimated time remaining in chapter
- **Per-Series Reader Presets** — Per-manga settings for reading direction, scale mode, background color, brightness, and more

#### Library & Management
- **Source Health Dashboard** — Visual indicators for source reliability with success/failure tracking
- **Export Downloads to CBZ** — Export chapters or entire series as CBZ comic book archives
- **Reading Statistics Dashboard** — Track total chapters read, reading streaks, weekly activity, and top manga by time

#### Infrastructure
- GitHub Actions CI/CD workflows optimized for development
- Automated build and test pipeline

### Changed
- CI workflows temporarily disabled during heavy development, then re-enabled for alpha release
- Import cleanup in multiple modules

### Fixed
- Removed unused imports in `PerMangaReaderPreferences.kt`
- Removed unused imports in `ReadingTimeEstimator.kt`
- Navigation bar padding in gallery preview
- Unstable keys in gallery grid (now uses page URL)

---

## [0.1.0-alpha] - 2026-03-05

### Initial Release

Based on Komikku with the following 2026 enhancements:

- Perfect Viewer-style reading modes (Fit Screen, Fit Width, Fit Height, Original, Smart Fit)
- AI-powered recommendations with "For You" tab
- Dynamic theming with cover color extraction
- Gallery view thumbnail strip
- Scale mode persistence per manga

---

## Upstream (Komikku)

See [upstream changelog](https://github.com/komikku-app/komikku/blob/master/CHANGELOG.md) for base features including:

- Extension system with hundreds of sources
- Library management with categories
- Offline downloads
- Local manga support
- Reading history & bookmarks
- Webtoon & pager modes
- Tracking integration (MyAnimeList, AniList, etc.)
