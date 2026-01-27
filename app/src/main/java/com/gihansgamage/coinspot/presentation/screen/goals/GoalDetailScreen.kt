package com.gihansgamage.coinspot.presentation.screen.goals

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: Int?,
    onBack: () -> Unit,
    onNavigateToProductDiscovery: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Details") },
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
                text = "Goal ID: ${goalId ?: "Unknown"}",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = { onNavigateToProductDiscovery("Sample Product") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Discover Products")
            }
        }
    }
}