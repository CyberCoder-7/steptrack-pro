package com.steptrack.pro.di

/**
 * Intentionally empty: PreferencesManager, StepSensorManager, NotificationHelper,
 * and ReminderScheduler all declare @Inject constructors with @Singleton scope,
 * so Hilt constructs them automatically without any @Provides methods here.
 * @ApplicationContext is supplied by Hilt's built-in ApplicationContextModule.
 *
 * This file is kept as a placeholder/extension point for any future
 * third-party classes that DO need manual @Provides wiring (e.g. Retrofit,
 * OkHttp clients, or any class Hilt can't constructor-inject directly).
 */
