package com.steptrack.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One row per calendar day. [date] is the natural primary key (yyyy-MM-dd)
 * so upserts are trivial and there is exactly one record per day.
 */
@Entity(tableName = "daily_steps")
data class DailyStepEntity(
    @PrimaryKey val date: String,
    val steps: Int,
    val goal: Int,
    val distance: Double,      // meters
    val calories: Double,
    val activeMinutes: Int,
    val floors: Int,
    val lastUpdatedEpochMillis: Long
)
