## Feature: AI Recommendations

### Simple Approach (No Heavy ML)

#### Data Collection
```kotlin
// Tables to add
data class ReadingHistory(
    val mangaId: Long,
    val chaptersRead: Int,
    val totalChapters: Int,
    val lastRead: Long,
    val timeSpent: Long // milliseconds
)

data class MangaTags(
    val mangaId: Long,
    val genres: List<String>,
    val themes: List<String>,
    val author: String,
    val status: String // ongoing, completed
)
```

#### Recommendation Algorithm
```kotlin
fun getRecommendations(): List<Manga> {
    // 1. Get user's top genres from reading history
    val topGenres = getTopGenresFromHistory()
    
    // 2. Find manga with similar genres not yet read
    return mangaRepository
        .getByGenres(topGenres)
        .filter { it !in readingHistory }
        .sortedBy { it.popularity }
        .take(10)
}
```

#### UI
- New "For You" tab in Browse
- "Because you read X" section
- Simple list with cover + title + reason

#### Implementation
- Database migration for reading_history table
- Background job to update recommendations weekly
- Cache results for performance