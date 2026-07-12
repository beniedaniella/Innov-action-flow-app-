package com.innovaction.finance.presentation.avances

import com.innovaction.finance.data.local.entity.CompteEntity
import com.innovaction.finance.data.local.entity.DeviseEntity
import com.innovaction.finance.data.local.entity.ProjetEntity
import com.innovaction.finance.data.local.relation.AvanceWithDetails

/** Filtre actif sur la liste. */
enum class FiltreAvance { TOUTES, ACTIVES, EN_RETARD, REMBOURSEES }

/** État du formulaire d'avance. */
data class FormulaireAvance(
    val id               : Long?   = null,
    val beneficiaire     : String  = "",
    val objet            : String  = "",
    val montant          : String  = "",
    val deviseId         : Long?   = null,
    val projetId         : Long?   = null,
    val dateEmission     : Long    = System.currentTimeMillis(),
    val dateEmissionAff  : String  = "",
    val dateEcheance     : Long?   = null,
    val dateEcheanceAff  : String  = "",
    val numeroDecharge   : String  = "",
    val remarques        : String  = "",
    // Validation
    val erreurBenef      : String? = null,
    val erreurObjet      : String? = null,
    val erreurMontant    : String? = null,
    val erreurDevise     : String? = null,
    val erreurEcheance   : String? = null,
    val isSaving         : Boolean = false,
)

/** État du formulaire de remboursement. */
data class FormulaireRemboursement(
    val avanceId         : Long    = 0,
    val montantRestant   : Double  = 0.0,
    val montant          : String  = "",
    val dateRemboursement: Long    = System.currentTimeMillis(),
    val dateAff          : String  = "",
    val deviseSymbole    : String  = "FC",
    val erreurMontant    : String? = null,
    val isSaving         : Boolean = false,
)

data class AvancesUiState(
    val isLoading           : Boolean             = true,
    val error               : String?             = null,

    // Données
    val toutes              : List<AvanceWithDetails> = emptyList(),
    val enRetard            : List<AvanceWithDetails> = emptyList(),
    val nbEnRetard          : Int                 = 0,
    val montantTotalRetard  : Double              = 0.0,

    // Filtre actif
    val filtre              : FiltreAvance        = FiltreAvance.TOUTES,

    // Détail
    val avanceSelectionnee  : AvanceWithDetails?  = null,
    val showDetail          : Boolean             = false,

    // Formulaire nouvelle/édition avance
    val showFormulaire      : Boolean             = false,
    val formulaire          : FormulaireAvance    = FormulaireAvance(),

    // Formulaire remboursement
    val showRemboursement   : Boolean             = false,
    val frmRembours         : FormulaireRemboursement = FormulaireRemboursement(),

    // Confirm suppression
    val showConfirmDelete   : AvanceWithDetails?  = null,

    // Référentiels
    val devises             : List<DeviseEntity>  = emptyList(),
    val projets             : List<ProjetEntity>  = emptyList(),
    val comptes             : List<CompteEntity>  = emptyList(),
) {
    val affichees: List<AvanceWithDetails>
        get() = when (filtre) {
            FiltreAvance.TOUTES      -> toutes
            FiltreAvance.ACTIVES     -> toutes.filter { it.avance.statut == "ACTIVE" }
            FiltreAvance.EN_RETARD   -> enRetard
            FiltreAvance.REMBOURSEES -> toutes.filter {
                it.avance.statut in listOf("REMBOURSEE_TOTALE", "REMBOURSEE_PARTIELLE")
            }
        }
}
