# 🤝 Contributing to Komikku 2026

Thank you for your interest in contributing to Komikku 2026! This document outlines how to get involved.

---

## 📋 Table of Contents

- [Reporting Issues & Feature Requests](#-reporting-issues--feature-requests)
- [Code Contributions](#-code-contributions)
- [Prerequisites](#-prerequisites)
- [Development Setup](#-development-setup)
- [Pull Request Guidelines](#-pull-request-guidelines)
- [Translations](#-translations)
- [Forks](#-forks)

---

## 🐞 Reporting Issues & Feature Requests

Before opening an issue, please:

1. Search [existing issues](https://github.com/Heartless-Veteran/komikku-2026/issues) to avoid duplicates
2. Use the appropriate issue template
3. Include as much detail as possible

For security vulnerabilities, please follow the [Security Policy](SECURITY.md) instead of opening a public issue.

---

## 💻 Code Contributions

Pull requests are welcome!

If you're interested in taking on [an open issue](https://github.com/Heartless-Veteran/komikku-2026/issues), please comment on it so others are aware. You do not need to ask for permission or an assignment.

---

## 🔧 Prerequisites

Before you start, the following skills and tools are **required**. Existing contributors will not actively teach them, but there are many great resources online.

**Knowledge:**
- Basic [Android development](https://developer.android.com/)
- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

**Tools:**
- [Android Studio](https://developer.android.com/studio) (Iguana 2023.2.1 or later)
- Android emulator or a physical device with developer options enabled

---

## ⚙️ Development Setup

1. **Fork** the repository and clone your fork
2. Open the project in Android Studio
3. Let Gradle sync complete
4. Build and run on your device or emulator:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📝 Pull Request Guidelines

- **One feature or fix per PR** — keep PRs focused and easy to review
- **Write a clear description** — explain what changed and why
- **Test your changes** — run the app and verify your changes work
- **Follow existing code style** — match the patterns already in the codebase
- **Update documentation** if your change affects user-facing behaviour

### Supporting Cloud Sync — Google Drive

If your PR involves Google Drive cloud sync, you will need to set up OAuth credentials:

1. Go to [Google Cloud Console](https://console.cloud.google.com) and create a new project
2. Enable the **Google Drive API** under *API & Services → Library*
3. Configure the OAuth consent screen with app name and contact info
4. Add the `.../auth/drive.appdata` and `.../auth/drive.file` scopes
5. Create an OAuth Client ID for Android with package name `eu.kanade.google.oauth`
6. Retrieve the SHA-1 fingerprint: `keytool -printcert -jarfile app-standard-universal-release.apk`
7. Download the JSON, rename it to `client_secrets.json`, and place it in `app/src/main/assets/`

---

## 🌐 Translations

Translations for the upstream Komikku components are managed via [Weblate](https://hosted.weblate.org/engage/komikku-app/).
Komikku 2026–specific strings may be contributed directly via pull request.

---

## 🍴 Forks

Forks are permitted under the terms of the [Apache 2.0 License](LICENSE).

When creating your own fork, please:

- **Avoid confusion with this project:**
  - Change the app name and icon
  - Disable or update the app update checker
- **Avoid installation conflicts:**
  - Change the `applicationId` in `app/build.gradle.kts`
- **Avoid data pollution:**
  - Replace `google-services.json` with your own Firebase config if using Firebase
  - Update the ACRA crash reporting endpoint in `app/build.gradle.kts`
