# Major Search Improvements - Analysis & Recommendations

## Current Search State

### What Exists:
1. **Global Search** - Search across all sources simultaneously
2. **Smart Library Search** - Fuzzy matching for library manga
3. **SmartMangaSearch** (our addition) - Natural language query parsing
4. **Source-specific filters** - Genre, status, etc. per source
5. **Bulk selection** - Multi-select search results

### What's Missing (Major Gaps):

---

## 🚀 Major Improvement #1: Universal Search Bar

**Current:** Separate search for library vs browse vs history
**Problem:** Users must know WHERE to search first

**Solution:** Single search bar that searches EVERYTHING:
- Library manga (local)
- Browse sources (remote)
- Reading history
- Downloaded chapters
- Bookmarks/notes

**UI:**
```
┌─────────────────────────────────────┐
│ 🔍 Search everything...             │
└─────────────────────────────────────┘
Results:
📚 From Library (3)
📖 From History (2)  
🌐 From Sources (15)
💾 Downloaded (1)
```

---

## 🚀 Major Improvement #2: Search Filters Panel

**Current:** Each source has different filters, no unified UI
**Problem:** Inconsistent experience, can't combine filters across sources

**Solution:** Universal filter panel that works across sources:

| Filter | Options |
|--------|---------|
| **Status** | Any, Ongoing, Completed, Hiatus |
| **Content Rating** | Any, Safe, Suggestive, NSFW |
| **Chapter Count** | Min/Max slider |
| **Last Updated** | Today, This week, This month, Any |
| **Source** | All, Pinned, Specific list |
| **Genre** | Multi-select with AND/OR logic |

**Advanced:**
- Exclude genres (NOT romance)
- Require genres (MUST have action)
- Year range
- Author/artist search

---

## 🚀 Major Improvement #3: Search History & Suggestions

**Current:** No search history persistence
**Problem:** Repeat searches, no discovery

**Solution:**
```
Search Bar Focused:
┌─────────────────────────────────────┐
│ 🔍 Type to search...                │
└─────────────────────────────────────┘

Recent Searches:
  action romance
  isekai with op mc
  ⏱️ 2 hours ago

Trending Searches:
  🔥 solo leveling
  🔥 chainsaw man
  🔥 blue lock

Suggested For You:
  💡 "sci-fi thriller" (based on history)
  💡 "completed romance" (trending in your genres)
```

---

## 🚀 Major Improvement #4: Smart Search Results Ranking

**Current:** Results shown source-by-source, no ranking
**Problem:** Best matches buried, duplicates across sources

**Solution:** Intelligent result aggregation:

```
Search: "solo leveling"

Ranked Results:
1. ⭐ Solo Leveling [Source A] - Exact match, high rating
2. ⭐ Solo Leveling [Source B] - Exact match  
3. Solo Leveling: Ragnarok [Source A] - Related
4. The Lone Necromancer [Source C] - Similar (isekai, leveling)
5. Omniscient Reader [Source D] - Similar (system, action)

Filters: Hide duplicates | Sort by: Relevance ⬇️
```

**Ranking Factors:**
- Title match quality (exact > contains > similar)
- User rating/popularity
- Source reliability
- Already in library (deprioritize)
- Reading history similarity

---

## 🚀 Major Improvement #5: Voice Search & Image Search

**Voice Search:**
- Tap microphone icon
- Speak: "Find me action manga with female lead"
- Converts to SmartMangaSearch query

**Image Search (Reverse):**
- Upload/screenshot manga page
- Find source manga
- "Where is this from?"

---

## 🚀 Major Improvement #6: Saved Searches & Alerts

**Saved Searches:**
```
My Saved Searches:
📌 "isekai romance completed" - 12 new results
📌 "action 100+ chapters" - 3 new results  
📌 "new releases this week" - Updated 2h ago
```

**Alerts:**
- "Notify me when new manga matches 'psychological thriller'"
- Weekly digest of new matches
- Push notification for exact title matches

---

## 🚀 Major Improvement #7: Search Within Manga

**Current:** Can't search inside manga/chapters
**Use Cases:**
- "Find chapter where X character first appears"
- "Search for 'reunion' in all chapters"
- "Find page with specific dialogue"

**Implementation:**
- OCR on downloaded pages (offline)
- Server-side search for popular manga
- Chapter title search

---

## Implementation Priority

| Rank | Feature | Impact | Effort | Notes |
|------|---------|--------|--------|-------|
| 1 | **Universal Search Bar** | 🔥🔥🔥 | Medium | Unifies all search entry points |
| 2 | **Smart Results Ranking** | 🔥🔥🔥 | High | Requires result aggregation logic |
| 3 | **Search History & Suggestions** | 🔥🔥 | Low | Database + UI only |
| 4 | **Universal Filter Panel** | 🔥🔥 | High | Complex filter mapping |
| 5 | **Saved Searches** | 🔥🔥 | Medium | Database + scheduling |
| 6 | **Voice Search** | 🔥 | Low | Uses existing SmartMangaSearch |
| 7 | **Image Search** | 🔥 | Very High | OCR/ML infrastructure |
| 8 | **Search Within Manga** | 🔥 | Very High | OCR + indexing |

---

## Recommended Implementation Order

### Phase 1: Foundation (2-3 days)
1. **Search History Database**
   - Table: `search_history(id, query, timestamp, result_count)`
   - UI: Recent searches dropdown

2. **Universal Search Entry**
   - Single search bar on main screen
   - Routes to appropriate screen based on result type

### Phase 2: Intelligence (3-5 days)
3. **Results Ranking**
   - Deduplicate across sources
   - Relevance scoring
   - "Best match" badge

4. **Smart Suggestions**
   - Based on reading history
   - Trending in your genres

### Phase 3: Power Features (5-7 days)
5. **Universal Filters**
   - Common filter UI
   - Map to source-specific filters

6. **Saved Searches**
   - Save/notify on new results
   - Background checking

---

## Files to Create/Modify

### New Files:
```
app/src/main/java/eu/kanade/domain/search/
  - SearchHistoryRepository.kt
  - SearchRanker.kt
  - UniversalSearchInteractor.kt

app/src/main/java/eu/kanade/presentation/search/
  - UniversalSearchBar.kt
  - SearchHistoryDropdown.kt
  - SearchFilterPanel.kt
  - SearchResultsRanked.kt

data/src/main/sqldelight/tachiyomi/data/
  - search_history.sq
```

### Modified Files:
```
app/src/main/java/eu/kanade/tachiyomi/ui/browse/source/globalsearch/SearchScreenModel.kt
app/src/main/java/eu/kanade/presentation/browse/GlobalSearchScreen.kt
app/src/main/java/eu/kanade/presentation/library/LibraryScreen.kt
app/src/main/java/eu/kanade/tachiyomi/ui/main/MainActivity.kt (add universal search)
```

---

## Quick Win: Search History (1 day)

Can implement immediately:
```kotlin
// Preference-based (no DB migration needed)
fun searchHistory() = preferenceStore.getStringSet("search_history", emptySet())

// Show last 10 searches
// Tap to re-run
// Swipe to delete
```

This alone would be a major UX improvement.