# Komikku 2026 - Complete Application Project Plan

## Project Overview

**Project Name:** Komikku 2026  
**Type:** Android Manga Reader Application  
**Base:** Fork of Mihon (formerly Tachiyomi)  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose  
**Architecture:** MVVM with Clean Architecture  
**Database:** SQLite with SQLDelight  
**Min SDK:** 24 (Android 7.0)  
**Target SDK:** 34 (Android 14)  

---

## Executive Summary

Komikku is a feature-rich manga reader application for Android that allows users to:
- Read manga from various online sources
- Download chapters for offline reading
- Track reading progress and history
- Organize library with categories
- Discover new manga through recommendations
- Customize reading experience extensively

This plan covers the complete application architecture, all existing features, and the 2026 enhancements.

---

## Core Application Architecture

### 1. Module Structure

```
komikku/
├── app/                          # Main application module
│   ├── src/main/java/eu/kanade/
│   │   ├── data/                 # Data layer (repositories, database)
│   │   ├── domain/               # Domain layer (use cases, models)
│   │   ├── presentation/         # UI layer (screens, components)
│   │   └── tachiyomi/            # Core app logic
│   └── src/main/res/             # Resources
├── core/
│   ├── common/                   # Common utilities
│   └── platform/                 # Platform-specific code
├── data/                         # Database schemas (SQLDelight)
├── domain/                       # Domain models and interfaces
├── i18n/                         # Localization resources
├── i18n-kmk/                     # Komikku-specific strings
├── presentation-core/            # Shared UI components
└── source-api/                   # Extension API for sources
```

### 2. Dependency Injection
- **Framework:** Injekt (Kotlin-native)
- **Pattern:** Constructor injection
- **Scope:** Singleton for repositories, Factory for ViewModels

### 3. State Management
- **Pattern:** MVVM with StateFlow
- **UI:** Jetpack Compose with unidirectional data flow
- **Persistence:** PreferenceStore for settings, SQLDelight for data

---

## Core Features (Existing)

### 1. Library Management

#### 1.1 Library Screen
- Grid/List display of saved manga
- Categories with custom sorting
- Search and filter functionality
- Bulk selection and operations
- Update checking for new chapters

#### 1.2 Manga Details
- Cover image display
- Synopsis and metadata
- Chapter list with read status
- Download management
- Tracking integration (MyAnimeList, AniList, etc.)

#### 1.3 Categories
- Create unlimited categories
- Drag-and-drop reordering
- Default category option
- Hidden categories

### 2. Reader

#### 2.1 Reading Modes
- **Pager:** Left-to-right, Right-to-left, Vertical
- **Webtoon:** Continuous vertical scrolling
- **Vertical Plus:** Optimized for long-strip manga

#### 2.2 Navigation
- Tap zones for page turning
- Volume key navigation
- Swipe gestures
- Chapter transitions

#### 2.3 Display Options
- 5 scale modes (FIT_SCREEN, FIT_WIDTH, FIT_HEIGHT, ORIGINAL_SIZE, SMART_FIT)
- Brightness adjustment
- Background color (Black, Gray, White, Auto)
- Fullscreen mode
- Keep screen on

#### 2.4 Advanced Features
- Double-page spread support
- Split tall images
- Crop borders
- Page rotation
- Auto-webtoon detection

### 3. Source Management

#### 3.1 Extensions
- Install from repository
- Manual APK installation
- Auto-update extensions
- Repository management

#### 3.2 Browse
- Browse by source
- Latest updates
- Popular manga
- Search within source
- Filter by genre/status

#### 3.3 Global Search
- Search across all sources
- Filter by pinned sources
- Show only results with chapters

### 4. Downloads

#### 4.1 Download Management
- Queue-based downloading
- Parallel downloads (configurable)
- Wi-Fi only option
- Download ahead while reading
- Auto-delete read chapters

#### 4.2 Storage
- Custom download location
- Folder per manga option
- Cache management
- Import local manga

### 5. Tracking

#### 5.1 Trackers Supported
- MyAnimeList
- AniList
- Kitsu
- Shikimori
- Bangumi
- Komga

#### 5.2 Sync Features
- Auto-sync progress
- Two-way sync
- Score and status sync

### 6. Backup & Restore

#### 6.1 Backup Options
- Automatic scheduled backups
- Manual backup creation
- Include/exclude categories
- Include/exclude tracking

