# 📋 Changelog

All notable changes to Komikku 2026 will be documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- Perfect Viewer–style reading scale modes (Fit Screen, Fit Width, Fit Height, Original Size, Smart Crop)
- `ScaleMode` enum with `toSubsamplingScaleType()` conversion helper
- Scale mode toggle button in the reader toolbar
- `scaleMode` preference in `ReaderPreferences`
- AI-powered recommendation system: genre-based matching, "For You" tab, "Because you read" suggestions
- Reading history tracker with weekly background refresh
- Cached recommendations for offline access
- Gallery view: full chapter thumbnail grid with direct page navigation
- Dynamic theming: accent colours extracted from manga cover via Android Palette API

### Changed
- App rebranded to **Komikku 2026** (`app_name` updated in strings.xml)
- `README.md` completely rewritten with feature overview and build instructions
- Documentation reorganised into `docs/` folder
- Improved CONTRIBUTING.md with project-specific guidance

### Security
- 20 security vulnerabilities inherited from upstream base repository identified
- Action plan documented in `docs/SECURITY_VULNERABILITIES.md`

---

## [0.1.0-alpha] — 2026-02-24

### Added
- Initial fork of [Komikku](https://github.com/komikku-app/komikku)
- Project roadmap (`docs/PROJECT_PLAN.md`)
- Feature specification documents in `docs/`
- CI workflow for debug builds (`.github/workflows/build-debug.yml`)

---

## Credits

- [Komikku](https://github.com/komikku-app/komikku) — upstream project
- Perfect Viewer — inspiration for reading modes
- All upstream contributors and translators
