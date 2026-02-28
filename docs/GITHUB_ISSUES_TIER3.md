# GitHub Issues for Tier 3 Features

## Issue #1: Search Suggestions & Autocomplete

**Title:** [Feature Request] Search Suggestions and Autocomplete

**Description:**
Implement search suggestions and autocomplete functionality to improve the search experience.

**Requirements:**
- [ ] Show search suggestions as user types (debounced 300ms)
- [ ] Suggest based on:
  - Recent searches
  - Popular/trending searches
  - Manga titles from library
  - Author/artist names
- [ ] Keyboard navigation (arrow keys + enter to select)
- [ ] Clear button for each suggestion
- [ ] Highlight matching text in suggestions

**UI Mockup:**
```
┌─────────────────────────────────────┐
│ 🔍 action ro│                       │
└─────────────────────────────────────┘
Suggestions:
  💡 action romance
  💡 action romance with op mc
  📚 Action Romance (Library)
  👤 Author: Action
```

**Files to Modify:**
- `GlobalSearchToolbar.kt` - Add suggestion dropdown
- `SearchScreenModel.kt` - Add suggestion logic
- `SearchHistoryRepository.kt` - Add title/author suggestions

**Estimated Effort:** 2-3 days
**Priority:** High

---

## Issue #2: Universal Search Bar

**Title:** [Feature Request] Universal Search Bar

**Description:**
Create a single search entry point that searches across library, history, and browse sources simultaneously.

**Requirements:**
- [ ] Single search bar accessible from main screen
- [ ] Results grouped by source:
  - Library results (top priority)
  - Reading history matches
  - Browse source results
- [ ] Quick filters (Library only, Sources only, All)
- [ ] Unified result ranking
- [ ] Show source badge on each result

**UI Mockup:**
```
┌─────────────────────────────────────┐
│ 🔍 Search everything...             │
└─────────────────────────────────────┘

📚 Library (3)
  [Cover] Title 1
  [Cover] Title 2

📖 History (2)
  [Cover] Previously read

🌐 Sources (15)
  [Cover] Title from Source A
  [Cover] Title from Source B
```

**Files to Create/Modify:**
- `UniversalSearchScreen.kt` (new)
- `UniversalSearchViewModel.kt` (new)
- `MainActivity.kt` - Add search FAB
- Navigation graph updates

**Estimated Effort:** 3-4 days
**Priority:** High

---

## Issue #3: Smart Results Ranking

**Title:** [Feature Request] Smart Search Results Ranking

**Description:**
Implement intelligent ranking for search results to show most relevant matches first.

**Requirements:**
- [ ] Deduplicate results across sources (same manga from multiple sources)
- [ ] Ranking factors:
  - Title match quality (exact > contains > similar)
  - User rating/popularity
  - Source reliability
  - Already in library (deprioritize)
  - Reading history similarity
- [ ] Show "Best match" badge on top result
- [ ] Sort options: Relevance, Rating, Newest
- [ ] Confidence score for each result

**Algorithm:**
```kotlin
score = (
    titleMatchWeight * titleScore +
    ratingWeight * normalizedRating +
    sourceReliabilityWeight * sourceScore +
    historyWeight * historySimilarity
) / totalWeight
```

**Files to Modify:**
- `SearchScreenModel.kt` - Add ranking logic
- `GlobalSearchScreen.kt` - Add sort options
- `SearchResultItem.kt` - Add badges

**Estimated Effort:** 2-3 days
**Priority:** Medium

---

## Issue #4: Search Within Manga (OCR)

**Title:** [Feature Request] Search Within Manga Pages (OCR)

**Description:**
Allow users to search for text within downloaded manga pages using OCR.

**Requirements:**
- [ ] OCR processing for downloaded pages
- [ ] Index text per manga
- [ ] Search dialog in manga details
- [ ] Results show page thumbnails with highlights
- [ ] Offline capability

**Technical Approach:**
- Use ML Kit Text Recognition or Tesseract OCR
- Background processing with WorkManager
- SQLite FTS for text indexing

**Files to Create:**
- `MangaTextSearchRepository.kt`
- `OcrProcessor.kt`
- `PageTextIndexWorker.kt`
- `MangaSearchScreen.kt`

**Estimated Effort:** 5-7 days
**Priority:** Low (complex)

---

## Issue #5: Voice Search

**Title:** [Feature Request] Voice Search

**Description:**
Add voice input capability to search.

**Requirements:**
- [ ] Microphone button in search bar
- [ ] Speech-to-text conversion
- [ ] Handle voice search intent
- [ ] Fallback to keyboard if voice fails

**Implementation:**
- Use Android SpeechRecognizer API
- Integrate with existing SmartMangaSearch

**Estimated Effort:** 1 day
**Priority:** Low

---

## Issue #6: Saved Searches with Alerts

**Title:** [Feature Request] Saved Searches with New Result Alerts

**Description:**
Allow users to save searches and get notified when new manga matches.

**Requirements:**
- [ ] Save search button in search results
- [ ] List of saved searches in settings
- [ ] Background check for new matches (weekly)
- [ ] Push notification for new results
- [ ] Quick "view new results" action

**Files to Create:**
- `SavedSearchRepository.kt`
- `SavedSearchWorker.kt`
- `SavedSearchesScreen.kt`

**Estimated Effort:** 2-3 days
**Priority:** Medium

---

## Summary for Copilot

**Recommended Order:**
1. Issue #1: Search Suggestions (2-3 days) - High impact, daily UX
2. Issue #2: Universal Search (3-4 days) - Major UX improvement
3. Issue #3: Smart Ranking (2-3 days) - Polish existing search
4. Issue #6: Saved Searches (2-3 days) - Power user feature
5. Issue #5: Voice Search (1 day) - Quick win
6. Issue #4: OCR Search (5-7 days) - Complex, defer

**Total Estimated Time:** 15-21 days for all Tier 3 features
