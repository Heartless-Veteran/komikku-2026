# GitHub Copilot Instructions for Komikku 2026

## Project Overview

Komikku 2026 is an Android manga reader fork built with:
- **Language:** Kotlin 100%
- **UI:** Jetpack Compose
- **Architecture:** MVVM + Clean Architecture
- **DI:** Injekt (Kotlin-native, not Hilt/Dagger)
- **Database:** SQLDelight (type-safe SQL)
- **Images:** Coil
- **Navigation:** Voyager

## Module Structure

```
komikku-2026/
├── app/                    # Main UI, Activities, ViewModels
│   ├── src/main/java/eu/kanade/
│   │   ├── data/          # Repositories, database
│   │   ├── domain/        # Use cases, models
│   │   ├── presentation/  # Compose screens, components
│   │   └── tachiyomi/     # Core app logic
├── core/                   # Shared utilities
├── data/                   # SQLDelight schemas
├── domain/                 # Domain models
├── i18n/                   # Localization (upstream)
├── i18n-kmk/              # Komikku-specific strings
├── presentation-core/      # Shared UI components
└── source-api/            # Extension API
```

## Coding Conventions

### Kotlin Style
- Use **trailing commas** in multi-line lists/parameters
- Prefer **expression bodies** for single-line functions
- Use **named arguments** for boolean parameters
- **No wildcard imports** — expand them explicitly

### Compose Patterns
```kotlin
// State hoisting pattern
@Composable
fun MyScreen(
    state: MyState,
    onEvent: (MyEvent) -> Unit,
    modifier: Modifier = Modifier,  // Always last parameter
)

// Collect state properly
val state by viewModel.state.collectAsState()

// Use Flow for reactive data
fun getData(): Flow<List<Data>>
```

### Dependency Injection (Injekt)
```kotlin
// Constructor injection
class MyViewModel(
    private val getManga: GetManga,
    private val preferences: ReaderPreferences,
) : ViewModel() {
    // ...
}

// Inject in Composable
val viewModel: MyViewModel = viewModel()
```

## Key Patterns

### Database (SQLDelight)
```kotlin
// Use generated classes only
val manga: Manga = queries.selectById(id).executeAsOne()

// Updates through repository
updateManga.executeSync(mangaUpdate)
```

### Preferences
```kotlin
// In ReaderPreferences.kt
fun mySetting() = preferenceStore.getBoolean("pref_key", defaultValue)

// Usage
val enabled by readerPreferences.mySetting().collectAsState()
```

### String Resources (Moko)
```xml
<!-- In i18n-kmk -->
<string name="my_string">My Text</string>
```
```kotlin
// Usage in Compose
text = stringResource(KMR.strings.my_string)
```

## KMK (Komikku) Markers

Always mark Komikku-specific changes:
```kotlin
// KMK -->
val myNewFeature = true
// KMK <--
```

## Common Tasks

### Adding a New Feature
1. Create use case in `domain/` if needed
2. Add repository method in `data/`
3. Create UI in `presentation/`
4. Add strings to `i18n-kmk/`
5. Update ViewModel in `app/`

### Adding a Setting
1. Add preference to `ReaderPreferences.kt`
2. Add UI to settings screen
3. Add string resource
4. Use in appropriate screen/ViewModel

### Adding UI Component
1. Create in `presentation/` module
2. Use Material3 components
3. Support dark/light theme
4. Add previews for Compose
5. Handle accessibility (content descriptions)

## File Naming

- **Screens:** `FeatureScreen.kt`
- **ViewModels:** `FeatureViewModel.kt`
- **Components:** `FeatureComponent.kt`
- **Repositories:** `FeatureRepository.kt`
- **Use Cases:** `GetFeature.kt`, `UpdateFeature.kt`

## Testing

- Unit tests in `src/test/`
- Use `kotlinx.coroutines.test` for coroutines
- Mock dependencies with Mockito
- UI tests in `src/androidTest/` (rarely used)

## Things to Avoid

❌ **Don't:**
- Use Android `Log` — use `logcat` from `tachiyomi.core`
- Use `!!` — handle nulls properly
- Use `GlobalScope` — use `viewModelScope` or `lifecycleScope`
- Add new dependencies without checking existing ones
- Modify upstream files without `KMK` markers
- Use Java collections — use Kotlin collections

✅ **Do:**
- Use `Flow` for reactive data
- Use `Result` type for operations that can fail
- Handle errors gracefully with `try/catch`
- Use `remember` and `derivedStateOf` appropriately in Compose
- Follow existing patterns in the codebase

## Project-Specific Context

### Reader Architecture
- `ReaderActivity` hosts the reader
- `ReaderViewModel` manages state
- `Viewer` subclasses handle rendering (Pager/Webtoon)
- Pages loaded via `PageLoader`

### Extension System
- Extensions are separate APKs
- Implement `Source` interface
- Use `HttpSource` for web-based sources

### Download System
- Downloads managed by `DownloadManager`
- Stored in configurable location
- Chapters are folders of images

## When Generating Code

1. Check existing similar implementations first
2. Follow the module structure (don't put UI in domain)
3. Use existing utilities (don't reinvent)
4. Add proper null handling
5. Include KMK markers for new features
6. Add string resources for user-facing text
7. Consider both light and dark themes
8. Handle configuration changes properly

## Useful Extension Functions

```kotlin
// From tachiyomi.core
fun CoroutineScope.launchIO(block: suspend () -> Unit)
fun CoroutineScope.launchUI(block: suspend () -> Unit)

// Flow operators
fun <T> Flow<T>.asState(): StateFlow<T>
```

## Questions?

Check existing implementations in:
- `ReaderActivity.kt` for reader patterns
- `LibraryScreen.kt` for list patterns
- `ReaderPreferences.kt` for settings patterns
