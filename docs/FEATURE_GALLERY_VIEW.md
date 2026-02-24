## Feature: Gallery View

### Overview
Grid view of all pages in current chapter for quick navigation.

### UI Design
```kotlin
@Composable
fun GalleryView(
    pages: List<Page>,
    currentPage: Int,
    onPageClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        itemsIndexed(pages) { index, page ->
            ThumbnailItem(
                page = page,
                isSelected = index == currentPage,
                onClick = { onPageClick(index) }
            )
        }
    }
}
```

### Features
- 3-column grid on phones, 4-5 on tablets
- Current page highlighted
- Tap to jump to page
- Long-press for options (share, save)
- Swipe down to dismiss

### Implementation
- New screen: `GalleryScreen.kt`
- Entry point: Button in reader toolbar
- Thumbnail generation: Coil with resize

### Performance
- Lazy loading for large chapters
- Cache thumbnails in memory
- Preload next chapter thumbnails