#### 6.2 Restore
- Restore from file
- Selective restore
- Merge with existing library

### 7. Settings

#### 7.1 Appearance
- Theme selection
- Dark mode options
- Custom accent colors
- Cover style (rounded/square)

#### 7.2 Library
- Default category
- Update frequency
- Download new chapters
- Skip duplicate chapters

#### 7.3 Reader
- Default reading mode
- Navigation layout
- Page transitions
- Webtoon padding

#### 7.4 Downloads
- Download location
- Concurrent downloads
- Download ahead count
- Remove after read

#### 7.5 Tracking
- Auto-sync interval
- Default tracker
- Update on add to library

#### 7.6 Advanced
- Clear cache
- Refresh library covers
- Database cleanup
- Debug logging

---

## 2026 Enhancements (New Features)

### Phase 1: Perfect Viewer Scale Modes
- 5 scale types with per-manga persistence
- UI toggle in reader toolbar

### Phase 2: AI Recommendations
- Collaborative filtering
- Content-based filtering
- Hybrid engine (70/30)
- Weekly background updates

### Phase 3: Gallery View
- Thumbnail strip navigation
- 4 position options
- Replace slider option

### Phase 4: Dynamic Theming
- Cover color extraction
- Multiple palette styles
- Theme application

### Phase 5: Enhanced Search
- Search suggestions
- Universal search
- Smart ranking
- Voice search
- Saved searches with alerts

### Phase 6: Reading Stats
- Session timer
- Daily goals
- Reading streaks
- Smart brightness

---

## Technical Implementation Details

### 1. Database Schema

#### 1.1 Core Tables
```sql
-- Manga table
CREATE TABLE mangas (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    source INTEGER NOT NULL,
    url TEXT NOT NULL,
    artist TEXT,
    author TEXT,
    description TEXT,
    genre TEXT,
    title TEXT NOT NULL,
    status INTEGER NOT NULL,
    thumbnail_url TEXT,
    favorite INTEGER NOT NULL DEFAULT 0,
    last_update INTEGER,
    next_update INTEGER,
    initialized INTEGER NOT NULL DEFAULT 0,
    viewer INTEGER NOT NULL DEFAULT 0,
    chapter_flags INTEGER NOT NULL DEFAULT 0,
    cover_last_modified INTEGER NOT NULL DEFAULT 0,
    date_added INTEGER NOT NULL DEFAULT 0,
    update_strategy INTEGER NOT NULL DEFAULT 0,
    calculate_interval INTEGER NOT NULL DEFAULT 0,
    last_modified_at INTEGER NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    notes TEXT
);

-- Chapters table
CREATE TABLE chapters (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    manga_id INTEGER NOT NULL,
    url TEXT NOT NULL,
    name TEXT NOT NULL,
    scanlator TEXT,
    read INTEGER NOT NULL DEFAULT 0,
    bookmark INTEGER NOT NULL DEFAULT 0,
    last_page_read INTEGER NOT NULL DEFAULT 0,
    date_fetch INTEGER NOT NULL DEFAULT 0,
    date_upload INTEGER NOT NULL DEFAULT 0,
    chapter_number REAL NOT NULL DEFAULT -1,
    source_order INTEGER NOT NULL DEFAULT 0,
    last_modified_at INTEGER NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (manga_id) REFERENCES mangas(_id) ON DELETE CASCADE
);

-- Categories table
CREATE TABLE categories (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    order INTEGER NOT NULL DEFAULT 0,
    flags INTEGER NOT NULL DEFAULT 0,
    manga_order TEXT
);

-- Manga-Category mapping
CREATE TABLE mangas_categories (
    manga_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    FOREIGN KEY (manga_id) REFERENCES mangas(_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(_id) ON DELETE CASCADE,
    PRIMARY KEY (manga_id, category_id)
);

-- History table
CREATE TABLE history (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    chapter_id INTEGER NOT NULL UNIQUE,
    last_read INTEGER,
    time_read INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (chapter_id) REFERENCES chapters(_id) ON DELETE CASCADE
);

-- Tracking table
CREATE TABLE tracks (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    manga_id INTEGER NOT NULL,
    sync_id INTEGER NOT NULL,
    remote_id INTEGER NOT NULL,
    library_id INTEGER,
    title TEXT,
    last_chapter_read INTEGER,
    total_chapters INTEGER,
    status INTEGER,
    score REAL,
    remote_url TEXT,
    start_date INTEGER,
    finish_date INTEGER,
    UNIQUE (manga_id, sync_id)
);
```

