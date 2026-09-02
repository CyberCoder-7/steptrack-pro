package com.steptrack.pro.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class ChartPoint(val label: String, val value: Int, val goalMet: Boolean)

/**
 * Lightweight, dependency-free bar chart for daily/weekly/monthly trends.
 * Drawn directly with Canvas to avoid pulling in a heavy charting library
 * for what is fundamentally a simple, smoothly animatable bar view.
 */
@Composable
fun StepBarChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    goalLine: Int? = null
) {
    val barColor = MaterialTheme.colorScheme.primary
    val goalColor = MaterialTheme.colorScheme.secondary
    val maxValue = (points.maxOfOrNull { it.value } ?: 1).coerceAtLeast(goalLine ?: 0).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        if (points.isEmpty()) {
            Text(
                text = "No data yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 4.dp)
        ) {
            val barSpacing = size.width / points.size
            val barWidth = barSpacing * 0.5f

            goalLine?.let { goal ->
                val y = size.height - (goal.toFloat() / maxValue) * size.height
                drawLine(
                    color = goalColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                )
            }

            points.forEachIndexed { index, point ->
                val barHeight = (point.value.toFloat() / maxValue) * size.height
                val left = index * barSpacing + (barSpacing - barWidth) / 2f
                drawRoundRect(
                    color = if (point.goalMet) barColor else barColor.copy(alpha = 0.35f),
                    topLeft = Offset(left, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
            }
        }
        ChartLabels(points)
    }
}

@Composable
private fun ChartLabels(points: List<ChartPoint>) {
    androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
        points.forEach { point ->
            Text(
                text = point.label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
