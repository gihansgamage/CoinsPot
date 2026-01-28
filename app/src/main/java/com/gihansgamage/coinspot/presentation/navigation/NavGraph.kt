package com.gihansgamage.coinspot.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gihansgamage.coinspot.presentation.screen.daily.DailySavingScreen
import com.gihansgamage.coinspot.presentation.screen.goals.CreateGoalScreen
import com.gihansgamage.coinspot.presentation.screen.goals.GoalDetailScreen
import com.gihansgamage.coinspot.presentation.screen.home.HomeScreen
import com.gihansgamage.coinspot.presentation.screen.insights.InsightsScreen
import com.gihansgamage.coinspot.presentation.screen.onboarding.OnboardingScreen
import com.gihansgamage.coinspot.presentation.screen.product.ProductDiscoveryScreen
import com.gihansgamage.coinspot.presentation.screen.settings.SettingsScreen

@Composable
fun CoinsPotNavGraph(
    navController: NavHostController,
    startDestination: String = "onboarding"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding Route
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Home Route
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

        // Create Goal Route
        composable("createGoal") {
            CreateGoalScreen(
                onBack = { navController.popBackStack() },
                onGoalCreated = { navController.popBackStack() }
            )
        }

        // Goal Detail Route
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

        // Daily Saving Route
        composable("dailySaving") {
            DailySavingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Insights/Analytics Route
        composable("insights") {
            InsightsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Product Discovery Route
        composable("productDiscovery/{productName}") { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName")
            ProductDiscoveryScreen(
                productName = productName,
                onBack = { navController.popBackStack() }
            )
        }

        // Settings Route
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Navigation route constants
 */
object NavRoutes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CREATE_GOAL = "createGoal"
    const val GOAL_DETAIL = "goalDetail/{goalId}"
    const val DAILY_SAVING = "dailySaving"
    const val INSIGHTS = "insights"
    const val PRODUCT_DISCOVERY = "productDiscovery/{productName}"
    const val SETTINGS = "settings"

    // Navigation functions
    fun goalDetail(goalId: Int) = "goalDetail/$goalId"
    fun productDiscovery(productName: String) = "productDiscovery/$productName"
}