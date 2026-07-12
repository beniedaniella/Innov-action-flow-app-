package com.innovaction.finance.presentation.rapports

/** Type de rapport sélectionné. */
enum class TypeRapport { MENSUEL, ANNUEL, PAR_PROJET }

/** Données agrégées d'un mois pour l'affichage. */
data class DonneesMois(
    val mois       : Int,
    val annee      : Int,
    val nomMois    : String,
    val entreesCdf : Double = 0.0,
    val sortiesCdf : Double = 0.0,
    val entreesUsd : Double = 0.0,
    val sortiesUsd : Double = 0.0,
    val nbOps      : Int    = 0,
)

data class RapportsUiState(
    val isLoading        : Boolean         = true,
    val error            : String?         = null,

    // Navigation interne
    val typeActif        : TypeRapport     = TypeRapport.MENSUEL,

    // Sélecteur de période
    val moisSelectionne  : Int             = java.util.Calendar.getInstance()
                                                .get(java.util.Calendar.MONTH) + 1,
    val anneeSelectionnee: Int             = java.util.Calendar.getInstance()
                                                .get(java.util.Calendar.YEAR),

    // Rapport mensuel
    val donneesMois      : DonneesMois?    = null,
    val repartitionProjet: List<Pair<String, Double>> = emptyList(),
    val repartitionMode  : List<Pair<String, Double>> = emptyList(),

    // Rapport annuel — 12 mois
    val donnees12Mois    : List<DonneesMois> = emptyList(),
    val totalAnneeEntCdf : Double = 0.0,
    val totalAnneeSorCdf : Double = 0.0,

    // État exports
    val exportEnCours    : Boolean         = false,
    val exportSucces     : String?         = null,   // nom du fichier exporté
    val exportErreur     : String?         = null,
)
