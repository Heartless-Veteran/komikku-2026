# Komikku 2026 - Detailed Project Plan

## Project Overview

**Project Name:** Komikku 2026  
**Type:** Android Manga Reader App Enhancement  
**Base:** Fork of Komikku (Tachiyomi-based)  
**Language:** Kotlin  
**Framework:** Jetpack Compose  
**Architecture:** MVVM with Clean Architecture  

---

## Executive Summary

This project involves enhancing a manga reader application with advanced features including Perfect Viewer-style reading modes, AI-powered recommendations, gallery view navigation, dynamic theming, and comprehensive search capabilities. The project is structured in phases with clear deliverables and technical requirements.

---

## Phase 1: Perfect Viewer Scale Modes

### Objective
Implement 5 different image scaling modes for optimal reading experience.

### Technical Requirements

#### 1.1 Scale Mode Enum
```kotlin
enum class ScaleMode {
    FIT_SCREEN,      // Fit entire image within screen
    FIT_WIDTH,       // Fit image width to screen width
    FIT_HEIGHT,      // Fit image height to screen height
    ORIGINAL_SIZE,   // Display at original resolution
    SMART_FIT        // Intelligent fitting based on image aspect ratio
}
```

#### 1.2 Implementation Details
- **File:** `app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/ScaleMode.kt`
- Add scale mode to `ReaderPreferences` with default value
- Implement per-manga persistence using `viewerFlags` (bits 6-8)
- Create UI toggle in reader toolbar
- Integrate with `SubsamplingScaleImageView`

#### 1.3 UI Components
- Toggle button in `ReaderBottomBar`
- Toast notification showing current mode
- String resources for all 5 modes

#### 1.4 Acceptance Criteria
- [ ] All 5 scale modes functional
- [ ] Per-manga persistence works
- [ ] UI toggle responsive
- [ ] Smooth transitions between modes
- [ ] Works with both pager and webtoon viewers

---

## Phase 2: AI Recommendations

### Objective
Implement AI-powered manga recommendation system.

### Technical Requirements

#### 2.1 Database Schema (SQLDelight)
```sql
-- reading_history table
CREATE TABLE reading_history (
    manga_id INTEGER PRIMARY KEY,
    last_read_at INTEGER NOT NULL,
    time_spent INTEGER DEFAULT 0,
    chapters_read INTEGER DEFAULT 0
);

-- manga_tags table
CREATE TABLE manga_tags (
    manga_id INTEGER PRIMARY KEY,
    genres TEXT NOT NULL,
    themes TEXT,
    demographics TEXT
);

-- recommendations_cache table
CREATE TABLE recommendations_cache (
    manga_id INTEGER NOT NULL,
    recommended_manga_id INTEGER NOT NULL,
    score REAL NOT NULL,
    reason TEXT NOT NULL,
    generated_at INTEGER NOT NULL,
    PRIMARY KEY (manga_id, recommended_manga_id)
);
```

#### 2.2 Recommendation Engines

**A. Collaborative Filtering Engine**
- Find users with similar reading patterns
- Calculate similarity using cosine similarity
- Recommend manga liked by similar users

**B. Content-Based Engine**
- Extract features from manga (genres, themes, author)
- Calculate Jaccard similarity between manga
- Recommend similar manga based on features

**C. Hybrid Engine**
- Combine collaborative and content-based (70/30 weight)
- Confidence scoring
- Generate explanation strings

#### 2.3 Background Processing
- WorkManager for weekly updates
- `UpdateRecommendationsWorker` class
- Batch processing for efficiency

#### 2.4 UI Components
- "For You" tab in Browse
- "Because you read X" section
- Recommendation cards with confidence badges
- Swipe to dismiss

#### 2.5 Acceptance Criteria
- [ ] Recommendations generate within 5 seconds
- [ ] Cache updates weekly
- [ ] UI shows loading states
- [ ] Explanations are meaningful
- [ ] Works offline with cached data

---

## Phase 3: Gallery View

### Objective
Implement thumbnail strip navigation for quick page jumping.

### Technical Requirements

#### 3.1 Thumbnail Strip Component
```kotlin
@Composable
fun ThumbnailStrip(
    pages: List<ReaderPage>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    visible: Boolean,
    position: GalleryPosition,
    modifier: Modifier = Modifier
)
```

#### 3.2 Layout Variants
- **Horizontal:** Top or Bottom placement
- **Vertical:** Left or Right placement
- Support for RTL layouts

#### 3.3 Settings
- Position: TOP, BOTTOM, LEFT, RIGHT
- Thumbnail size: SMALL, MEDIUM, LARGE
- Auto-hide delay: 3-30 seconds
- Show/hide page numbers

#### 3.4 Integration
- Gallery button in `ReaderBottomBar`
- Replace slider option
- Gesture support (swipe to dismiss)

