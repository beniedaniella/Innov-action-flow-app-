package com.innovaction.finance.presentation.navigation

/**
 * Toutes les routes de navigation de l'application.
 * Sealed class = navigation type-safe, sans chaînes magiques dans le code.
 */
sealed class NavRoutes(val route: String) {

    // ── Écrans principaux (Bottom Navigation) ───────────────────────────────
    data object Dashboard      : NavRoutes("dashboard")
    data object Operations     : NavRoutes("operations")
    data object Avances        : NavRoutes("avances")
    data object Rapports       : NavRoutes("rapports")
    data object Parametres     : NavRoutes("parametres")

    // ── Formulaires ─────────────────────────────────────────────────────────
    data object NouvelleOperation : NavRoutes("operation/new")
    data object NouvelleAvance    : NavRoutes("avance/new")

    // ── Détails (avec argument) ──────────────────────────────────────────────
    data object DetailOperation : NavRoutes("operation/{id}") {
        fun createRoute(id: Long) = "operation/$id"
    }
    data object DetailAvance    : NavRoutes("avance/{id}") {
        fun createRoute(id: Long) = "avance/$id"
    }
    data object DetailCompte    : NavRoutes("compte/{id}") {
        fun createRoute(id: Long) = "compte/$id"
    }
    data object DetailProjet    : NavRoutes("projet/{id}") {
        fun createRoute(id: Long) = "projet/$id"
    }
    data object DetailRapport   : NavRoutes("rapport/{type}/{periode}") {
        fun createRoute(type: String, periode: String) = "rapport/$type/$periode"
    }

    // ── Modules secondaires ──────────────────────────────────────────────────
    data object Comptes         : NavRoutes("comptes")
    data object Projets         : NavRoutes("projets")
    data object Exports         : NavRoutes("exports")
    data object Securite        : NavRoutes("securite")
}

/** Écrans affichés dans la Bottom Navigation Bar. */
val bottomNavItems = listOf(
    NavRoutes.Dashboard,
    NavRoutes.Operations,
    NavRoutes.Avances,
    NavRoutes.Rapports,
    NavRoutes.Parametres,
)
