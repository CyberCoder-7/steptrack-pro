package com.steptrack.pro.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.steptrack.pro.presentation.achievements.AchievementsScreen
import com.steptrack.pro.presentation.dashboard.DashboardScreen
import com.steptrack.pro.presentation.history.HistoryScreen
import com.steptrack.pro.presentation.insights.InsightsScreen
import com.steptrack.pro.presentation.onboarding.OnboardingScreen
import com.steptrack.pro.presentation.profile.ProfileScreen
import com.steptrack.pro.presentation.settings.SettingsScreen
import com.steptrack.pro.presentation.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object History : Screen("history")
    object Achievements : Screen("achievements")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Insights : Screen("insights")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.History, "History", Icons.Filled.History, Icons.Outlined.History),
    BottomNavItem(Screen.Achievements, "Achievements", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun StepTrackNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToInsights = { navController.navigate(Screen.Insights.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) }
            )
        }
        composable(Screen.History.route) { HistoryScreen() }
        composable(Screen.Achievements.route) { AchievementsScreen() }
        composable(Screen.Profile.route) {
            ProfileScreen(onNavigateToSettings = { navController.navigate(Screen.Settings.route) })
        }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Insights.route) { InsightsScreen(onBack = { navController.popBackStack() }) }
    }
}

fun isBottomNavRoute(route: String?): Boolean =
    bottomNavItems.any { it.screen.route == route }
