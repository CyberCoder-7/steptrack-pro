package com.steptrack.pro.util

object Constants {
    const val DEFAULT_DAILY_GOAL = 10_000
    const val DEFAULT_STRIDE_METERS = 0.75
    const val CALORIES_PER_STEP = 0.04
    const val STEPS_PER_FLOOR = 16          // ~16 steps climbs one floor of stairs
    const val STEPS_PER_ACTIVE_MINUTE = 100 // rough cadence used to estimate active time

    const val PREFS_DATASTORE_NAME = "steptrack_settings"

    const val NOTIF_CHANNEL_TRACKING_ID = "tracking_channel"
    const val NOTIF_CHANNEL_REMINDERS_ID = "reminders_channel"
    const val NOTIF_ID_TRACKING = 1001
    const val NOTIF_ID_GOAL_PROGRESS = 1002
    const val NOTIF_ID_GOAL_ACHIEVED = 1003
    const val NOTIF_ID_DAILY_REMINDER = 1004

    const val ACTION_STOP_SERVICE = "com.steptrack.pro.action.STOP_SERVICE"

    const val DATE_PATTERN = "yyyy-MM-dd"

    const val HISTORY_RETENTION_DAYS = 400 // keep >1 year for yearly comparisons
}
