package com.innovaction.finance.presentation.operations

import com.innovaction.finance.data.local.entity.*
import com.innovaction.finance.data.local.relation.OperationWithDetails

// ── Filtres du Journal ────────────────────────────────────────────────────────
data class FiltresJournal(
    val type         : String?   = null,   // "ENTREE" | "SORTIE" | "TRANSFERT" | "FRAIS" | null = tous
    val compteId     : Long?     = null,
    val projetId     : Long?     = null,
    val deviseId     : Long?     = null,
    val federationId : Long?     = null,
    val dateDebut    : Long?     = null,
    val dateFin      : Long?     = null,
    val recherche    : String    = "",
) {
    val actifs: Boolean
        get() = type != null || compteId != null || projetId != null ||
                deviseId != null || federationId != null ||
                dateDebut != null || dateFin != null || recherche.isNotBlank()
}

// ── État du formulaire ────────────────────────────────────────────────────────
data class FormulaireOperation(
    val id             : Long?   = null,   // null = nouvelle opération
    val type           : String  = "ENTREE",
    val date           : Long    = System.currentTimeMillis(),
    val dateAffichage  : String  = "",
    val libelle        : String  = "",
    val montant        : String  = "",
    val compteId       : Long?   = null,
    val compteDestId   : Long?   = null,   // transferts uniquement
    val projetId       : Long?   = null,
    val categorieId    : Long?   = null,
    val modePaiementId : Long?   = null,
    val deviseId       : Long?   = null,
    val federationId   : Long?   = null,
    val numeroPiece    : String  = "",
    val remarques      : String  = "",
    // Validation
    val erreurLibelle  : String? = null,
    val erreurMontant  : String? = null,
    val erreurCompte   : String? = null,
    val erreurCategorie: String? = null,
    val erreurMode     : String? = null,
    val erreurDevise   : String? = null,
    val isSaving       : Boolean = false,
) {
    val isValide: Boolean
        get() = libelle.isNotBlank() && montant.toDoubleOrNull() != null &&
                montant.toDouble() > 0 && compteId != null &&
                categorieId != null && modePaiementId != null && deviseId != null
}

// ── État global du module ─────────────────────────────────────────────────────
data class OperationsUiState(
    val isLoading          : Boolean                    = true,
    val error              : String?                    = null,

    // Journal
    val operations         : List<OperationWithDetails> = emptyList(),
    val filtres            : FiltresJournal             = FiltresJournal(),
    val soldeCdf           : Double                     = 0.0,
    val soldeUsd           : Double                     = 0.0,

    // Données pour les dropdowns (chargées depuis la DB)
    val comptes            : List<CompteEntity>         = emptyList(),
    val projets            : List<ProjetEntity>         = emptyList(),
    val categories         : List<CategorieEntity>      = emptyList(),
    val modesPaiement      : List<ModePaiementEntity>   = emptyList(),
    val devises            : List<DeviseEntity>         = emptyList(),
    val federations        : List<FederationEntity>     = emptyList(),

    // Formulaire
    val showFormulaire     : Boolean                    = false,
    val formulaire         : FormulaireOperation        = FormulaireOperation(),

    // Détail
    val operationDetail    : OperationWithDetails?      = null,
    val showDetail         : Boolean                    = false,

    // Confirmation suppression
    val showConfirmDelete  : OperationWithDetails?      = null,
)
