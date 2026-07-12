package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Devise (CDF, USD, EUR…).
 * Toutes les devises sont configurables depuis l'application.
 * Aucune devise n'est codée en dur dans la logique métier.
 */
@Entity(tableName = "devises")
data class DeviseEntity(
    @PrimaryKey(autoGenerate = true)
    val id       : Long   = 0,
    val code     : String,          // "CDF", "USD"
    val nom      : String,          // "Franc Congolais", "Dollar américain"
    val symbole  : String,          // "FC", "$"
    val isActive : Boolean = true,
    val ordre    : Int     = 0,     // ordre d'affichage
)
