# Komikku 2026 - Proposed Changes Review

## 📋 Overview
**Total Proposed Changes:** 5 major features + 3 quick wins
**Estimated Time:** 2-3 weeks
**Priority:** High to Low

---

## 🎯 TIER 1: High Impact (Implement First)

### 1. Settings UI for Existing Features
**Status:** Preferences exist, UI missing
**Files to Create/Modify:**
```
app/src/main/java/eu/kanade/presentation/reader/settings/
  - GallerySettingsPage.kt (NEW)
  - ScaleModeSettingsPage.kt (NEW)
  
app/src/main/java/eu/kanade/presentation/more/settings/screen/
  - SettingsReaderScreen.kt (MODIFY - add sections)
```

**Settings to Add:**
| Setting | Type | Default |
|---------|------|---------|
| Gallery Position | Dropdown | BOTTOM |
| Gallery Thumbnail Size | Chips | MEDIUM |
| Gallery Auto-hide Delay | Slider | 3s |
| Use Thumbnail for Navigation | Toggle | true |
| Default Scale Mode | Dropdown | FIT_SCREEN |
| Remember Scale Per Manga | Toggle | true |

**Lines of Code:** ~300
**Time:** 1-2 days

---

### 2. Author/Artist Search - Prefix Syntax
**Status:** New feature
**Files to Modify:**
```
app/src/main/java/eu/kanade/tachiyomi/ui/library/LibraryScreenModel.kt
  - filterLibrary() - add prefix parsing (~50 lines)
  - filterManga() - handle author-only search (~30 lines)
```

**Syntax:**
- `author:oda` → search author field only
- `artist:takeuchi` → search artist field only
- `a:oda` → search both author and artist

**Example Results:**
```
Query: "author:oda"
✓ One Piece (Eiichiro Oda)
✓ Oda Nobuna no Yabou (different author)
✗ Oda Cinnamon Nobunaga (title match only - excluded)
```

**Lines of Code:** ~80
**Time:** 1 day

---

### 3. Search History
**Status:** New feature
**Files to Create:**
```
data/src/main/sqldelight/tachiyomi/data/search_history.sq (NEW)
app/src/main/java/eu/kanade/domain/search/SearchHistoryRepository.kt (NEW)
app/src/main/java/eu/kanade/presentation/search/SearchHistoryDropdown.kt (NEW)
```

**Files to Modify:**
```
app/src/main/java/eu/kanade/presentation/browse/GlobalSearchScreen.kt
  - Add history dropdown
  
app/src/main/java/eu/kanade/tachiyomi/ui/browse/source/globalsearch/SearchScreenModel.kt
  - Save searches
  - Load history
```

**Features:**
- Save last 20 searches
- Tap to re-run
- Swipe to delete
- Show timestamp ("2 hours ago")

**Database:**
```sql
CREATE TABLE search_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    query TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    result_count INTEGER
);
```

**Lines of Code:** ~400
**Time:** 2 days

---

## 🎯 TIER 2: Medium Impact

### 4. AI Recommendations Visual Differentiation
**Status:** Needs UI polish
**Files to Modify:**
```
app/src/main/java/eu/kanade/presentation/browse/recommendations/
  - RecommendationsScreenContent.kt (MODIFY)
  - RecommendationCard.kt (NEW - or modify existing)
```

**Visual Changes:**
```
Before:
┌─────────────┐
│  Cover      │
│             │
├─────────────┤
│ Title       │
│ Rating: 4.5 │
└─────────────┘

After:
┌─────────────┐
│  Cover      │
│  🤖 For You │  ← AI badge
├─────────────┤
│ Title       │
│ ⭐ 92% match │  ← Confidence
│ Because you │  ← Explanation
│ liked X     │
└─────────────┘
```

**Features:**
- "For You" badge on AI recommendations
- Confidence score (70-95% match)
- "Because you liked [Manga]" explanation
- Hybrid score breakdown (collaborative vs content)

**Lines of Code:** ~200
**Time:** 1-2 days

---

### 5. Thumbnail Strip as Slider Replacement
**Status:** Setting exists, integration incomplete
**Files to Modify:**
```
app/src/main/java/eu/kanade/presentation/reader/appbars/ReaderAppBars.kt
  - Conditionally show ThumbnailStrip instead of ChapterNavigator
  
app/src/main/java/eu/kanade/tachiyomi/ui/reader/ReaderActivity.kt
  - Pass gallery position setting to ThumbnailStrip
```

