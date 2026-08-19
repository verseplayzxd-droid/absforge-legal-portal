# AbsForge — 30 Day Abs Workout

A complete, native Android fitness application built with modern Android architecture to deliver a premium, 30-day core workout experience.

---

## 🏋️ Application Architecture & Tech Stack

- **Language**: Kotlin 2.1.20
- **UI Framework**: Jetpack Compose with Material 3 foundations
- **SDK Target**: `compileSdk = 36`, `targetSdk = 36`, `minSdk = 26`
- **Architecture**: MVVM + Repository Pattern with Clean Architecture
- **Database**: Room 2.7.1 for persistent local storage
- **Preferences**: DataStore 1.1.7 for user settings and program progression
- **Background Tasks**: WorkManager 2.10.1 for daily reminder notifications
- **Audio & Guidance**: TextToSpeech Android API + System Haptics
- **Exercise Animations**: Articulated Compose Canvas human figure system with muscle highlights

---

## 📂 Project Structure

```
absforge/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/absforge/
│   │   │   │   ├── ads/              # AdsManager & DebugAdsProvider
│   │   │   │   ├── audio/            # VoiceCoachManager & HapticManager
│   │   │   │   ├── billing/          # BillingManager & DebugBillingProvider
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/        # Room Database, Entities (13), DAOs (11)
│   │   │   │   │   ├── preferences/  # DataStore PreferencesManager
│   │   │   │   │   └── seed/         # 33 Seeded Exercises & 3 Full 30-Day Plans
│   │   │   │   ├── notifications/    # WorkManager Workers, Scheduler & BootReceiver
│   │   │   │   ├── ui/
│   │   │   │   │   ├── animation/    # Articulated Canvas Exercise Animation System
│   │   │   │   │   ├── components/   # AbsForge UI Design Components
│   │   │   │   │   ├── exercises/    # Exercise Library & Details Screens
│   │   │   │   │   ├── home/         # Dashboard & Quick Workouts
│   │   │   │   │   ├── navigation/   # Jetpack Navigation Host & Bottom Nav Bar
│   │   │   │   │   ├── onboarding/   # 6-Step Personalization Flow
│   │   │   │   │   ├── profile/      # Profile, Settings, Achievements, Premium, Privacy
│   │   │   │   │   ├── progress/     # Activity Analytics & Body Measurement Tracking
│   │   │   │   │   ├── theme/        # "Void and Spark" Dark Theme & Typography
│   │   │   │   │   └── workout/      # 30-Day Program Grid, Overview, Player Engine & Rest Timer
│   │   │   │   └── utils/            # CalorieCalculator & DateUtils
│   │   │   └── res/                  # App resources, dark theme XMLs & adaptive icon
│   │   └── test/                     # Unit tests for calories, streak, and 90% completion logic
│   └── build.gradle.kts              # App module build config (API 36)
├── build.gradle.kts                  # Root build config
└── settings.gradle.kts               # Dependency resolution & settings
```

---

## 🛠️ How to Build

### Requirements
- **JDK**: Java 17 or JDK 21 (bundled JDK recommended)
- **Android SDK**: API Level 36 (`android-36`)
- **Gradle**: 8.14.2 (wrapper included)

### Build Commands

#### Debug APK
```bash
./gradlew assembleDebug
```
Output location: `app/build/outputs/apk/debug/app-debug.apk`

#### Release App Bundle (.aab)
```bash
./gradlew bundleRelease
```
Output location: `app/build/outputs/bundle/release/app-release.aab`

#### Run Unit Tests
```bash
./gradlew test
```

---

## 💡 Key Features & Systems

### 1. 30-Day Program Engine
- 3 Progressive Tracks: **Beginner**, **Intermediate**, **Advanced**
- Real day-by-day progression across all 30 days with integrated recovery days
- Day states: Completed (Lime check), Current (Lime glow), Available, Locked, Rest

### 2. Workout Player & Active Exercise
- Real coroutine-driven timer system (pausable, lifecycle-aware)
- Pre-exercise animated countdown (3-2-1-GO!)
- Articulated Canvas Human Silhouette animations for all 33 exercises
- Rest timer with +20 Seconds extension and Skip support

### 3. Data Safety & Persistence
- Fully offline-first architecture with Room local database
- DataStore preference persistence for settings and active progress
- Privacy option to delete all user data from Profile

---

## 🔑 External Credentials & SDK Dependencies (For Production Release)

The project includes modular abstractions for Ads and Google Play Billing:

1. **AdsManager (`com.absforge.ads.AdsManager`)**:
   - Currently uses `DebugAdsProvider` (no real ad requests).
   - *To connect AdMob*: Replace `DebugAdsProvider` with your AdMob SDK implementation and add `com.google.android.gms.ads.APPLICATION_ID` to `AndroidManifest.xml`.

2. **BillingManager (`com.absforge.billing.BillingManager`)**:
   - Currently uses `DebugBillingProvider` (demo subscription flow).
   - *To connect Google Play Billing*: Implement the Play Billing Library v6+ inside `BillingManagerImpl` with your product IDs (`absforge_monthly`, `absforge_yearly`, `absforge_lifetime`).
