# 📋 Changelog

All notable changes to Komikku 2026 will be documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added
- `SECURITY.md` — security policy with private reporting instructions and response timeline
- `assets/` folder with design token and colour-scheme reference
- Issue templates, PR template, and `FUNDING.yml` updated to reference this fork

### Changed
- `README.md` completely rewritten with ASCII banner, shields.io badges, feature tables, and project structure tree
- `CONTRIBUTING.md` rewritten with project-specific guidance, dev setup steps, and PR checklist
- `CHANGELOG.md` converted to [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format
- `CODE_OF_CONDUCT.md` given a table of contents and section divider
- Documentation reorganised: `PROJECT_PLAN.md`, `SCALE_MODES_IMPLEMENTATION.md`, `TEST_SUMMARY.md`, `SECURITY_VULNERABILITIES.md` moved to `docs/`
- Spurious empty file `1` removed

---

## [0.1.0-alpha] — 2026-02-24

### Added
- Initial fork of [Komikku](https://github.com/komikku-app/komikku)
- Project roadmap (`docs/PROJECT_PLAN.md`)
- Feature specification documents in `docs/`
- CI workflow for debug builds (`.github/workflows/build-debug.yml`)
- App rebranded to **Komikku 2026** (`app_name` updated in strings.xml)
- `ScaleMode` enum with `toSubsamplingScaleType()` conversion helper (in progress)
- `scaleMode` preference in `ReaderPreferences` (in progress)
- AI-powered recommendation system scaffold (in progress)
- Reading history tracker (in progress)

---

## Credits

- [Komikku](https://github.com/komikku-app/komikku) — upstream project
- Perfect Viewer — inspiration for reading modes
- All upstream contributors and translators
