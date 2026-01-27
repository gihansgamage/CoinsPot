package com.gihansgamage.coinspot.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gihansgamage.coinspot.presentation.screen.home.components.GoalCard
import com.gihansgamage.coinspot.presentation.screen.home.components.StatCard
import com.gihansgamage.coinspot.presentation.viewmodel.HomeViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGoalDetail: (Int) -> Unit,
    onNavigateToCreateGoal: () -> Unit,
    onNavigateToDailySaving: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Collect states from ViewModel
    val userName by viewModel.userName.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()
    val totalSavings by viewModel.totalSavings.collectAsState()
    val totalRemaining by viewModel.totalRemaining.collectAsState()
    val completedGoalsCount by viewModel.completedGoalsCount.collectAsState()

    // Get greeting based on time
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = userName.ifEmpty { "Welcome" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateGoal,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Goal"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Stats Section
            item {
                Text(
                    text = "Your Savings Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Saved",
                        value = "$currencySymbol${String.format("%.2f", totalSavings)}",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Active Goals",
                        value = "${activeGoals.size}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Remaining",
                        value = "$currencySymbol${String.format("%.2f", totalRemaining)}",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Completed",
                        value = "$completedGoalsCount",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Actions
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToDailySaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Daily Saving")
                    }

                    OutlinedButton(
                        onClick = onNavigateToInsights,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Insights")
                    }
                }
            }

            // Active Goals Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (activeGoals.isNotEmpty()) {
                        TextButton(onClick = { /* TODO: Navigate to all goals */ }) {
                            Text("See All")
                        }
                    }
                }
            }

            if (activeGoals.isEmpty()) {
                item {
                    EmptyGoalsPlaceholder(
                        onCreateGoal = onNavigateToCreateGoal
                    )
                }
            } else {
                items(activeGoals) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onNavigateToGoalDetail(goal.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}

@Composable
fun EmptyGoalsPlaceholder(
    onCreateGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎯",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Active Goals Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start your savings journey by creating your first goal!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onCreateGoal) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Your First Goal")
            }
        }
    }
}