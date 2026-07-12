package com.innovaction.finance.presentation.securite

enum class EtatVerrou { DEVERROUILLE, VERROUILLE, CONFIGURATION_PIN }

data class SecuriteUiState(
    val isLoading         : Boolean          = true,
    val etat              : EtatVerrou       = EtatVerrou.DEVERROUILLE,
    val pinEnabled        : Boolean          = false,
    val biometricEnabled  : Boolean          = false,
    val biometricDispo    : Boolean          = false,

    // Saisie PIN
    val pinSaisi          : String           = "",   // jamais stocké — seulement en mémoire
    val pinConfirmation   : String           = "",
    val etapeConfirmation : Boolean          = false,  // true = re-saisir pour confirmer
    val erreur            : String?          = null,
    val succesMessage     : String?          = null,
    val isSaving          : Boolean          = false,
)
