# Play Console "Data Safety" Form — Answers for StepTrack Pro

Google requires this form to match your actual code, and mismatches can get
listings rejected or pulled. Based on how this codebase behaves as shipped:

## Does your app collect or share any of the required user data types?

**Collects:** Yes (health & fitness data, personal info you enter).
**Shares with third parties:** No — there are no analytics SDKs, ad SDKs, or
network calls anywhere in this codebase.

## Data types to declare

| Category | Type | Collected? | Shared? | Purpose |
|---|---|---|---|---|
| Health and fitness | Fitness info (steps, distance, calories, activity) | Yes | No | App functionality |
| Personal info | Name | Yes (optional, user-entered) | No | App functionality (personalization) |
| Personal info | Age | Yes (optional, user-entered) | No | App functionality (calorie calc) |
| Personal info | Other (height, weight, gender) | Yes (optional, user-entered) | No | App functionality (distance/calorie calc) |

## Is data encrypted in transit?

Mark **"Data isn't transmitted off the device."** — this app has no network
stack; Room and DataStore both write to local, app-private storage.

## Can users request data deletion?

Yes — clarify in the form that **uninstalling the app deletes all data**,
since there's no backend account to separately delete data from. If you add
Android's "Auto backup" or any cloud sync later, revisit this answer.

## Is data collection required or optional?

All of it is optional in the sense that profile fields can be left blank
(defaults are used), and the app doesn't force account creation. Step data
itself is core to the app's function and is collected automatically once the
person grants the Activity Recognition permission.

## Before you submit

- [ ] Host `PRIVACY_POLICY.md`'s content at a real, publicly reachable URL
      (GitHub Pages, a simple static site, Notion public page, etc.) — Play
      Console requires a live link, not a file upload.
- [ ] Re-check this table against the actual shipped build if you add any
      SDK (crash reporting, analytics, ads) before release — those all
      change the answers above.