#### 1.2 2026 Enhancement Tables
```sql
-- Reading history for recommendations
CREATE TABLE reading_history (
    manga_id INTEGER PRIMARY KEY,
    last_read_at INTEGER NOT NULL,
    time_spent INTEGER DEFAULT 0,
    chapters_read INTEGER DEFAULT 0
);

-- Manga tags for recommendations
CREATE TABLE manga_tags (
    manga_id INTEGER PRIMARY KEY,
    genres TEXT NOT NULL,
    themes TEXT,
    demographics TEXT
);

-- Recommendations cache
CREATE TABLE recommendations_cache (
    manga_id INTEGER NOT NULL,
    recommended_manga_id INTEGER NOT NULL,
    score REAL NOT NULL,
    reason TEXT NOT NULL,
    generated_at INTEGER NOT NULL,
    PRIMARY KEY (manga_id, recommended_manga_id)
);

-- Saved searches
CREATE TABLE saved_searches (
    id TEXT PRIMARY KEY,
    query TEXT NOT NULL,
    filters TEXT,
    created_at INTEGER NOT NULL,
    last_checked_at INTEGER NOT NULL,
    last_result_count INTEGER NOT NULL DEFAULT 0,
    notify_on_new_results INTEGER NOT NULL DEFAULT 1
);
```

### 2. Key Repositories

```kotlin
// Manga repository
interface MangaRepository {
    suspend fun getMangaById(id: Long): Manga?
    suspend fun getMangaByUrlAndSource(url: String, sourceId: Long): Manga?
    suspend fun insertManga(manga: Manga): Long
    suspend fun updateManga(manga: Manga)
    suspend fun deleteManga(manga: Manga)
    fun getLibraryManga(): Flow<List<LibraryManga>>
}

// Chapter repository
interface ChapterRepository {
    suspend fun getChapterById(id: Long): Chapter?
    suspend fun getChaptersByMangaId(mangaId: Long): List<Chapter>
    suspend fun insertChapter(chapter: Chapter): Long
    suspend fun updateChapter(chapter: Chapter)
    suspend fun deleteChapters(chapters: List<Chapter>)
}

// Category repository
interface CategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getCategoriesByMangaId(mangaId: Long): List<Category>
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long)
}

// History repository
interface HistoryRepository {
    suspend fun getHistoryByMangaId(mangaId: Long): List<History>
    suspend fun upsertHistory(history: History)
    suspend fun deleteHistory(historyId: Long)
}
```

### 3. Source API

```kotlin
// Base source interface
interface Source {
    val id: Long
    val name: String
    val lang: String
    val supportsLatest: Boolean
    
    suspend fun getMangaDetails(manga: Manga): SManga
    suspend fun getChapterList(manga: Manga): List<SChapter>
    suspend fun getPageList(chapter: SChapter): List<Page>
}

// Catalogue source (searchable)
interface CatalogueSource : Source {
    suspend fun getSearchManga(page: Int, query: String, filters: FilterList): MangasPage
    suspend fun getLatestUpdates(page: Int): MangasPage
    fun getFilterList(): FilterList
}

// Configurable source
interface ConfigurableSource : Source {
    val baseUrl: String
    fun setupPreferenceScreen(screen: PreferenceScreen)
}
```

### 4. UI Components

#### 4.1 Navigation
```kotlin
// Main navigation destinations
sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Updates : Screen("updates")
    object History : Screen("history")
    object Browse : Screen("browse")
    object More : Screen("more")
    
    // Detail screens
    object Manga : Screen("manga/{mangaId}")
    object Reader : Screen("reader/{mangaId}/{chapterId}")
    object WebView : Screen("webview")
    object Settings : Screen("settings")
}
```

#### 4.2 Common Components
```kotlin
// Manga cover component
@Composable
fun MangaCover(
    data: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
)

// Manga list item
@Composable
fun MangaListItem(
    manga: Manga,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
)

// Chapter list item
@Composable
fun ChapterListItem(
    chapter: Chapter,
    isRead: Boolean,
    isBookmarked: Boolean,
    downloadState: Download.State,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
)
```

---

## Build System

