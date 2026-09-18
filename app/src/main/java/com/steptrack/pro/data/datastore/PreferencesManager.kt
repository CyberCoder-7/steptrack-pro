package com.steptrack.pro.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.steptrack.pro.domain.model.AppSettings
import com.steptrack.pro.domain.model.AppTheme
import com.steptrack.pro.domain.model.DistanceUnit
import com.steptrack.pro.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = Constants.PREFS_DATASTORE_NAME)

/**
 * Single source of truth for user-configurable settings, persisted via
 * Jetpack DataStore (replacing SharedPreferences for type-safety + Flow support).
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
        val THEME = stringPreferencesKey("theme")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val GOAL_REMINDER_ENABLED = booleanPreferencesKey("goal_reminder_enabled")
        val GOAL_ACHIEVED_NOTIF_ENABLED = booleanPreferencesKey("goal_achieved_notif_enabled")
        val STRIDE_LENGTH = doublePreferencesKey("stride_length_meters")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SENSOR_CALIBRATION_OFFSET = intPreferencesKey("sensor_calibration_offset")
        val STEP_BASELINE_TODAY = intPreferencesKey("step_baseline_today")
        val STEP_BASELINE_DATE = stringPreferencesKey("step_baseline_date")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            dailyGoal = prefs[Keys.DAILY_GOAL] ?: Constants.DEFAULT_DAILY_GOAL,
            distanceUnit = DistanceUnit.valueOf(prefs[Keys.DISTANCE_UNIT] ?: DistanceUnit.KM.name),
            theme = AppTheme.valueOf(prefs[Keys.THEME] ?: AppTheme.SYSTEM.name),
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER_ENABLED] ?: true,
            dailyReminderHour = prefs[Keys.DAILY_REMINDER_HOUR] ?: 18,
            dailyReminderMinute = prefs[Keys.DAILY_REMINDER_MINUTE] ?: 0,
            goalReminderEnabled = prefs[Keys.GOAL_REMINDER_ENABLED] ?: true,
            goalAchievedNotifEnabled = prefs[Keys.GOAL_ACHIEVED_NOTIF_ENABLED] ?: true,
            strideLengthMeters = prefs[Keys.STRIDE_LENGTH] ?: Constants.DEFAULT_STRIDE_METERS,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false
        )
    }

    suspend fun updateDailyGoal(goal: Int) {
        context.dataStore.edit { it[Keys.DAILY_GOAL] = goal }
    }

    suspend fun updateDistanceUnit(unit: DistanceUnit) {
        context.dataStore.edit { it[Keys.DISTANCE_UNIT] = unit.name }
    }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.DAILY_REMINDER_ENABLED] = enabled
            it[Keys.DAILY_REMINDER_HOUR] = hour
            it[Keys.DAILY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun updateGoalReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.GOAL_REMINDER_ENABLED] = enabled }
    }

    suspend fun updateGoalAchievedNotifEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.GOAL_ACHIEVED_NOTIF_ENABLED] = enabled }
    }

    suspend fun updateStrideLength(meters: Double) {
        context.dataStore.edit { it[Keys.STRIDE_LENGTH] = meters }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun updateSensorCalibrationOffset(offset: Int) {
        context.dataStore.edit { it[Keys.SENSOR_CALIBRATION_OFFSET] = offset }
    }

    suspend fun getStepBaseline(): Pair<Int, String>? {
        val prefs = context.dataStore.data.first()
        val baseline = prefs[Keys.STEP_BASELINE_TODAY] ?: return null
        val date = prefs[Keys.STEP_BASELINE_DATE] ?: return null
        return baseline to date
    }

    suspend fun setStepBaseline(hardwareValue: Int, date: String) {
        context.dataStore.edit {
            it[Keys.STEP_BASELINE_TODAY] = hardwareValue
            it[Keys.STEP_BASELINE_DATE] = date
        }
    }
}
