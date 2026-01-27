package com.gihansgamage.coinspot.presentation.screen.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressChart(
    planned: Double,
    actual: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Planned",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$${String.format("%.2f", planned)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Actual",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$${String.format("%.2f", actual)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simple bar chart visualization
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val barWidth = size.width / 3
            val maxHeight = size.height
            val maxValue = maxOf(planned, actual)

            if (maxValue > 0) {
                // Planned bar
                val plannedHeight = (planned / maxValue * maxHeight).toFloat()
                drawRect(
                    color = Color(0xFF4CAF50),
                    topLeft = Offset(barWidth / 2, maxHeight - plannedHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, plannedHeight)
                )

                // Actual bar
                val actualHeight = (actual / maxValue * maxHeight).toFloat()
                drawRect(
                    color = Color(0xFF2196F3),
                    topLeft = Offset(barWidth * 1.5f + barWidth / 2, maxHeight - actualHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, actualHeight)
                )
            }
        }
    }
}