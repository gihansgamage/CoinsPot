package com.gihansgamage.coinspot.presentation.screen.goals

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gihansgamage.coinspot.presentation.components.common.AppTextField
import com.gihansgamage.coinspot.presentation.viewmodel.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    onBack: () -> Unit,
    onGoalCreated: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isGoalCreated) {
        if (uiState.isGoalCreated) {
            onGoalCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Goal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create a new saving goal",
                style = MaterialTheme.typography.headlineSmall
            )

            AppTextField(
                value = uiState.goalName,
                onValueChange = { viewModel.updateGoalName(it) },
                label = "Goal Name (e.g., New Phone, Vacation)"
            )

            AppTextField(
                value = if (uiState.targetPrice == 0.0) "" else uiState.targetPrice.toString(),
                onValueChange = { viewModel.updateTargetPrice(it.toDoubleOrNull() ?: 0.0) },
                label = "Target Price (${uiState.currencySymbol})",
                keyboardType = KeyboardType.Decimal
            )

            AppTextField(
                value = if (uiState.dailySavingAmount == 0.0) "" else uiState.dailySavingAmount.toString(),
                onValueChange = { viewModel.updateDailySavingAmount(it.toDoubleOrNull() ?: 0.0) },
                label = "Daily Saving Amount (${uiState.currencySymbol})",
                keyboardType = KeyboardType.Decimal
            )

            // Show estimated days
            if (uiState.estimatedDays > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Estimated Time to Complete",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${uiState.estimatedDays} days",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.createGoal() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.goalName.isNotEmpty() &&
                        uiState.targetPrice > 0 &&
                        uiState.dailySavingAmount > 0
            ) {
                Text("Create Goal")
            }
        }
    }
}