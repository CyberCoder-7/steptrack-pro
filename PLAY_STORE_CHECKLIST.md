# Play Store Submission Checklist

Read this before uploading. Items marked **(cannot verify here)** are things
I could not confirm because I don't have an Android SDK/Gradle/emulator
available in this environment — I've verified the code is structurally sound
(package/import consistency, balanced braces across all 75 Kotlin files,
no duplicate DI bindings) but it has never actually been compiled by a real
Gradle+AGP+Kotlin toolchain. **Your first Android Studio sync is the real
test.** If it throws errors, paste them back to me and I'll fix them.

## 1. Build it — first, for real

- [ ] Open in Android Studio (Koala/2024.1 or newer), let Gradle sync fully.
- [ ] **(cannot verify here)** Fix any compile errors that surface — version
      mismatches between AGP 8.5.2 / Kotlin 1.9.24 / Compose compiler
      1.5.14 are the most likely source if your installed Android
      Studio ships different defaults; bump versions together if needed.
- [ ] Run on a **physical device** — emulators frequently don't emit
      `TYPE_STEP_COUNTER` events, so the core feature is best confirmed on
      real hardware. Walk around with the app open and locked to confirm
      the notification updates and steps persist after reopening.
- [ ] Reboot the test device and confirm tracking resumes (tests
      `BootReceiver` + the sensor baseline-reset logic).

## 2. Identity & versioning

- [ ] Change `applicationId` in `app/build.gradle.kts` from `com.steptrack.pro`
      to a package name **you actually own** (Play Store requires globally
      unique application IDs, and you can never change it after first
      publish).
- [ ] Replace the placeholder adaptive icon
      (`app/src/main/res/drawable/ic_launcher_*.xml`) with real brand
      artwork — run it through Android Studio's Image Asset tool to
      generate all densities correctly.
- [ ] Confirm `versionCode`/`versionName` in `app/build.gradle.kts` before
      each upload — Play Console rejects re-uploading the same `versionCode`.

## 3. Signing

- [ ] Follow `RELEASE_SIGNING.md` to generate a keystore and build a signed
      `.aab` via `./gradlew bundleRelease`.
- [ ] Opt into **Play App Signing** on first upload (recommended — see the
      end of `RELEASE_SIGNING.md` for why).

## 4. Store listing content (Play Console → your app)

- [ ] Host `PRIVACY_POLICY.md` at a real public URL and link it in
      **App content → Privacy policy**. Fill in the bracketed placeholders
      (date, contact email) first.
- [ ] Complete **App content → Data safety** using `DATA_SAFETY_FORM_GUIDE.md`.
- [ ] Complete **App content → Health apps declaration form** — this app
      requests `FOREGROUND_SERVICE_HEALTH` and reads fitness sensor data,
      which puts it in Google's "Health" policy category and requires this
      separate declaration (distinct from the general Data Safety form).
- [ ] Complete the standard **App content** sections: target audience/age
      (this app has no age-gating logic, so mark it general audience unless
      you add restrictions), ads declaration (mark **no ads** — there are
      none in this codebase), and content rating questionnaire.
- [ ] Write the actual store listing: short/long description, screenshots
      (minimum 2, per Play's current spec — capture these from a real
      device run since I can't generate authentic screenshots here),
      feature graphic, app icon.

## 5. Permissions review

- [ ] `ACTIVITY_RECOGNITION` and `POST_NOTIFICATIONS` are requested at
      runtime (`MainActivity`) — confirm the permission-rationale UX feels
      right on a real device; Android's default system dialogs are used
      as-is with no custom rationale screen currently.
- [ ] Play Console may flag `FOREGROUND_SERVICE_HEALTH` for manual review —
      this is expected for a legitimate fitness-tracking foreground
      service; the Health apps declaration form (above) is what satisfies it.

## 6. Known code-level follow-ups (see README.md for detail)

- [ ] Real Room `Migration` objects before your first schema change
      (currently `fallbackToDestructiveMigration()`, acceptable only for v1).
- [ ] Compose UI tests (only data-layer instrumentation tests exist today).

## 7. Final pre-submit smoke test

- [ ] Fresh install → onboarding → grant permissions → walk → confirm steps
      update, notification shows correct count, and force-closing the app
      doesn't lose more than ~30s of steps (the periodic-persist window).
- [ ] Toggle every Settings option once and confirm it takes effect
      (goal, units, theme, both reminder toggles, stride calibration).
