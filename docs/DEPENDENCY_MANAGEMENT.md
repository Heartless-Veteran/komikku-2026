# Dependency Management

## Overview
This project uses Gradle with Kotlin DSL for dependency management.

## Key Files
- `build.gradle.kts` - Root build configuration
- `app/build.gradle.kts` - App module dependencies
- `gradle/libs.versions.toml` - Version catalog (if using)

## Security Scanning

### Mend (WhiteSource)
- **Scan Schedule:** Weekly (Sundays)
- **Trigger:** Push to master, PRs, manual
- **Report:** Dependency vulnerability report

### GitHub Dependabot
Enabled for automatic dependency updates.

## Updating Dependencies

### Check for updates
```bash
./gradlew dependencyUpdates
```

### Update specific dependency
Edit `build.gradle.kts` or version catalog.

### Update all dependencies
1. Check Mend/Dependabot reports
2. Update versions in configuration
3. Test build
4. Commit changes

## Vulnerability Response

1. **Critical:** Update immediately
2. **High:** Update within 1 week
3. **Medium/Low:** Update next release cycle

## Approved Dependencies

Major dependencies used:
- Kotlin Coroutines
- Jetpack Compose
- Coil (image loading)
- OkHttp (networking)
- SQLDelight (database)

See `app/build.gradle.kts` for full list.