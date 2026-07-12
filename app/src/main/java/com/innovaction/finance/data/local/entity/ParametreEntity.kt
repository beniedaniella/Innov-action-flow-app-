package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Paramètres configurables de l'application.
 * Stockés en base (clé/valeur) plutôt qu'en DataStore pour pouvoir
 * être exportés, sauvegardés et restaurés avec les données.
 *
 * Exemples de clés :
 *   "seuil_alerte_cdf"    → "500000"
 *   "seuil_alerte_usd"    → "150"
 *   "nom_association"     → "INNOV'ACTION"
 *   "exercice_en_cours"   → "2026"
 *   "devise_principale_id"→ "1"
 */
@Entity(tableName = "parametres")
data class ParametreEntity(
    @PrimaryKey
    val cle         : String,
    val valeur      : String,
    val description : String  = "",
    val categorie   : String  = "GENERAL",  // GENERAL | ALERTES | EXPORT | SECURITE
    val updatedAt   : Long    = System.currentTimeMillis(),
)
