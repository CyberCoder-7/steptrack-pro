# StepTrack Pro

A production-oriented Android step counter app built with Kotlin, Jetpack Compose,
MVVM + Clean Architecture, Room, Hilt, and DataStore.

## How to open this project

1. Unzip the archive.
2. Open the `StepTrackPro` folder in **Android Studio (Koala or newer)**.
3. Let Gradle sync — it will download the Gradle 8.7 wrapper, AGP 8.5.2, Kotlin 1.9.24.
4. Run on a device or emulator with **API 26+**. Step-counter sensors are hardware-dependent —
   most emulators don't report `TYPE_STEP_COUNTER` events, so test the core sensor loop
   on a physical device when possible (the app is written to fail gracefully otherwise,
   showing an "no step-counter sensor" banner on the dashboard).

## Architecture

```
presentation/   Compose UI + ViewModels (MVVM), one package per screen
domain/         Pure Kotlin: models, repository interfaces, use cases (no Android deps)
data/           Room, DataStore, sensors, foreground service, repository implementations
di/             Hilt modules wiring data -> domain
notification/   Notification channel/builder helpers + WorkManager reminder worker
receiver/       Boot receiver to relaunch tracking after device reboot
util/           Pure calculation/date/permission helpers, unit-testable
```

This follows the classic Clean Architecture dependency rule: `presentation` and `data`
both depend on `domain`, but `domain` depends on nothing Android-specific — so the
use cases and step-math functions in `util/StepCalculationUtils.kt` can be unit tested
with plain JUnit, no Robolectric or instrumentation needed.

## Step counting design

- **`Sensor.TYPE_STEP_COUNTER`** is the primary source: a low-power hardware counter
  that reports cumulative steps *since the last device boot* (not since midnight).
- Because of that, `StepSensorManager` persists a **baseline** (the hardware value at
  the start of "today") in DataStore. `todaySteps = latestHardwareValue - baseline`.
- **Reboot handling**: if a new hardware reading is *lower* than the stored baseline,
  a reboot occurred (the counter reset to 0). The manager preserves already-counted
  steps for the day and re-baselines from the new value, so a reboot never erases
  progress.
- **`Sensor.TYPE_STEP_DETECTOR`** is a per-step event used as a fallback for devices
  where the counter is flaky or absent.
- **`StepCounterService`** is a foreground service (`FOREGROUND_SERVICE_TYPE_HEALTH`)
  that owns the sensor listener for as long as tracking is active, persists a snapshot
  to Room every 30 seconds (bounding data loss on process death), and drives the
  always-on tracking notification plus goal-progress / goal-achieved notifications.
- **`BootReceiver`** restarts the foreground service after `ACTION_BOOT_COMPLETED`.

## Known gaps / next steps before a Play Store submission

Closed out in this pass:

- ✅ **Battery-optimization exemption UX** — `BatteryOptimizationUtils` checks
  `PowerManager.isIgnoringBatteryOptimizations()` and can launch the direct
  `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` system dialog. The dashboard
  shows a dismissible-by-action banner when the app isn't exempted yet, and
  re-checks the status on `ON_RESUME` (so returning from the system dialog
  updates the UI immediately).
- ✅ **Reminder scheduling wired up** — `ReminderScheduler` (WorkManager
  `PeriodicWorkRequest`, correct initial-delay-to-next-occurrence math) is
  called from `SettingsViewModel` on toggle and reconciled on app cold start
  in `StepTrackApp.onCreate()`.
- ✅ **Room schema export** — `room.schemaLocation` is now configured via KSP
  args, so `app/schemas/` will contain versioned JSON schemas from the next
  build, which real `Migration` objects can diff against.
- ✅ **Unit + instrumentation tests** — pure-Kotlin `domain`/`util` logic is
  covered by JUnit tests (`src/test`), and Room DAO behavior (upsert-by-date,
  range queries, lifetime sum, pruning) is covered by an in-memory-database
  instrumentation test (`src/androidTest`).

Still open:

- **Room migrations**: still on `fallbackToDestructiveMigration()` since
  there's only ever been schema v1 so far — write a real `Migration(1, 2)`
  the first time a column changes, using the exported schema JSON as the
  starting point.
- **App icon / branding**: placeholder adaptive icon included; swap in real
  brand assets before release.
- **Play Store metadata**: privacy policy (required for `ACTIVITY_RECOGNITION`
  and health-adjacent data), store listing, and a data-safety form declaring
  on-device-only step/health data.
- **UI/Compose tests**: current instrumentation coverage is data-layer only;
  add `createAndroidComposeRule` tests for the Dashboard/Settings screens
  once the visual design is finalized (Compose UI tests are more brittle to
  churn than logic tests, so worth deferring until the UI stabilizes).
