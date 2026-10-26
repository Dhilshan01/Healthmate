# HealthMate – Personal Health & Wellness Tracker

Native Android app built with Kotlin, Jetpack Compose (Material 3), MVVM, StateFlow and SQLite (`SQLiteOpenHelper`).

## Requirements
- Android Studio (latest stable, Narwhal or newer)
- JDK 17 (bundled with Android Studio: *Settings > Build Tools > Gradle > Gradle JDK = jbr-17*)
- Android SDK Platform 36 (install from *Tools > SDK Manager*)
- Device or emulator running Android 8.0 (API 26) or newer

## Run
1. *File > Open* and select this `HealthMate-Android` folder (the one containing `settings.gradle.kts`).
2. Wait for Gradle sync to finish.
3. Pick a device/emulator and press **Run**.

## Release APK
*Build > Generate Signed App Bundle / APK > APK*, or run `./gradlew assembleRelease`
(the release type currently signs with the debug key, so no keystore is needed).
Output: `app/build/outputs/apk/release/app-release.apk`

## Structure
```
app/src/main/kotlin/com/example/healthmate
├── MainActivity.kt
├── core
│   ├── design/Theme.kt                      colours + Material 3 theme
│   └── notifications/ReminderReceiver.kt    AlarmManager scheduler + receiver
└── feature/records
    ├── data/HealthData.kt                   models, SQLite helper, repository
    └── presentation
        ├── HealthViewModel.kt               UI state (StateFlow)
        └── HealthMateApp.kt                 navigation + all Compose screens
```
