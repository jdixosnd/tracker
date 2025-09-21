package com.babyfeed.tracker.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object ChildList : Screen("child_list")

    object RegisterChild : Screen("register_child_screen") {
        const val routeWithArgs = "register_child_screen?childId={childId}"
        val arguments = listOf(
            navArgument("childId") {
                type = NavType.IntType
                defaultValue = -1 // Indicates a new child
            }
        )

        fun createRoute(childId: Int): String {
            return "register_child_screen?childId=$childId"
        }
    }

    object AddMilkFeed : Screen("add_milk_feed_screen") {
        const val routeWithArgs = "add_milk_feed_screen/{childId}"
        val arguments = listOf(
            navArgument("childId") { type = NavType.IntType }
        )
        fun createRoute(childId: Int): String {
            return "add_milk_feed_screen/$childId"
        }
    }

    object MedicationDashboard : Screen("medication_dashboard/{childId}") {
        fun createRoute(childId: Int): String {
            return "medication_dashboard/$childId"
        }
    }
    object MedicationLog : Screen("medication_log")

    object AddEditMedication : Screen("add_edit_medication_screen") {
        const val routeWithArgs = "add_edit_medication_screen?childId={childId}&medicationId={medicationId}"
        val arguments = listOf(
            navArgument("childId") { type = NavType.IntType },
            navArgument("medicationId") {
                type = NavType.IntType
                defaultValue = -1 // New medication
            }
        )
        fun createRoute(childId: Int, medicationId: Int? = null): String {
            val route = "add_edit_medication_screen?childId=$childId"
            return if (medicationId != null) "$route&medicationId=$medicationId" else route
        }
    }
}
