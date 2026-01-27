package com.gihansgamage.coinspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check onboarding status
        var startDestination = "onboarding"

        lifecycleScope.launch {
            val isOnboardingComplete = dataStoreManager.isOnboardingComplete.first()
            startDestination = if (isOnboardingComplete) "home" else "onboarding"

            setContent {
                CoinsPotTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CoinsPotApp(startDestination = startDestination)
                    }
                }
            }
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