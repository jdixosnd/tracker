package com.babyfeed.tracker.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.babyfeed.tracker.ui.screens.addmilkfeed.AddMilkFeedScreen
import com.babyfeed.tracker.ui.screens.childprofile.ChildListScreen
import com.babyfeed.tracker.ui.screens.childprofile.ChildProfileViewModel
import com.babyfeed.tracker.ui.screens.childprofile.RegisterChildScreen
import com.babyfeed.tracker.ui.screens.dashboard.DashboardScreen
import com.babyfeed.tracker.ui.screens.medication.AddEditMedicationScreen
import com.babyfeed.tracker.ui.screens.medication.MedicationDashboardScreen
import com.babyfeed.tracker.ui.screens.medication.MedicationLogScreen

@Composable
fun NavGraph(
    viewModel: ChildProfileViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val children by viewModel.children.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startDestination = if (children.isEmpty()) {
            Screen.RegisterChild.routeWithArgs
        } else {
            Screen.Dashboard.route
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable(
                route = Screen.RegisterChild.routeWithArgs,
                arguments = Screen.RegisterChild.arguments
            ) { backStackEntry ->
                val childId = backStackEntry.arguments?.getInt("childId") ?: -1
                RegisterChildScreen(
                    childId = childId,
                    onProfileCreated = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onManageChildren = { navController.navigate(Screen.ChildList.route) },
                    onAddMilkFeed = { childId ->
                        navController.navigate(Screen.AddMilkFeed.createRoute(childId))
                    },
                    onNavigateToMedication = {
                        val activeChildId = viewModel.activeChild.value?.id ?: -1
                        if (activeChildId != -1) {
                            navController.navigate(Screen.MedicationDashboard.createRoute(activeChildId))
                        }
                    }
                )
            }
            composable(Screen.MedicationDashboard.route) {
                MedicationDashboardScreen(
                    onAddMedication = { childId ->
                        navController.navigate(Screen.AddEditMedication.createRoute(childId))
                    },
                    onNavigateToLog = {
                        navController.navigate(Screen.MedicationLog.route)
                    }
                )
            }
            composable(Screen.MedicationLog.route) {
                MedicationLogScreen()
            }
            composable(
                route = Screen.AddMilkFeed.routeWithArgs,
                arguments = Screen.AddMilkFeed.arguments
            ) { backStackEntry ->
                val childId = backStackEntry.arguments?.getInt("childId") ?: -1
                AddMilkFeedScreen(
                    childId = childId,
                    onFeedAdded = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.ChildList.route) {
                ChildListScreen(
                    onAddChild = { navController.navigate(Screen.RegisterChild.createRoute(-1)) },
                    onEditChild = { childId ->
                        navController.navigate(Screen.RegisterChild.createRoute(childId))
                    }
                )
            }
            composable(
                route = Screen.AddEditMedication.routeWithArgs,
                arguments = Screen.AddEditMedication.arguments
            ) { backStackEntry ->
                val childId = backStackEntry.arguments?.getInt("childId") ?: -1
                val medicationId = backStackEntry.arguments?.getInt("medicationId")
                AddEditMedicationScreen(
                    childId = childId,
                    medicationId = if (medicationId == -1) null else medicationId,
                    onMedicationSaved = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
