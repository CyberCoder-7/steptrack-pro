# Try the App Without Installing Android Studio

You still need *something* to turn the source code into an installable file
— that step can't be skipped — but it doesn't have to happen on your own
computer. This guide uses **GitHub Actions**, a free cloud build service, to
do the compiling for you. You end up with an `app-debug.apk` file you install
directly on your phone.

This produces a **debug build** — perfect for trying the app yourself, but
not the signed release build the Play Store requires later (that step still
needs `RELEASE_SIGNING.md` and eventually a real build environment).

## Step 1 — Create a free GitHub account

Go to [github.com](https://github.com) and sign up, if you don't already have
an account. No payment needed.

## Step 2 — Create a new repository

- Click the **+** in the top-right → **New repository**.
- Name it anything, e.g. `steptrack-pro`.
- Leave it **Public** (simplest) or **Private** — either works.
- Don't check any of the "initialize with README" boxes.
- Click **Create repository**.

## Step 3 — Upload the project

On the new repo's page, click **uploading an existing file** (or **Add file
→ Upload files**).

- Unzip `StepTrackPro.zip` on your computer first.
- Drag the **entire contents** of the unzipped `StepTrackPro` folder
  (not the folder itself — its contents: `app`, `gradle`, `build.gradle.kts`,
  `.github`, etc.) into the upload box.
- Scroll down, click **Commit changes**.

If the drag-and-drop feels unreliable for nested folders in your browser,
installing **GitHub Desktop** (a much lighter app than Android Studio) and
using its "Add existing repository → Publish" flow is a more reliable
alternative — still no Android tooling required.

## Step 4 — Let it build automatically

Uploading to the `main` branch automatically triggers the build. Click the
**Actions** tab at the top of your repo — you'll see a run in progress
("Build Debug APK"). It takes roughly 3–6 minutes.

If you don't see a run start, click **Actions → Build Debug APK → Run
workflow → Run workflow** to trigger it manually.

## Step 5 — Download the APK

Once the run shows a green checkmark:

- Click into that run.
- Scroll to the **Artifacts** section at the bottom.
- Click **steptrack-pro-debug-apk** to download a zip.
- Unzip it — inside is `app-debug.apk`.

## Step 6 — Get the APK onto your phone

Any transfer method works:
- Email it to yourself and open the attachment on your phone, or
- Upload it to Google Drive/Dropbox and download it on your phone, or
- Plug your phone into your computer via USB and copy it over.

## Step 7 — Install it

Android blocks installs from outside the Play Store by default:

1. Tap the downloaded `app-debug.apk` file on your phone (in Files, Gmail,
   Drive — wherever you saved it).
2. Android will prompt: *"For your security, your phone isn't allowed to
   install unknown apps from this source."* Tap **Settings**, then toggle
   **Allow from this source** for whichever app you opened it with.
3. Go back and tap the APK file again → **Install**.

## Step 8 — Try it

Open the app, go through onboarding, grant the Activity Recognition and
Notification permissions when asked, and walk around with your phone to
confirm steps are counting. Lock the screen and check the notification still
updates.

## If the build fails

Click the failed run in the **Actions** tab, open the **Build debug APK**
step, and copy the error text — paste it back to me and I'll fix the
underlying code. This is the same kind of error an Android Studio sync would
have caught, just surfaced in the cloud instead.

## An alternative if you'd rather not use GitHub at all

Advanced option: install **Termux** from F-Droid (not the Play Store version,
which is outdated) directly on your Android phone, then install a JDK and
the Android command-line SDK tools inside it to run `gradle assembleDebug`
entirely on-device, no computer or GitHub needed. This is more technical and
uses more phone storage/battery for the build — reasonable if you're
comfortable with a terminal, otherwise the GitHub Actions path above is
easier.
