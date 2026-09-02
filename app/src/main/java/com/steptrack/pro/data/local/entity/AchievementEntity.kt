package com.steptrack.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val title: String,
    val description: String,
    val iconName: String,
    val unlockedDate: String?,
    val isUnlocked: Boolean
)
