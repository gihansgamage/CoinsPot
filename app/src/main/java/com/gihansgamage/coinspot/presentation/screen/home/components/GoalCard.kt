package com.gihansgamage.coinspot.presentation.screen.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCard(
    goal: SavingGoal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Goal name
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Amount info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${goal.currencySymbol}${String.format("%.2f", goal.currentAmount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "of ${goal.currencySymbol}${String.format("%.2f", goal.targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Days remaining
            val daysRemaining = goal.daysUntilTarget
            Text(
                text = if (daysRemaining > 0) {
                    "$daysRemaining days remaining"
                } else if (daysRemaining == 0L) {
                    "Due today!"
                } else {
                    "${-daysRemaining} days overdue"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (daysRemaining < 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}