package com.innovaction.finance.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

data class BottomNavItem(
    val route : String,
    val label : String,
    val icon  : ImageVector,
)

private val navItems = listOf(
    BottomNavItem(NavRoutes.Dashboard.route,  "Tableau",     Icons.Filled.Dashboard),
    BottomNavItem(NavRoutes.Operations.route, "Opérations",  Icons.Filled.AccountBalance),
    BottomNavItem(NavRoutes.Avances.route,    "Avances",     Icons.Filled.Assignment),
    BottomNavItem(NavRoutes.Rapports.route,   "Rapports",    Icons.Filled.Analytics),
    BottomNavItem(NavRoutes.Parametres.route, "Paramètres",  Icons.Filled.Settings),
)

@Composable
fun InnovActionBottomBar(
    currentDestination : NavDestination?,
    onNavigate         : (String) -> Unit,
) {
    NavigationBar {
        navItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(item.route) },
                icon     = { Icon(item.icon, contentDescription = item.label) },
                label    = { Text(item.label) },
            )
        }
    }
}
