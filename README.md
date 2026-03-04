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

### ✅ Completed

#### 📐 Perfect Viewer Reading Modes
| Mode | Description | Status |
|------|-------------|--------|
| **Fit Screen** | Fit the entire page to the screen (default) | ✅ |
| **Fit Width** | Stretch page width to fill screen width | ✅ |
| **Fit Height** | Stretch page height to fill screen height | ✅ |
| **Original Size** | Display at native resolution — no scaling | ✅ |
| **Smart Fit** | Auto-adjust based on image orientation | ✅ |

- Per-manga scale mode persistence
- UI toggle in reader toolbar

#### 🖼️ Full Chapter Gallery Preview
- ✅ Full-screen grid gallery (2/3/4 columns)
- ✅ Adjustable thumbnail sizes (S/M/L)
- ✅ Tap any page to jump instantly
- ✅ Page number overlays
- ✅ Remembers column/size preferences

#### 🖼️ Gallery View (Thumbnail Strip)
- ✅ Perfect Viewer-style thumbnail strip
- ✅ Tap to jump to any page
- ✅ Page number overlays
- ✅ Auto-hide functionality

#### 🤖 AI Recommendations
- ✅ "For You" tab with personalized suggestions
- ✅ "Because you read X" section
- ✅ Tag-based matching algorithm
- ✅ Reading history tracking
- ✅ Weekly background updates

#### ⏱️ Reading Time Estimation
- ✅ Estimates time remaining in chapter
- ✅ Learns from your reading speed
- ✅ Shows "5 min remaining" in reader

#### 📊 Reading Statistics Dashboard
- ✅ Total chapters read counter
- ✅ Reading streak tracking
- ✅ Weekly activity heatmap
- ✅ Top manga by reading time
- ✅ Total reading time stats

#### ⚙️ Per-Series Reader Presets
- ✅ Per-manga reading direction
- ✅ Per-manga scale mode
- ✅ Per-manga background color
- ✅ Per-manga brightness settings
- ✅ Reset to defaults option

#### 🔍 Source Health Dashboard
- ✅ Visual health indicators (Green/Yellow/Red)
- ✅ Success/failure rate tracking
- ✅ Last successful fetch timestamp
- ✅ Auto-detects broken sources

#### 📦 Export Downloads to CBZ
- ✅ Export chapters as CBZ (comic book format)
- ✅ Export entire manga series
- ✅ Progress indicator for large exports
- ✅ Compatible with standard comic readers

### ✅ Phase 4: Dynamic Theming
- ✅ Extract colors from manga covers
- ✅ Apply to app bars, navigation, accents
- ✅ Theme modes: Vibrant, Fidelity, and more
- ✅ Reader background theming
- ✅ Toggle in settings

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
└── docs/                   # Documentation & planning
```

---

## 🤝 Contributing

Contributions are warmly welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting a pull request.

---

## 📜 License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](LICENSE) file for details.

---

## 💖 Credits

- [Komikku](https://github.com/komikku-app/komikku) by the Komikku team — the upstream project
- [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi) — the original open-source manga reader
- [Perfect Viewer](https://play.google.com/store/apps/details?id=com.rookiestudio.perfectviewer) — inspiration for advanced reading modes
- All upstream contributors and translators

---

_Made with ❤️ for manga readers everywhere_
