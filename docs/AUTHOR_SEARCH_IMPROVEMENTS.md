# Author/Artist Search - Problem Analysis & Solutions

## 🔍 Current State (The Problem)

### Library Search (filterManga function):
```kotlin
// Current implementation - SIMPLE TEXT MATCH
manga.title.contains(query, true) ||
    (manga.author?.contains(query, true) == true) ||
    (manga.artist?.contains(query, true) == true)
```

**Issues:**
1. **Substring only** - "Oda" won't find "Eiichiro Oda" (case insensitive, but not word-boundary)
2. **No dedicated author search** - Mixed with title, description, genre
3. **No author index** - Full table scan on every search
4. **No author suggestions** - No autocomplete
5. **Inconsistent data** - Some sources use "Last, First", others "First Last"

### Global Search (Browse):
- Searches by title ONLY across sources
- Author/artist search depends on source implementation
- Most sources don't support author search at all

---

## 🚨 Why It's Terrible

| Issue | User Impact |
|-------|-------------|
| "Oda" search | Finds "Oda Nobuna" (title) but NOT "One Piece" by Eiichiro Oda |
| "Eiichiro Oda" search | Works only if exact match in author field |
| "Oda, Eiichiro" vs "Eiichiro Oda" | Same person, different results |
| Multi-author manga | Only finds if ALL authors match query |
| No author browsing | Can't see all manga by favorite author |

---

## ✅ Solutions (Ranked by Impact)

### Solution 1: Author Index & Dedicated Search (HIGH IMPACT)

**New Database Table:**
```sql
CREATE TABLE authors (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    normalized_name TEXT NOT NULL, -- "eichirooda" for fuzzy matching
    manga_count INTEGER DEFAULT 0
);

CREATE TABLE manga_authors (
    manga_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    role TEXT NOT NULL, -- 'author', 'artist', 'story', 'art'
    PRIMARY KEY (manga_id, author_id, role)
);

CREATE INDEX idx_authors_normalized ON authors(normalized_name);
CREATE INDEX idx_manga_authors_author ON manga_authors(author_id);
```

**Benefits:**
- Fast author lookup
- Normalized names (handle "Oda, Eiichiro" vs "Eiichiro Oda")
- Browse all manga by author
- Author statistics

---

### Solution 2: Enhanced Library Search (MEDIUM IMPACT)

**Prefix Search Syntax:**
```
author:oda          → Find all by Oda
artist:takeuchi     → Find all by Takeuchi
author:"Eiichiro Oda" → Exact author match
a:oda               → Short form
```

**Implementation:**
```kotlin
// Parse query for prefixes
when {
    query.startsWith("author:") -> searchByAuthor(query.removePrefix("author:"))
    query.startsWith("artist:") -> searchByArtist(query.removePrefix("artist:"))
    query.startsWith("a:") -> searchByAuthorOrArtist(query.removePrefix("a:"))
    else -> normalSearch(query)
}

// Author search with normalization
fun searchByAuthor(name: String): List<Manga> {
    val normalized = normalizeName(name) // "eichirooda"
    return database.searchAuthors(normalized)
}
```

**Benefits:**
- Precise author search
- No false positives from titles
- Power user feature

---

### Solution 3: Fuzzy Author Matching (MEDIUM IMPACT)

**Handle Name Variations:**
```kotlin
// Normalize author names
fun normalizeAuthorName(name: String): String {
    return name
        .lowercase()
        .replace(",", "")           // "Oda, Eiichiro" → "oda eiichiro"
        .replace(".", "")           // "E. Oda" → "e oda"
        .replace("  ", " ")         // Clean double spaces
        .trim()
        .split(" ")
        .sorted()                   // Consistent order
        .joinToString("")
}

// "Eiichiro Oda", "Oda, Eiichiro", "Oda Eiichiro" → all become "eichiroota"
```

**Search:**
```kotlin
// Fuzzy match with Levenshtein distance
fun findAuthors(query: String): List<Author> {
    val normalized = normalizeAuthorName(query)
    return authors.filter { 
        levenshteinDistance(it.normalizedName, normalized) <= 2 
    }
}
```

**Benefits:**
- Handles inconsistent naming
- Finds similar spellings
- Better user experience

---

### Solution 4: Author Autocomplete (HIGH UX IMPACT)

**Search Bar Enhancement:**
```
User types: "od"
Suggestions:
  📚 "oda" in titles
  👤 Author: Eiichiro Oda
  👤 Artist: Oda Suzuka
  👤 Author: Oda Nobuna

User types: "author:ei"
Suggestions:
  👤 Eiichiro Oda (42 manga)
  👤 Eiji Yoshikawa (12 manga)
```

