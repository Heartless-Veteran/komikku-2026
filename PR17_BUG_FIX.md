# Bug Fix for PR #17

## Issue Found

**File:** `ReaderViewModel.kt`
**Function:** `toggleScaleMode()`
**Line:** ~1177

### Problem
```kotlin
val viewer = state.value.viewer
when (viewer) {
    is PagerViewer -> viewer.config.scaleMode = nextMode
    is WebtoonViewer -> viewer.config.scaleMode = nextMode
}
```

If `viewer` is `null`, the `when` statement does nothing silently. This means:
1. The scale mode preference is saved
2. But the viewer doesn't update
3. User sees no visual feedback

### Fix Needed
Add null handling:

```kotlin
val viewer = state.value.viewer
when (viewer) {
    is PagerViewer -> viewer.config.scaleMode = nextMode
    is WebtoonViewer -> viewer.config.scaleMode = nextMode
    null -> { /* Viewer not ready, preference saved but will apply on next load */ }
}
```

Or better - check if viewer exists before applying:

```kotlin
if (mangaId != null) {
    readerPreferences.mangaScaleMode(mangaId).set(nextIndex)
    // Only apply to viewer if it exists
    state.value.viewer?.let { viewer ->
        when (viewer) {
            is PagerViewer -> viewer.config.scaleMode = nextMode
            is WebtoonViewer -> viewer.config.scaleMode = nextMode
        }
    }
}
```

## Recommendation

**Request changes** - Ask Copilot to fix the null handling before merging.

This is a minor bug but could cause confusion when user toggles scale mode before viewer is fully loaded.