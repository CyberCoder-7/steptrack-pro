# Release Signing

The Play Store requires every release build to be signed with a key you
control. This project is wired to pick up signing credentials from Gradle
properties so the actual keystore/passwords never live in source control.

## 1. Generate a keystore (once, keep it forever)

Run this from a machine with a JDK installed (Android Studio bundles one):

```bash
keytool -genkeypair -v \
  -keystore steptrack-release.jks \
  -alias steptrack \
  -keyalg RSA -keysize 2048 -validity 10000
```

You'll be prompted for a store password, a key password, and identity details
(name/org/country — these go into the certificate, not the app).

**Store this file somewhere safe and back it up.** If you lose it, you can
never update the app under the same Play Store listing again — Google cannot
recover it for you, and re-signing with a new key means publishing as a brand
new app with zero existing installs/reviews.

## 2. Point Gradle at it

In `gradle.properties` (or better, in `~/.gradle/gradle.properties` so it's
never even in the project folder), set:

```properties
STEPTRACK_RELEASE_STORE_FILE=/absolute/path/to/steptrack-release.jks
STEPTRACK_RELEASE_STORE_PASSWORD=your-store-password
STEPTRACK_RELEASE_KEY_ALIAS=steptrack
STEPTRACK_RELEASE_KEY_PASSWORD=your-key-password
```

`app/build.gradle.kts` already reads these four properties and applies them
to the `release` build type automatically — no other changes needed.

## 3. Build the release artifact

Play Store submissions require an **Android App Bundle**, not a raw APK:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## 4. (Recommended) Enroll in Play App Signing

When you create the app in Play Console and upload your first `.aab`, opt
into **Play App Signing**. Google then re-signs your bundle with its own key
for distribution while keeping your upload key as a secondary credential —
this means a lost upload key is *recoverable* through Play Console's key
reset process, unlike a fully self-managed key.