**Behavior:**
- When `useThumbnailStripForNavigation() = true`:
  - Hide ChapterNavigator (slider)
  - Show ThumbnailStrip at configured position
  - Persistent (not auto-hide)
- When false: Original behavior

**Lines of Code:** ~100
**Time:** 1 day

---

## 🎯 TIER 3: Nice to Have

### 6. Reading Session Timer
**Status:** New feature
**Files to Create:**
```
app/src/main/java/eu/kanade/domain/readingstats/
  - ReadingSessionRepository.kt
  - ReadingStats.kt
  
app/src/main/java/eu/kanade/presentation/reader/stats/
  - ReadingTimerOverlay.kt
```

**Features:**
- Track daily reading time
- Set goals (30 min, 1 hour, etc.)
- Show streak ("7 day streak!")
- Gentle notification on goal completion

**Lines of Code:** ~500
**Time:** 2-3 days

---

### 7. Smart Brightness Enhancement
**Status:** Enhancement to existing
**Files to Modify:**
```
app/src/main/java/eu/kanade/tachiyomi/ui/reader/setting/ReaderPreferences.kt
  - Add autoBrightnessEnabled()
  - Add brightnessPerManga()
  
app/src/main/java/eu/kanade/tachiyomi/ui/reader/ReaderActivity.kt
  - Auto-adjust based on time
  - Save per-manga brightness
```

**Features:**
- Time-based auto-adjustment (brighter day, dimmer night)
- Per-manga brightness memory
- Smooth transition animation

**Lines of Code:** ~150
**Time:** 1 day

---

## 📊 Summary Table

| # | Feature | New Files | Modified Files | LOC | Days | Priority |
|---|---------|-----------|----------------|-----|------|----------|
| 1 | Settings UI | 2 | 1 | 300 | 1-2 | 🔥🔥🔥 |
| 2 | Author Prefix Search | 0 | 1 | 80 | 1 | 🔥🔥🔥 |
| 3 | Search History | 3 | 2 | 400 | 2 | 🔥🔥🔥 |
| 4 | AI Recs Visual | 1 | 1 | 200 | 1-2 | 🔥🔥 |
| 5 | Thumbnail Slider | 0 | 2 | 100 | 1 | 🔥🔥 |
| 6 | Reading Timer | 3 | 2 | 500 | 2-3 | 🔥 |
| 7 | Smart Brightness | 0 | 2 | 150 | 1 | 🔥 |

**Total:** ~1,730 lines, 9-13 days

---

## 🚀 Recommended Order

### Week 1: Foundation
1. **Settings UI** - Makes all other features configurable
2. **Author Prefix Search** - Immediate user value
3. **Search History** - Daily UX improvement

### Week 2: Polish
4. **AI Recs Visual** - Makes AI features discoverable
5. **Thumbnail Slider** - Completes gallery feature

### Week 3: Nice-to-Have
6. **Reading Timer** - Gamification
7. **Smart Brightness** - Convenience

---

## ⚡ Quick Wins (Can Do Today)

### A. Author Prefix Search (30 min)
```kotlin
// Add to LibraryScreenModel.kt filterLibrary()
when {
    query.startsWith("author:") -> searchByAuthorOnly(query)
    query.startsWith("artist:") -> searchByArtistOnly(query)
    query.startsWith("a:") -> searchByAuthorOrArtist(query)
    else -> normalSearch(query)
}
```

### B. Gallery Settings UI (2 hours)
- Add dropdowns to existing settings page
- No new screens needed

### C. Search History - Preference Version (1 hour)
- Use stringSet preference instead of database
- Show last 10 searches
- No migration needed

---

## ❓ Questions for You

1. **Priority:** Which tier should I start with?
2. **Author Search:** Prefix syntax (`author:oda`) or dedicated author field in search bar?
3. **Search History:** Full database table or simple preference storage?
4. **Settings UI:** New dedicated pages or add to existing GeneralSettingsPage?
5. **Timeline:** Want quick wins first or complete features?

---

## 📝 Files Ready to Modify

All analysis complete. Ready to implement on your approval.

**Next Step:** Pick a feature and I'll start coding.