**Implementation:**
```kotlin
@Composable
fun AuthorAutocomplete(
    query: String,
    onAuthorSelected: (Author) -> Unit
) {
    val suggestions by remember(query) {
        derivedStateOf {
            when {
                query.startsWith("author:") -> searchAuthors(query.removePrefix("author:"))
                query.length >= 2 -> searchAuthors(query) // Show author suggestions
                else -> emptyList()
            }
        }
    }
    
    LazyColumn {
        items(suggestions) { author ->
            AuthorSuggestionItem(
                author = author,
                onClick = { onAuthorSelected(author) }
            )
        }
    }
}
```

---

### Solution 5: Author Profile Page (HIGH VALUE)

**New Screen:** AuthorDetailScreen
```
┌─────────────────────────────────────┐
│ ← Eiichiro Oda                      │
├─────────────────────────────────────┤
│ 👤 42 Manga | 15,234 Chapters       │
│ ⭐ Avg Rating: 4.5/5                │
│                                     │
│ 📚 Manga:                           │
│ ┌─────────┐ ┌─────────┐ ┌─────────┐│
│ │One Piece│ │  OP:    │ │ Romance ││
│ │         │ │  Ace    │ │ Dawn    ││
│ └─────────┘ └─────────┘ └─────────┘│
│                                     │
│ 🏆 Most Popular: One Piece          │
│ 📅 Latest: OP: Ace Story (2024)     │
└─────────────────────────────────────┘
```

**Benefits:**
- Discover more by favorite creators
- Author statistics
- Better browsing experience

---

## 🎯 Recommended Implementation Plan

### Phase 1: Quick Wins (1-2 days)
1. **Prefix Search Syntax**
   - Add `author:` and `artist:` prefix support
   - No DB changes needed
   - Immediate power user benefit

2. **Normalize Author Names**
   - Clean up existing author data
   - Handle "Last, First" format

### Phase 2: Core Improvement (3-5 days)
3. **Author Index Table**
   - Create authors + manga_authors tables
   - Migration for existing data
   - Fast author lookup

4. **Author Autocomplete**
   - Search bar suggestions
   - Author icon indicators

### Phase 3: Polish (2-3 days)
5. **Author Profile Page**
   - Browse all manga by author
   - Statistics display

6. **Global Author Search**
   - Search by author across sources (if supported)

---

## 📁 Files to Modify

### New Files:
```
domain/src/main/java/tachiyomi/domain/author/
  - Author.kt
  - AuthorRepository.kt
  - GetMangaByAuthor.kt

data/src/main/sqldelight/tachiyomi/data/
  - authors.sq
  - manga_authors.sq

app/src/main/java/eu/kanade/presentation/author/
  - AuthorAutocomplete.kt
  - AuthorProfileScreen.kt
```

### Modified Files:
```
app/src/main/java/eu/kanade/tachiyomi/ui/library/LibraryScreenModel.kt
  - filterManga() - add prefix parsing
  - searchByAuthor() - new function

app/src/main/java/eu/kanade/presentation/library/
  - LibraryToolbar.kt - add autocomplete

data/src/main/sqldelight/tachiyomi/data/mangas.sq
  - Add author search query
```

---

## 💡 Quick Implementation: Prefix Search

Can implement immediately (no DB changes):

```kotlin
// In LibraryScreenModel.kt
private fun filterLibrary(unfiltered: List<LibraryItem>, query: String?, ...): List<LibraryItem> {
    if (query.isNullOrBlank()) return unfiltered
    
    // NEW: Handle author prefix
    val authorQuery = when {
        query.startsWith("author:", ignoreCase = true) -> 
            query.removePrefix("author:").trim() to "author"
        query.startsWith("artist:", ignoreCase = true) -> 
            query.removePrefix("artist:").trim() to "artist"
        query.startsWith("a:", ignoreCase = true) -> 
            query.removePrefix("a:").trim() to "both"
        else -> null
    }
    
    if (authorQuery != null) {
        val (searchTerm, field) = authorQuery
        return unfiltered.filter { item ->
            when (field) {
                "author" -> item.libraryManga.manga.author?.contains(searchTerm, true) == true
                "artist" -> item.libraryManga.manga.artist?.contains(searchTerm, true) == true
                "both" -> item.libraryManga.manga.author?.contains(searchTerm, true) == true ||
                         item.libraryManga.manga.artist?.contains(searchTerm, true) == true
                else -> false
            }
        }
    }
    
    // Existing search logic...
}
```

**Usage:**
- Type `author:oda` → finds all by Oda
- Type `artist:takeuchi` → finds all by Takeuchi
- Type `a:oda` → finds all by Oda (author OR artist)