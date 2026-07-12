package com.innovaction.finance.presentation.parametres

import com.innovaction.finance.data.local.entity.*

enum class SectionParametres {
    ACCUEIL, ASSOCIATION, PROJETS, MODES_PAIEMENT, CATEGORIES,
    FEDERATIONS, TAUX_CHANGE, ALERTES, PREFERENCES
}

data class ParametresUiState(
    val isLoading       : Boolean                 = true,
    val error           : String?                 = null,
    val section         : SectionParametres       = SectionParametres.ACCUEIL,

    // Données référentiels (chargées depuis la DB)
    val projets         : List<ProjetEntity>       = emptyList(),
    val modes           : List<ModePaiementEntity> = emptyList(),
    val categories      : List<CategorieEntity>    = emptyList(),
    val federations     : List<FederationEntity>   = emptyList(),
    val devises         : List<DeviseEntity>       = emptyList(),

    // Paramètres clé/valeur
    val nomAssociation  : String = "INNOV'ACTION",
    val exercice        : String = "2026",
    val seuilAlerteCdf  : String = "500000",
    val seuilAlerteUsd  : String = "150",
    val tauxUsdCdf      : String = "2800",
    val rappelAvanceJours: String = "7",
    val darkMode        : Boolean = false,

    // Formulaire générique (réutilisé pour projets, modes, catégories…)
    val showFormulaire  : Boolean = false,
    val formLabel       : String  = "",
    val formValeur1     : String  = "",  // budgetCdf / ordre / typeDefaut…
    val formValeur2     : String  = "",
    val formEditId      : Long?   = null,
    val formIsSaving    : Boolean = false,

    // Confirmation suppression
    val showConfirmDelete : String? = null,  // message de confirmation
    val pendingDeleteId   : Long?   = null,

    val succesMessage   : String? = null,
)
