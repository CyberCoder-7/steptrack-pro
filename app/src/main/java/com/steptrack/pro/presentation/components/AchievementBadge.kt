package com.steptrack.pro.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.steptrack.pro.domain.model.Achievement
import com.steptrack.pro.presentation.theme.GoldAchievement

/** Badge tile used in the Achievements grid and the dashboard's "Recent" row. */
@Composable
fun AchievementBadge(
    achievement: Achievement,
    modifier: Modifier = Modifier,
    animateUnlock: Boolean = false
) {
    val scale = remember { Animatable(if (animateUnlock) 0.6f else 1f) }
    LaunchedEffect(achievement.isUnlocked) {
        if (achievement.isUnlocked && animateUnlock) {
            scale.animateTo(1.15f, tween(220))
            scale.animateTo(1f, tween(150))
        }
    }

    Column(
        modifier = modifier.width(88.dp).scale(scale.value),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = if (achievement.isUnlocked) GoldAchievement.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (achievement.isUnlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                contentDescription = achievement.title,
                tint = if (achievement.isUnlocked) GoldAchievement else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = achievement.title,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
