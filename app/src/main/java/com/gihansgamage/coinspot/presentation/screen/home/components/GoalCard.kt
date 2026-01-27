package com.gihansgamage.coinspot.presentation.screen.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${goal.currency} ${String.format("%.2f", goal.currentSaved)} / ${String.format("%.2f", goal.targetPrice)}",
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = (goal.progressPercentage / 100).toFloat(),
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "${String.format("%.0f", goal.progressPercentage)}% Complete",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}