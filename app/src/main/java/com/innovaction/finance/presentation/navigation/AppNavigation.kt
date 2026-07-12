package com.innovaction.finance.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.innovaction.finance.presentation.avances.AvancesScreen
import com.innovaction.finance.presentation.components.topbar.InnovActionTopBar
import com.innovaction.finance.presentation.comptes.ComptesScreen
import com.innovaction.finance.presentation.dashboard.DashboardScreen
import com.innovaction.finance.presentation.operations.OperationsScreen
import com.innovaction.finance.presentation.parametres.ParametresScreen
import com.innovaction.finance.presentation.projets.ProjetsScreen
import com.innovaction.finance.presentation.rapports.RapportsScreen
import com.innovaction.finance.presentation.securite.SecuriteScreen
import com.innovaction.finance.presentation.splash.PlaceholderScreen
import com.innovaction.finance.presentation.theme.NavTransitions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController     = rememberNavController()
    val drawerState       = rememberDrawerState(DrawerValue.Closed)
    val scope             = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val isDetailScreen = currentRoute in listOf(
        NavRoutes.NouvelleOperation.route, NavRoutes.NouvelleAvance.route,
        NavRoutes.DetailOperation.route,   NavRoutes.DetailAvance.route,
        NavRoutes.DetailCompte.route,      NavRoutes.DetailProjet.route,
    )

    fun navigate(route: String) = navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true; restoreState = true
    }

    ModalNavigationDrawer(
        drawerState     = drawerState,
        gesturesEnabled = !isDetailScreen,
        drawerContent   = {
            InnovActionDrawer(
                currentRoute = currentRoute,
                onNavigate   = { navigate(it) },
                onClose      = { scope.launch { drawerState.close() } },
            )
        }
    ) {
        Scaffold(
            modifier     = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                InnovActionTopBar(
                    title       = routeTitle(currentRoute),
                    subtitle    = routeSubtitle(currentRoute),
                    onMenuClick = if (!isDetailScreen) { { scope.launch { drawerState.open() } } } else null,
                    onBackClick = if (isDetailScreen)  { { navController.popBackStack() } }        else null,
                )
            },
            bottomBar = {
                if (!isDetailScreen) InnovActionBottomBar(
                    currentDestination = navBackStackEntry?.destination,
                    onNavigate         = { navigate(it) },
                )
            }
        ) { pad ->
            NavHost(
                navController    = navController,
                startDestination = NavRoutes.Dashboard.route,
                modifier         = Modifier.padding(pad),
                // Transitions globales
                enterTransition  = { NavTransitions.fadeIn },
                exitTransition   = { NavTransitions.fadeOut },
                popEnterTransition  = { NavTransitions.slideInFromLeft },
                popExitTransition   = { NavTransitions.slideOutToRight },
            ) {
                composable(NavRoutes.Dashboard.route,
                    enterTransition = { NavTransitions.fadeIn },
                    exitTransition  = { NavTransitions.fadeOut }) {
                    DashboardScreen(
                        onNavigateToOperations = { navigate(NavRoutes.Operations.route) },
                        onNavigateToAvances    = { navigate(NavRoutes.Avances.route) },
                    )
                }
                composable(NavRoutes.Comptes.route)    { ComptesScreen() }
                composable(NavRoutes.Operations.route) { OperationsScreen() }
                composable(NavRoutes.Projets.route)    { ProjetsScreen() }
                composable(NavRoutes.Avances.route)    { AvancesScreen() }
                composable(NavRoutes.Rapports.route)   { RapportsScreen() }
                composable(NavRoutes.Exports.route)    { RapportsScreen() }
                composable(NavRoutes.Parametres.route) { ParametresScreen() }
                composable(NavRoutes.Securite.route)   { SecuriteScreen() }  // ✅ Étape 11
                composable(NavRoutes.NouvelleOperation.route) { PlaceholderScreen("Nouvelle opération") }
                composable(NavRoutes.NouvelleAvance.route)    { PlaceholderScreen("Nouvelle avance") }
                composable(NavRoutes.DetailOperation.route)   { PlaceholderScreen("Détail opération") }
                composable(NavRoutes.DetailAvance.route)      { PlaceholderScreen("Détail avance") }
                composable(NavRoutes.DetailCompte.route)      { PlaceholderScreen("Détail compte") }
                composable(NavRoutes.DetailProjet.route)      { PlaceholderScreen("Détail projet") }
            }
        }
    }
}

private fun routeTitle(route: String?) = when (route) {
    NavRoutes.Dashboard.route  -> "Tableau de bord"
    NavRoutes.Comptes.route    -> "Comptes"
    NavRoutes.Operations.route -> "Opérations financières"
    NavRoutes.Projets.route    -> "Projets"
    NavRoutes.Avances.route    -> "Avances & Décharges"
    NavRoutes.Rapports.route,
    NavRoutes.Exports.route    -> "Rapports & Exports"
    NavRoutes.Parametres.route -> "Paramètres"
    NavRoutes.Securite.route   -> "Sécurité"
    else -> "INNOV'ACTION"
}

private fun routeSubtitle(route: String?) = when (route) {
    NavRoutes.Dashboard.route  -> "Vue d'ensemble"
    NavRoutes.Comptes.route    -> "Soldes en temps réel"
    NavRoutes.Operations.route -> "Journal de caisse"
    NavRoutes.Projets.route    -> "Budget · Recettes · Dépenses"
    NavRoutes.Avances.route    -> "Suivi des remboursements"
    NavRoutes.Rapports.route,
    NavRoutes.Exports.route    -> "Mensuel · Annuel · Par projet"
    NavRoutes.Parametres.route -> "Configuration de l'application"
    NavRoutes.Securite.route   -> "PIN · Biométrie"
    else -> null
}
