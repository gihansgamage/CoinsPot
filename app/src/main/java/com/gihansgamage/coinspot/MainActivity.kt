package com.gihansgamage.coinspot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.presentation.screen.daily.DailySavingScreen
import com.gihansgamage.coinspot.presentation.screen.goals.CreateGoalScreen
import com.gihansgamage.coinspot.presentation.screen.goals.GoalDetailScreen
import com.gihansgamage.coinspot.presentation.screen.home.HomeScreen
import com.gihansgamage.coinspot.presentation.screen.insights.InsightsScreen
import com.gihansgamage.coinspot.presentation.screen.onboarding.OnboardingScreen
import com.gihansgamage.coinspot.presentation.screen.product.ProductDiscoveryScreen
import com.gihansgamage.coinspot.presentation.screen.settings.SettingsScreen
import com.gihansgamage.coinspot.presentation.theme.CoinsPotTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d(TAG, "=== MainActivity onCreate started ===")

            setContent {
                var isLoading by remember { mutableStateOf(true) }
                var startDestination by remember { mutableStateOf("onboarding") }
                var hasError by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    try {
                        Log.d(TAG, "Checking onboarding status...")
                        val isOnboardingComplete = dataStoreManager.isOnboardingComplete.first()
                        startDestination = if (isOnboardingComplete) "home" else "onboarding"
                        Log.d(TAG, "Start destination determined: $startDestination")
                        isLoading = false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error checking onboarding status", e)
                        // Default to onboarding on error
                        startDestination = "onboarding"
                        isLoading = false
                        hasError = true
                    }
                }

                CoinsPotTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            else -> {
                                CoinsPotApp(startDestination = startDestination)
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "=== MainActivity onCreate completed ===")
        } catch (e: Exception) {
            Log.e(TAG, "FATAL ERROR in MainActivity onCreate", e)
            e.printStackTrace()
            // Rethrow to see the crash in system logs
            throw e
        }
    }
}

@Composable
fun CoinsPotApp(startDestination: String = "onboarding") {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToGoalDetail = { goalId ->
                    navController.navigate("goalDetail/$goalId")
                },
                onNavigateToCreateGoal = {
                    navController.navigate("createGoal")
                },
                onNavigateToDailySaving = {
                    navController.navigate("dailySaving")
                },
                onNavigateToInsights = {
                    navController.navigate("insights")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        composable("createGoal") {
            CreateGoalScreen(
                onBack = { navController.popBackStack() },
                onGoalCreated = { navController.popBackStack() }
            )
        }
        composable("goalDetail/{goalId}") { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")?.toIntOrNull()
            GoalDetailScreen(
                goalId = goalId,
                onBack = { navController.popBackStack() },
                onNavigateToProductDiscovery = { productName ->
                    navController.navigate("productDiscovery/$productName")
                }
            )
        }
        composable("dailySaving") {
            DailySavingScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("insights") {
            InsightsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("productDiscovery/{productName}") { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName")
            ProductDiscoveryScreen(
                productName = productName,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}