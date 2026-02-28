#!/bin/bash

# GitHub Issues Creation Script for Tier 3 Features
# Run this after setting up GitHub CLI (gh) with authentication

echo "Creating GitHub Issues for Tier 3 Features..."

# Issue 1: Search Suggestions
ght issue create \
  --repo Heartless-Veteran/komikku-2026 \
  --title "[Feature] Search Suggestions and Autocomplete" \
  --label "enhancement,search" \
  --body "## Description
Implement search suggestions and autocomplete functionality to improve the search experience.

## Requirements
- [ ] Show search suggestions as user types (debounced 300ms)
- [ ] Suggest based on recent searches, trending, library titles, authors
- [ ] Keyboard navigation support
- [ ] Clear button for each suggestion
- [ ] Highlight matching text

## Files to Modify
- \`GlobalSearchToolbar.kt\`
- \`SearchScreenModel.kt\`
- \`SearchHistoryRepository.kt\`

## Estimated Effort
2-3 days

## Priority
High"

# Issue 2: Universal Search
ght issue create \
  --repo Heartless-Veteran/komikku-2026 \
  --title "[Feature] Universal Search Bar" \
  --label "enhancement,search" \
  --body "## Description
Create a single search entry point that searches across library, history, and browse sources.

## Requirements
- [ ] Single search bar on main screen
- [ ] Results grouped by source (Library, History, Sources)
- [ ] Quick filters (Library only, Sources only, All)
- [ ] Unified result ranking
- [ ] Source badges on results

## Files to Create/Modify
- \`UniversalSearchScreen.kt\` (new)
- \`UniversalSearchViewModel.kt\` (new)
- \`MainActivity.kt\`

## Estimated Effort
3-4 days

## Priority
High"

# Issue 3: Smart Results Ranking
ght issue create \
  --repo Heartless-Veteran/komikku-2026 \
  --title "[Feature] Smart Search Results Ranking" \
  --label "enhancement,search" \
  --body "## Description
Implement intelligent ranking for search results.

## Requirements
- [ ] Deduplicate across sources
- [ ] Ranking factors: title match, rating, source reliability
- [ ] 'Best match' badge
- [ ] Sort options: Relevance, Rating, Newest

## Files to Modify
- \`SearchScreenModel.kt\`
- \`GlobalSearchScreen.kt\`

## Estimated Effort
2-3 days

## Priority
Medium"

# Issue 4: Saved Searches
ght issue create \
  --repo Heartless-Veteran/komikku-2026 \
  --title "[Feature] Saved Searches with Alerts" \
  --label "enhancement,search" \
  --body "## Description
Allow users to save searches and get notified of new matches.

## Requirements
- [ ] Save search button
- [ ] List saved searches in settings
- [ ] Background check for new matches
- [ ] Push notifications

## Files to Create
- \`SavedSearchRepository.kt\`
- \`SavedSearchWorker.kt\`
- \`SavedSearchesScreen.kt\`

## Estimated Effort
2-3 days

## Priority
Medium"

# Issue 5: Voice Search
ght issue create \
  --repo Heartless-Veteran/komikku-2026 \
  --title "[Feature] Voice Search" \
  --label "enhancement,search" \
  --body "## Description
Add voice input capability to search.

## Requirements
- [ ] Microphone button in search bar
- [ ] Speech-to-text conversion
- [ ] Handle voice search intent

## Implementation
Use Android SpeechRecognizer API.

## Estimated Effort
1 day

## Priority
Low"

# Issue 6: OCR Search
ght issue create \
  --repo Heartless-Veteran/komikku-2026 \
  --title "[Feature] Search Within Manga (OCR)" \
  --label "enhancement,search,complex" \
  --body "## Description
Search for text within downloaded manga pages using OCR.

## Requirements
- [ ] OCR processing for pages
- [ ] Text indexing per manga
- [ ] Search dialog in manga details
- [ ] Offline capability

## Technical Approach
- ML Kit Text Recognition or Tesseract
- WorkManager for background processing
- SQLite FTS indexing

## Estimated Effort
5-7 days

## Priority
Low (complex)"

echo "All issues created successfully!"
