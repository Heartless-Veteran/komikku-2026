# Komikku 2026 - Change Log

## Project Overview
Fork of Komikku manga reader with Perfect Viewer features, AI recommendations, Gallery view, and Dynamic theming.

**Repository:** https://github.com/Heartless-Veteran/komikku-2026
**Base:** Komikku (original)
**Version:** 0.1.0-alpha
**Started:** 2026-02-24

---

## Changes Made

### 1. Project Setup

#### Files Created
- `PROJECT_PLAN.md` - Project roadmap and feature phases
- `VERSION` - Version information and feature list
- `README.md` - Complete rewrite with our features
- `CHANGELOG.md` - This file

#### Documentation (`docs/`)
- `FEATURE_READING_MODES.md` - Perfect Viewer scale modes
- `FEATURE_RECOMMENDATIONS.md` - AI recommendation system
- `FEATURE_GALLERY_VIEW.md` - Gallery view specification
- `FEATURE_DYNAMIC_THEME.md` - Dynamic theming from covers

### 2. GitHub Setup

#### Workflows (`.github/workflows/`)
- `build-debug.yml` - CI for debug builds
- `ai-assistant.yml` - Webhook for @kimi mentions
- `build_pull_request.yml` - Original PR checks (kept)
- `build_release.yml` - Original release (kept)

#### Issues Created
| # | Title | Type | Status |
|---|-------|------|--------|
| 3 | Perfect Viewer Reading Modes | Feature | Open |
| 4 | AI Recommendations | Feature | Open |
| 5 | Gallery View | Feature | Open |
| 6 | Dynamic Theming | Feature | Open |
| 7 | Refactor large ViewModels | Improvement | Open |
| 8 | Extract hardcoded URLs | Improvement | Open |
| 9 | Address TODO/FIXME comments | Improvement | Open |
| 10 | Optimize ReaderActivity | Improvement | Open |

### 3. Code Changes

#### ScaleMode Infrastructure
**Files Modified:**
- `app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/ScaleMode.kt` (NEW)
  - Enum with 5 modes: FIT_SCREEN, FIT_WIDTH, FIT_HEIGHT, ORIGINAL_SIZE, SMART_CROP
  
- `app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/ViewerConfig.kt`
  - Added `scaleMode` property
  - Added preference listener for scale mode changes
  
- `app/src/main/java/eu/kanade/tachiyomi/ui/reader/setting/ReaderPreferences.kt`
  - Added `scaleMode()` preference

#### App Rebranding
- `i18n/src/commonMain/moko-resources/base/strings.xml`
  - Changed `app_name` from "Komikku" to "Komikku 2026"

#### Build Configuration
- `app/google-services.json` (NEW)
  - Dummy Firebase config for debug builds

### 4. Infrastructure

#### ngrok Tunnel
- URL: `https://toxically-shredless-winfred.ngrok-free.dev`
- Purpose: GitHub webhook receiver
- Status: Active

#### Webhook Server
- Port: 18790
- File: `webhook-server-v2.js`
- Purpose: Receive GitHub events and forward to OpenClaw

---

## Git Branches

| Branch | Purpose | Status |
|--------|---------|--------|
| `master` | Main branch with all changes | Active |
| `feature/perfect-viewer-modes` | Scale mode development | Merged to master |

---

## In Progress

### Subagent Tasks (2 hour runtime)
- Implementing PagerViewer scale mode logic
- Adding UI toggle in reader toolbar
- Testing each scale mode

---

## Next Steps

1. Complete scale mode implementation
2. Test all 5 scale modes
3. Merge to master when stable
4. Start Phase 2: AI Recommendations

---

## Credits

- Original Komikku: https://github.com/komikku-app/komikku
- Perfect Viewer: Inspiration for reading modes
- Development: Heartless-Veteran with Kimi AI assistance
## 2026-02-24 - Phase 2 Complete

### AI Recommendations (Phase 2) - MERGED
- ✅ Reading history tracking
- ✅ Genre-based recommendation algorithm  
- ✅ "For You" tab in Browse
- ✅ "Because you read" suggestions
- ✅ Weekly background updates
- ✅ Cached recommendations

### Security
- ⚠️ 20 vulnerabilities detected (inherited from base repo)
- 📋 Action plan created

### Master Branch Now Contains:
- Phase 1: Perfect Viewer Scale Modes
- Phase 2: AI Recommendations
- Refactoring: ReadingHistoryTracker
- Dependency management setup