#### 3.5 Acceptance Criteria
- [ ] Smooth scrolling performance
- [ ] Thumbnails load asynchronously
- [ ] Current page highlighted
- [ ] Tap to jump works instantly
- [ ] Auto-hide functional

---

## Phase 4: Dynamic Theming

### Objective
Implement cover-based dynamic theming.

### Technical Requirements

#### 4.1 Color Extraction
- Use Palette API or MaterialKolor
- Extract dominant colors from cover image
- Generate color schemes (Vibrant, Muted, etc.)

#### 4.2 Theme Application
- Apply seed color to MaterialTheme
- Support multiple palette styles:
  - Tonal Spot
  - Vibrant
  - Expressive
  - Fidelity
  - Content
  - Neutral

#### 4.3 Settings
- Toggle: `themeCoverBased`
- Palette style selector
- Apply to reader background

#### 4.4 Acceptance Criteria
- [ ] Colors extract within 500ms
- [ ] Theme applies smoothly
- [ ] Works with all cover types
- [ ] Fallback for extraction failures

---

## Phase 5: Enhanced Search

### Objective
Implement comprehensive search with suggestions, history, and smart ranking.

### Technical Requirements

#### 5.1 Search History
```kotlin
// Data class
 data class SearchHistoryItem(
    val query: String,
    val timestamp: Long,
    val resultCount: Int
)

// Repository
class SearchHistoryRepository @Inject constructor(
    private val preferenceStore: PreferenceStore
) {
    fun getSearchHistory(): Flow<List<SearchHistoryItem>>
    suspend fun addSearch(query: String, resultCount: Int)
    suspend fun removeSearch(query: String)
    suspend fun clearHistory()
}
```

#### 5.2 Search Suggestions
- Real-time suggestions as user types
- Debounce: 300ms
- Sources: History, Trending, Library titles
- Highlight matching text

#### 5.3 Smart Ranking
```kotlin
// Scoring algorithm
score = (
    titleMatchWeight * titleScore +      // 0-50
    sourceReliabilityWeight * sourceScore + // 0-20
    popularityWeight * chapterCount +      // 0-20
    libraryPenalty                         // -10 if in library
) / totalWeight
```

#### 5.4 Universal Search
- Single search bar
- Results grouped by source:
  - Library (priority)
  - History
  - Browse sources
- Source badges on results

#### 5.5 Voice Search
- Android SpeechRecognizer integration
- Mic button in search bar
- Fallback to keyboard

#### 5.6 Saved Searches
- Save query with filters
- Weekly background check
- Push notifications for new results
- `SavedSearchCheckWorker` with WorkManager

#### 5.7 Acceptance Criteria
- [ ] Suggestions appear within 300ms
- [ ] Ranking improves relevance
- [ ] Voice search accuracy > 90%
- [ ] Saved searches notify correctly
- [ ] All search modes functional

---

## Phase 6: Reading Stats & QoL

### Objective
Implement reading statistics and quality of life features.

### Technical Requirements

#### 6.1 Reading Timer
```kotlin
class ReadingStatsRepository @Inject constructor(
    private val preferenceStore: PreferenceStore
) {
    fun readingGoalEnabled(): Preference<Boolean>
    fun readingGoalMinutes(): Preference<Int>
    fun getDailyReadingTime(): Flow<Long>
    fun getReadingStreak(): Flow<Int>
    
    fun startSession()
    suspend fun endSession()
}
```

#### 6.2 Smart Brightness
- Time-based auto-adjustment:
  - Morning (6-8am): 50%
  - Day (9am-4pm): 100%
  - Evening (5-7pm): 50%
  - Night (8-10pm): -20%
  - Late night (11pm-5am): -50%
- Per-manga brightness memory

#### 6.3 UI Components
- Reading timer overlay
- Daily goal progress
- Streak counter with fire icon
- Brightness adjustment slider

#### 6.4 Acceptance Criteria
- [ ] Timer accurate within 1 second
- [ ] Streak counts correctly
- [ ] Brightness adjusts smoothly
- [ ] Settings persist across sessions

---

## Architecture & Patterns

### Dependency Injection
- Use Injekt/Koin for DI
- All repositories annotated with @Singleton @Inject
- Constructor injection preferred

### State Management
- MVVM with StateFlow
- Immutable State classes with @Immutable
- Unidirectional data flow

### Database
- SQLDelight for type-safe SQL
- Migrations for schema changes
- Repository pattern for data access

### Background Work
- WorkManager for periodic tasks
- Coroutines for async operations
- Flow for reactive streams

---

## File Structure

