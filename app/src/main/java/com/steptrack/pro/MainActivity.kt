package com.steptrack.pro

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.steptrack.pro.data.service.StepCounterService
import com.steptrack.pro.presentation.navigation.StepTrackNavGraph
import com.steptrack.pro.presentation.navigation.bottomNavItems
import com.steptrack.pro.presentation.navigation.isBottomNavRoute
import com.steptrack.pro.presentation.theme.StepTrackProTheme
import com.steptrack.pro.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalPermissionsApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StepTrackProTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = PermissionUtils.requiredRuntimePermissions().toList()
                )

                androidx.compose.runtime.LaunchedEffect(Unit) {
                    if (PermissionUtils.requiredRuntimePermissions().isNotEmpty() && !permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                    } else {
                        startTrackingService()
                    }
                }

                androidx.compose.runtime.LaunchedEffect(permissionsState.allPermissionsGranted) {
                    if (permissionsState.allPermissionsGranted) {
                        startTrackingService()
                    }
                }

                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (isBottomNavRoute(currentRoute)) {
                            NavigationBar {
                                bottomNavItems.forEach { item ->
                                    val selected = currentRoute == item.screen.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            androidx.compose.material3.Icon(
                                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label
                                            )
                                        },
                                        label = { Text(item.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.padding(padding)) {
                        StepTrackNavGraph(navController = navController)
                    }
                }
            }
        }
    }

    private fun startTrackingService() {
        val intent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, intent)
        } else {
            startService(intent)
        }
    }
}
