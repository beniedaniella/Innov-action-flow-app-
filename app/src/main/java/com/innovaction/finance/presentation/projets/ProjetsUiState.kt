package com.innovaction.finance.presentation.projets

import com.innovaction.finance.data.local.entity.DeviseEntity
import com.innovaction.finance.data.local.entity.ProjetEntity
import com.innovaction.finance.data.local.relation.OperationWithDetails

/** Données calculées pour un projet. */
data class ProjetStats(
    val projet        : ProjetEntity,
    // CDF
    val entreesCdf    : Double = 0.0,
    val sortiesCdf    : Double = 0.0,
    val soldeCdf      : Double = 0.0,
    val pctBudgetCdf  : Float  = 0f,   // dépenses / budget
    // USD
    val entreesUsd    : Double = 0.0,
    val sortiesUsd    : Double = 0.0,
    val soldeUsd      : Double = 0.0,
    val pctBudgetUsd  : Float  = 0f,
    // Compteurs
    val nbOperations  : Int    = 0,
) {
    val statutBudget: String get() = when {
        projet.budgetCdf <= 0        -> "Pas de budget"
        pctBudgetCdf >= 1.0f         -> "⛔ Budget dépassé"
        pctBudgetCdf >= 0.8f         -> "⚠️ Budget critique"
        else                         -> "✅ Budget OK"
    }
}

/** Données d'un mois pour le mini-graphique du projet. */
data class ProjetMoisData(
    val label      : String,
    val entreesCdf : Double,
    val sortiesCdf : Double,
)

data class ProjetsUiState(
    val isLoading         : Boolean           = true,
    val error             : String?           = null,

    // Liste
    val projetsStats      : List<ProjetStats> = emptyList(),

    // Détail
    val projetSelectionne : ProjetStats?      = null,
    val operationsProjet  : List<OperationWithDetails> = emptyList(),
    val graphiqueMois     : List<ProjetMoisData>       = emptyList(),

    // Référentiels
    val devises           : List<DeviseEntity> = emptyList(),

    // Formulaire ajout/édition
    val showFormulaire    : Boolean           = false,
    val formNom           : String            = "",
    val formDescription   : String            = "",
    val formBudgetCdf     : String            = "",
    val formBudgetUsd     : String            = "",
    val formCouleur       : String            = "#1F3864",
    val formIsSaving      : Boolean           = false,
    val formProjetId      : Long?             = null,  // null = nouveau

    // Confirmation désactivation
    val showConfirmDesactiver : ProjetStats?  = null,
)
