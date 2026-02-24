<div align="center">

```
██╗  ██╗ ██████╗ ███╗   ███╗██╗██╗  ██╗██╗  ██╗██╗   ██╗
██║ ██╔╝██╔═══██╗████╗ ████║██║██║ ██╔╝██║ ██╔╝██║   ██║
█████╔╝ ██║   ██║██╔████╔██║██║█████╔╝ █████╔╝ ██║   ██║
██╔═██╗ ██║   ██║██║╚██╔╝██║██║██╔═██╗ ██╔═██╗ ██║   ██║
██║  ██╗╚██████╔╝██║ ╚═╝ ██║██║██║  ██╗██║  ██╗╚██████╔╝
╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝
                        2 0 2 6
```

**✦ 漫画 · An Enhanced Manga Reader for Android ✦**

[![Build](https://img.shields.io/github/actions/workflow/status/Heartless-Veteran/komikku-2026/build_pull_request.yml?branch=main&style=for-the-badge&logo=android&logoColor=white&color=7c3aed&labelColor=1a0533)](https://github.com/Heartless-Veteran/komikku-2026/actions)
[![Version](https://img.shields.io/badge/version-0.1.0--alpha-ec4899?style=for-the-badge&logo=semantic-release&logoColor=white&labelColor=1a0533)](https://github.com/Heartless-Veteran/komikku-2026/releases)
[![License](https://img.shields.io/badge/license-Apache%202.0-a855f7?style=for-the-badge&logo=apache&logoColor=white&labelColor=1a0533)](LICENSE)
[![Android](https://img.shields.io/badge/Android-5.0%2B-34d399?style=for-the-badge&logo=android&logoColor=white&labelColor=1a0533)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-f97316?style=for-the-badge&logo=kotlin&logoColor=white&labelColor=1a0533)](https://kotlinlang.org)

*A fork of [Komikku](https://github.com/komikku-app/komikku) with enhanced reading modes, AI-powered recommendations, gallery browsing, and dynamic theming — in active development.*

</div>

---

## 📖 Table of Contents

- [✨ Features](#-features)
- [📚 Core Features](#-core-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [🚀 Building](#-building)
- [📁 Project Structure](#-project-structure)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)
- [💖 Credits](#-credits)

---

## ✨ Features

### 🚧 In Development

> These features are actively being built. Status is tracked in [`docs/PROJECT_PLAN.md`](docs/PROJECT_PLAN.md).

#### 📐 Perfect Viewer Reading Modes

| Mode | Description |
|------|-------------|
| **Fit Screen** | Fit the entire page to the screen (default) |
| **Fit Width** | Stretch page width to fill screen width |
| **Fit Height** | Stretch page height to fill screen height |
| **Original Size** | Display at native resolution — no scaling |
| **Smart Crop** | Automatically crop white/black margins |

#### 🤖 AI Recommendations
- Personalised suggestions based on your reading history
- Similar manga finder powered by tag-based matching
- Curated picks that adapt as your library grows

### 📅 Planned

#### 🖼️ Gallery View
> *See the whole chapter at a glance.*

- Grid thumbnail view of all pages in a chapter
- Tap any thumbnail to jump directly to that page
- Fast, smooth scrolling with lazy-loaded previews

#### 🎨 Dynamic Theming
> *Let the manga set the mood.*

- Automatically extracts colour palette from the manga cover
- Applies accent colours to the reader background and UI elements
- Seamless, immersive visual experience per title

---

## 📚 Core Features

Inherited from the upstream [Komikku](https://github.com/komikku-app/komikku) project:

- 🔌 **Extension System** — hundreds of manga sources via extensions
- 📂 **Library Management** — organise and track your reading list
- 💾 **Offline Downloads** — download chapters to read without internet
- 📖 **Local Manga** — read manga stored on your device
- 🔖 **Reading History & Bookmarks**
- 🌐 **Webtoon & Pager Modes**
- ⚙️ **Rich Settings** — granular control over every aspect of the app

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| [Kotlin](https://kotlinlang.org) | Primary language (100%) |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative UI toolkit |
| [SQLDelight](https://cashapp.github.io/sqldelight/) | Type-safe SQL database |
| [Coil](https://coil-kt.github.io/coil/) | Image loading & caching |
| [Android Palette API](https://developer.android.com/develop/ui/views/graphics/palette-colors) | Dynamic colour extraction |

---

## 🚀 Building

### Prerequisites

- Android Studio Iguana (2023.2.1) or later
- JDK 17+
- Android SDK 35

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

### Running Tests

```bash
./gradlew test
```

---

## 📁 Project Structure

```
komikku-2026/
├── app/                    # Main Android application module
├── core/                   # Core utilities and shared logic
├── core-metadata/          # Metadata handling
├── data/                   # Data layer (repositories, DAOs)
├── domain/                 # Domain layer (use cases, models)
├── presentation-core/      # Shared Compose UI components
├── presentation-widget/    # Home screen widget
├── source-api/             # Extension/source API definitions
├── source-local/           # Local file source implementation
├── i18n/                   # Translations (upstream)
├── i18n-kmk/               # Komikku-specific translations
├── docs/                   # Documentation & planning
│   ├── DEPENDENCY_MANAGEMENT.md
│   ├── FEATURE_DYNAMIC_THEME.md
│   ├── FEATURE_GALLERY_VIEW.md
│   ├── FEATURE_READING_MODES.md
│   ├── FEATURE_RECOMMENDATIONS.md
│   ├── PROJECT_PLAN.md
│   ├── SCALE_MODES_IMPLEMENTATION.md
│   ├── SECURITY_VULNERABILITIES.md
│   └── TEST_SUMMARY.md
└── .github/                # CI/CD workflows and templates
```

---

## 🤝 Contributing

Contributions are warmly welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting a pull request, and make sure to follow our [Code of Conduct](CODE_OF_CONDUCT.md).

For security issues, please refer to [SECURITY.md](SECURITY.md).

---

## 📜 License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](LICENSE) file for details.

---

## 💖 Credits

- [Komikku](https://github.com/komikku-app/komikku) by the Komikku team — the upstream project this fork is based on
- [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi) — the original open-source manga reader
- Perfect Viewer — inspiration for the advanced reading modes
- All upstream contributors and translators

---

<div align="center">

*Made with ❤️ for manga readers everywhere*

</div>
