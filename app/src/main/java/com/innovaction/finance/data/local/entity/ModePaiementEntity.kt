package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mode de paiement (Espèces, M-Pesa, Airtel Money, Banque…).
 * Configurable depuis les Paramètres — aucune valeur codée en dur.
 */
@Entity(tableName = "modes_paiement")
data class ModePaiementEntity(
    @PrimaryKey(autoGenerate = true)
    val id       : Long    = 0,
    val nom      : String,
    val icone    : String  = "payments",  // nom d'icône Material
    val isActive : Boolean = true,
    val ordre    : Int     = 0,
)
