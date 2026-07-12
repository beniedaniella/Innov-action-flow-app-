package com.innovaction.finance.util

/**
 * Constantes TECHNIQUES uniquement.
 * Aucune valeur métier (nom de projet, devise, taux…) ne figure ici.
 * Toute donnée métier est stockée et lue depuis Room.
 */
object AppConstants {

    // Base de données
    const val DB_NAME             = "innovaction_finance.db"
    const val DB_VERSION          = 1

    // DataStore (préférences UI)
    const val PREFS_NAME          = "innovaction_prefs"
    const val PREF_DARK_MODE      = "pref_dark_mode"        // Boolean
    const val PREF_BIOMETRIC      = "pref_biometric_enabled" // Boolean (étape 15)

    // Pagination
    const val PAGE_SIZE           = 30

    // Formats d'affichage (localisables si besoin)
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMAT_EXPORT  = "yyyy-MM-dd"
}
