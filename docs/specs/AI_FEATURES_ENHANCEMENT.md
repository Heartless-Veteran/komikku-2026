# AI Features Enhancement Spec

## 1. Smart Recommendations (ML-Based)

### Current: Tag-based matching
### New: Collaborative Filtering + Content-Based Hybrid

**Algorithm:**
- Collaborative: Users with similar taste → recommend what they liked
- Content-based: Manga features → find similar
- Hybrid: Weighted combination (70% collaborative, 30% content)

**Data needed:**
- User-manga interaction matrix (read, bookmark, rating)
- Manga feature vectors (tags, genres, themes)
- Similarity scores between users

**Implementation:**
- Lightweight ML model (on-device or small server)
- K-nearest neighbors for user similarity
- Cosine similarity for content matching
- Cache recommendations, update weekly

## 2. Smart Search (Natural Language)

### Current: Keyword search
### New: NL understanding with intent parsing

**Examples:**
- "action manga with strong female lead" → filter: action + tags:female-protagonist
- "something like One Piece but shorter" → find: adventure + similar-tags + chapter-count < 100
- "romance without drama" → filter: romance + exclude: drama

**Implementation:**
- Intent classification (genre, theme, length, similarity)
- Entity extraction (manga names, genres, attributes)
- Query builder from NL to SQL/filter
- Optional: LLM for complex queries

**Files to modify:**
- `RecommendationsRepositoryImpl.kt` - New algorithm
- `BrowseRepository.kt` - Smart search
- `MangaSearchScreen.kt` - NL input UI
- Database: Add user similarity cache

## Priority
1. Smart search (easier, immediate value)
2. Smart recommendations (more complex, long-term value)