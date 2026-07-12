package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Fédération de l'association.
 * Configurable depuis les Paramètres — aucune valeur codée en dur.
 */
@Entity(tableName = "federations")
data class FederationEntity(
    @PrimaryKey(autoGenerate = true)
    val id          : Long    = 0,
    val nom         : String,
    val description : String  = "",
    val contact     : String  = "",
    val isActive    : Boolean = true,
    val ordre       : Int     = 0,
)
