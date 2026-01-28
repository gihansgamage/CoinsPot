package com.gihansgamage.coinspot.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Goals : Screen("goals")
    object GoalDetail : Screen("goalDetail/{goalId}") {
        fun createRoute(goalId: Int) = "goalDetail/$goalId"
    }
    object CreateGoal : Screen("createGoal")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
}