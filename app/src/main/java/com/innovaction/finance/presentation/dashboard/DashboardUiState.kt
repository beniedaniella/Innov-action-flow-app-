package com.innovaction.finance.presentation.dashboard

import com.innovaction.finance.data.local.relation.AvanceWithDetails
import com.innovaction.finance.data.local.relation.OperationWithDetails

/** Données d'un mois pour le graphique (entrées vs sorties). */
data class MoisData(
    val label      : String,   // "Jan", "Fév"…
    val entreesCdf : Double,
    val sortiesCdf : Double,
)

/** Résumé d'un projet pour la barre de progression. */
data class ProjetResume(
    val id         : Long,
    val nom        : String,
    val budgetCdf  : Double,
    val depensesCdf: Double,
    val couleur    : String,
) {
    val pourcentage: Float
        get() = if (budgetCdf <= 0) 0f else (depensesCdf / budgetCdf).coerceIn(0.0, 1.5).toFloat()
}

/** Résumé d'un compte pour l'affichage des soldes. */
data class CompteResume(
    val nom         : String,
    val solde       : Double,
    val deviseCode  : String,
    val deviseSymbole: String,
    val couleur     : String,
)

/** État complet du Tableau de bord. */
data class DashboardUiState(
    val isLoading          : Boolean              = true,
    val error              : String?              = null,

    // Soldes globaux
    val soldeTotalCdf      : Double               = 0.0,
    val soldeTotalUsd      : Double               = 0.0,

    // Comptes individuels
    val comptes            : List<CompteResume>   = emptyList(),

    // Totaux toutes périodes
    val totalEntreesCdf    : Double               = 0.0,
    val totalSortiesCdf    : Double               = 0.0,
    val totalEntreesUsd    : Double               = 0.0,
    val totalSortiesUsd    : Double               = 0.0,

    // Compteurs
    val nbOperations       : Int                  = 0,
    val nbAvancesEnRetard  : Int                  = 0,

    // Alertes
    val alerteSoldeBasCdf  : Boolean              = false,
    val alerteSoldeBasUsd  : Boolean              = false,
    val seuilAlerteCdf     : Double               = 500_000.0,
    val seuilAlerteUsd     : Double               = 150.0,

    // Graphique 6 derniers mois
    val donneesGraphique   : List<MoisData>       = emptyList(),

    // Projets
    val projets            : List<ProjetResume>   = emptyList(),

    // Dernières opérations
    val dernieresOperations: List<OperationWithDetails> = emptyList(),

    // Avances en retard (pour la bannière)
    val avancesEnRetard    : List<AvanceWithDetails>    = emptyList(),

    // Nom de l'association (depuis paramètres)
    val nomAssociation     : String               = "INNOV'ACTION",
    val exercice           : String               = "2026",
)