### Gradle Configuration
```kotlin
// build.gradle.kts (app module)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.moko.resources)
}

android {
    namespace = "app.komikku"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.komikku"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "2026.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    
    // Navigation
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.transitions)
    
    // Dependency Injection
    implementation(libs.injekt.core)
    implementation(libs.injekt.compose)
    
    // Database
    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)
    
    // Image Loading
    implementation(libs.coil.compose)
    
    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Preferences
    implementation(libs.preference.ktx)
    
    // WorkManager
    implementation(libs.workmanager.ktx)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.compose.ui.test)
}
```

### Build Commands
```bash
# Clean build
./gradlew clean

# Generate moko resources
./gradlew :i18n:generateMRcommonMain

# Build debug APK
./gradlew :app:assembleDebug

# Build release APK
./gradlew :app:assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint

# Install debug
./gradlew :app:installDebug
```

---

## Testing Strategy

### 1. Unit Tests
```kotlin
class SearchRankingRepositoryTest {
    @Test
    fun `calculateScore returns higher score for exact match`() {
        // Test implementation
    }
    
    @Test
    fun `rankResults deduplicates across sources`() {
        // Test implementation
    }
}
```

### 2. Integration Tests
```kotlin
class MangaRepositoryTest {
    @Test
    fun `insert and retrieve manga`() = runTest {
        // Test implementation
    }
}
```

### 3. UI Tests
```kotlin
class LibraryScreenTest {
    @Test
    fun `display manga in library`() {
        composeTestRule.setContent {
            LibraryScreen()
        }
        
        composeTestRule
            .onNodeWithText("Test Manga")
            .assertIsDisplayed()
    }
}
```

---

## Deployment

### Release Process
1. Update version in `build.gradle.kts`
2. Update `CHANGELOG.md`
3. Create signed release build
4. Upload to GitHub Releases
5. Update website/download links

### Signing Configuration
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH"))
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}
```

---

## Performance Targets

| Metric | Target |
|--------|--------|
| Cold start time | < 2 seconds |
| Library load (1000 manga) | < 500ms |
| Chapter list load | < 300ms |
| Page load (network) | < 2 seconds |
| Page load (cached) | < 100ms |
| Memory usage | < 512MB |
| APK size | < 50MB |
| Database size | < 100MB |

---

## Security Considerations

1. **No hardcoded secrets** - Use environment variables
2. **SQL Injection prevention** - Use parameterized queries
3. **Certificate pinning** - For API calls
4. **ProGuard obfuscation** - For release builds
5. **Secure storage** - Encrypted preferences for sensitive data

---

## Maintenance

### Regular Tasks
- Weekly: Update extensions repository
- Monthly: Dependency updates
- Quarterly: Security audit
- Annually: Major version update

### Monitoring
- Crash reporting (Firebase Crashlytics)
- Analytics (Firebase Analytics)
- User feedback (GitHub Issues)

---

## Resources

### Documentation
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design 3](https://m3.material.io/)

### Community
- GitHub: https://github.com/Heartless-Veteran/komikku-2026
- Issues: https://github.com/Heartless-Veteran/komikku-2026/issues

---

## Conclusion

This comprehensive plan covers the entire Komikku 2026 application - from core architecture to every feature, database schema, API design, build system, and deployment process. The 2026 enhancements build upon this solid foundation, adding AI recommendations, enhanced search, gallery view, and reading statistics.

**Total Estimated Effort:** 40-50 developer days for complete implementation from scratch
**Complexity:** High
**Team Size Recommended:** 2-3 developers

---

## Notes for AI Implementation

### Critical Success Factors
1. **Follow architecture patterns** - Don't bypass MVVM/Clean Architecture
2. **Use dependency injection** - Never create dependencies manually
3. **Respect SQLDelight** - All DB operations through generated code
4. **Test thoroughly** - Unit tests for logic, UI tests for flows
5. **Profile performance** - Check memory and CPU usage

### Common Pitfalls
1. **Memory leaks** - Always clear references in ViewModel.onCleared()
2. **Blocking main thread** - Use coroutines for all I/O
3. **SQL injection** - Never concatenate SQL strings
4. **Resource leaks** - Close cursors, cancel jobs
5. **State mutation** - Always create new state objects

### Build Issues to Watch
1. Moko resources not generating - Run `:i18n:generateMRcommonMain`
2. SQLDelight errors - Check .sq file syntax
3. Compose compiler version - Match with Kotlin version
4. ProGuard warnings - Add keep rules

---

**End of Complete Application Project Plan**
