# ReaderViewModel Refactoring Plan

## Current State
- **File:** `ReaderViewModel.kt`
- **Lines:** 1,569
- **Problem:** Too many responsibilities, hard to maintain

## Refactoring Strategy

### 1. Extract ReaderStateManager
**Responsibilities:**
- Manage current page/chapter state
- Handle page transitions
- Track reading position

```kotlin
class ReaderStateManager(
    private val getChapters: GetChaptersByMangaId,
    private val chapterLoader: ChapterLoader,
) {
    suspend fun loadChapter(chapter: ReaderChapter)
    fun getCurrentPage(): Int
    fun setCurrentPage(page: Int)
    fun hasNextChapter(): Boolean
    fun hasPreviousChapter(): Boolean
}
```

### 2. Extract HistoryTracker
**Responsibilities:**
- Track reading history
- Update database
- Calculate reading time

```kotlin
class HistoryTracker(
    private val upsertHistory: UpsertHistory,
    private val trackChapter: TrackChapter,
) {
    suspend fun startReading(chapter: Chapter)
    suspend fun stopReading(chapter: Chapter, pagesRead: Int)
    suspend fun markChapterRead(chapter: Chapter)
}
```

### 3. Extract DownloadManager
**Responsibilities:**
- Handle chapter downloads
- Check download status
- Queue downloads

```kotlin
class ReaderDownloadManager(
    private val downloadManager: DownloadManager,
) {
    suspend fun downloadChapter(chapter: Chapter)
    fun isChapterDownloaded(chapter: Chapter): Boolean
    fun deleteChapterDownload(chapter: Chapter)
}
```

### 4. Extract ImageSaver
**Responsibilities:**
- Save images to device
- Handle share functionality
- Manage temp files

```kotlin
class ReaderImageSaver(
    private val imageSaver: ImageSaver,
    private val tempFileManager: UniFileTempFileManager,
) {
    suspend fun saveImage(page: ReaderPage, location: Location): Uri
    suspend fun shareImage(page: ReaderPage): Uri
}
```

## Benefits

1. **Testability:** Each class can be unit tested independently
2. **Maintainability:** Smaller, focused classes
3. **Reusability:** Components can be reused elsewhere
4. **Readability:** Clear separation of concerns

## Migration Plan

1. Create new classes alongside existing code
2. Gradually move functionality
3. Update tests
4. Remove old code once stable

## Estimated Reduction
- ReaderViewModel: 1,569 → ~800 lines
- New files: 4 classes (~200 lines each)