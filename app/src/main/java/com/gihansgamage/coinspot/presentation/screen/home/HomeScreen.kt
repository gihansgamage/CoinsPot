package com.gihansgamage.coinspot.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gihansgamage.coinspot.presentation.components.common.LoadingIndicator
import com.gihansgamage.coinspot.presentation.components.navigation.BottomNavigationBar
import com.gihansgamage.coinspot.presentation.components.navigation.NavItem
import com.gihansgamage.coinspot.presentation.screen.home.components.GoalCard
import com.gihansgamage.coinspot.presentation.screen.home.components.ProgressChart
import com.gihansgamage.coinspot.presentation.viewmodel.HomeViewModel

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
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "CoinsPot",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.greeting.isNotEmpty()) {
                            Text(
                                uiState.greeting,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateGoal,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        },
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    NavItem(
                        icon = Icons.Default.Wallet,
                        label = "Home",
                        route = "home"
                    ),
                    NavItem(
                        icon = Icons.Default.Wallet,
                        label = "Daily",
                        route = "daily"
                    ),
                    NavItem(
                        icon = Icons.Default.Insights,
                        label = "Insights",
                        route = "insights"
                    )
                ),
                currentRoute = "home",
                onItemClick = { route ->
                    when (route) {
                        "daily" -> onNavigateToDailySaving()
                        "insights" -> onNavigateToInsights()
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    // Stats Overview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Active Goals",
                            value = uiState.totalActiveGoals.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Saved",
                            value = "${uiState.currencySymbol}${String.format("%.2f", uiState.totalSavedAmount)}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Monthly Progress
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Monthly Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ProgressChart(
                                planned = uiState.totalSavingsThisMonth,
                                actual = uiState.totalSavingsThisMonth * 0.8, // Example
                                modifier = Modifier.height(150.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Active Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.activeGoals.isEmpty()) {
                    item {
                        EmptyState(
                            onAddGoal = onNavigateToCreateGoal
                        )
                    }
                } else {
                    items(uiState.activeGoals) { goal ->
                        GoalCard(
                            goal = goal,
                            onClick = { onNavigateToGoalDetail(goal.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyState(
    onAddGoal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Goals Yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first saving goal to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddGoal) {
            Text("Create First Goal")
        }
    }
}