package com.innovaction.finance.presentation.comptes

import com.innovaction.finance.data.local.relation.CompteWithDevise
import com.innovaction.finance.data.local.relation.OperationWithDetails

data class CompteDetail(
    val compteWithDevise : CompteWithDevise,
    val solde            : Double,
    val totalEntrees     : Double,
    val totalSorties     : Double,
)

data class ComptesUiState(
    val isLoading        : Boolean           = true,
    val error            : String?           = null,
    val comptes          : List<CompteDetail> = emptyList(),
    // Détail d'un compte sélectionné
    val compteSelectionne: CompteDetail?     = null,
    val historiqueOps    : List<OperationWithDetails> = emptyList(),
    // Formulaire d'ajout/modification
    val showFormulaire   : Boolean           = false,
    val formNom          : String            = "",
    val formDeviseId     : Long?             = null,
    val formSoldeInitial : String            = "0",
    val formCouleur      : String            = "#1F3864",
    val formIsSaving     : Boolean           = false,
)