```
app/src/main/java/eu/kanade/
├── domain/
│   ├── search/
│   │   ├── SearchHistoryRepository.kt
│   │   ├── SearchHistoryItem.kt
│   │   ├── SearchSuggestionsRepository.kt
│   │   ├── SearchRankingRepository.kt
│   │   ├── SavedSearchRepository.kt
│   │   └── SavedSearchCheckWorker.kt
│   ├── readingstats/
│   │   └── ReadingStatsRepository.kt
│   ├── brightness/
│   │   └── SmartBrightnessRepository.kt
│   └── recommendations/
│       ├── engine/
│       │   ├── CollaborativeFilteringEngine.kt
│       │   ├── ContentBasedEngine.kt
│       │   └── HybridRecommendationEngine.kt
│       └── RecommendationsRepositoryImpl.kt
├── presentation/
│   ├── search/
│   │   ├── SearchSuggestionsDropdown.kt
│   │   ├── SearchHistoryDropdown.kt
│   │   ├── UniversalSearchScreen.kt
│   │   └── VoiceSearchButton.kt
│   ├── reader/
│   │   ├── ThumbnailStrip.kt
│   │   └── stats/
│   │       └── ReadingTimerOverlay.kt
│   └── browse/
│       └── recommendations/
│           └── RecommendationsScreenContent.kt
└── tachiyomi/
    └── ui/
        ├── library/
        │   └── AuthorSearchParser.kt
        └── reader/
            └── setting/
                └── ReaderPreferences.kt
```

---

## String Resources

All new features require string resources in:
`i18n-kmk/src/commonMain/moko-resources/base/strings.xml`

Example:
```xml
<string name="gallery_position_title">Gallery position</string>
<string name="scale_mode_fit_screen">Fit screen</string>
<string name="recent_searches">Recent searches</string>
```

---

## Build Configuration

### Workflows
- `.github/workflows/build-debug.yml` - Debug builds
- `.github/workflows/build_pull_request.yml` - PR checks

### Gradle
- Min SDK: 24
- Target SDK: 34
- Java 17
- Kotlin 1.9+

### Key Dependencies
- Jetpack Compose
- SQLDelight
- WorkManager
- Coil (image loading)
- MaterialKolor (theming)
- Moko Resources (localization)

---

## Testing Strategy

### Unit Tests
- Repository logic
- Ranking algorithms
- State management

### Integration Tests
- Database operations
- Worker execution
- Search functionality

### UI Tests
- Navigation flows
- Reader interactions
- Settings changes

---

## Performance Requirements

- App launch: < 2 seconds
- Page load: < 500ms
- Search results: < 1 second
- Thumbnail generation: < 200ms
- Memory usage: < 512MB average

---

## Security Considerations

- No hardcoded secrets
- SQL injection prevention (parameterized queries)
- Input validation on all user inputs
- Secure storage for preferences
- ProGuard/R8 obfuscation for release

---

## Deliverables

### Code
- [ ] All features implemented
- [ ] Unit tests passing
- [ ] No lint errors
- [ ] Documentation complete

### Artifacts
- [ ] Debug APK
- [ ] Release APK (signed)
- [ ] Source code (GitHub)
- [ ] Documentation (Markdown)

### Documentation
- [ ] README.md updated
- [ ] CHANGELOG.md updated
- [ ] Architecture decision records
- [ ] API documentation (if applicable)

---

## Timeline Estimate

| Phase | Duration | Complexity |
|-------|----------|------------|
| Phase 1: Scale Modes | 2 days | Medium |
| Phase 2: AI Recommendations | 5 days | High |
| Phase 3: Gallery View | 3 days | Medium |
| Phase 4: Dynamic Theming | 2 days | Low |
| Phase 5: Enhanced Search | 7 days | High |
| Phase 6: Reading Stats | 3 days | Medium |
| **Total** | **22 days** | - |

---

## Success Criteria

1. All 21 features functional
2. Build passes CI/CD
3. No critical bugs
4. Performance benchmarks met
5. User acceptance testing passed
6. Documentation complete

---

## Notes for AI Implementation

### Common Issues to Avoid
1. **Missing @Inject annotations** - All repositories need proper DI setup
2. **Inner class references** - Use standalone classes for data models
3. **String resources** - Add to i18n module, not hardcoded
4. **Moko resources** - Run `:i18n:generateMRcommonMain` after adding strings
5. **State class immutability** - Use copy() for updates, never mutate directly

### Build Commands
```bash
# Clean build
./gradlew clean

# Generate resources
./gradlew :i18n:generateMRcommonMain

# Build debug APK
./gradlew :app:assembleDebug

# Run tests
./gradlew test
```

### Debugging Tips
- Check imports are correct (especially for inner classes)
- Verify all string resources exist
- Ensure SQLDelight tables match queries
- Test on actual device, not just emulator

---

## Conclusion

This project plan provides a comprehensive roadmap for implementing all features in Komikku 2026. Each phase builds upon the previous, with clear technical requirements and acceptance criteria. The architecture supports scalability and maintainability, while the testing strategy ensures quality delivery.

**Estimated Total Effort:** 22 developer days  
**Complexity:** High  
**Risk Level:** Medium (mitigated by phased approach)
