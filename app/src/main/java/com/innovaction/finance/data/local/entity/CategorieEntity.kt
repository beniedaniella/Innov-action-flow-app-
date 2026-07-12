package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Catégorie d'opération (Cotisation, Subvention, Fournitures, Salaire…).
 * Le type d'opération associé guide la catégorie par défaut dans les formulaires.
 * Configurable depuis les Paramètres — aucune valeur codée en dur.
 */
@Entity(tableName = "categories")
data class CategorieEntity(
    @PrimaryKey(autoGenerate = true)
    val id          : Long    = 0,
    val nom         : String,
    val typeDefaut  : String,   // "ENTREE" | "SORTIE" | "TOUS"
    val icone       : String   = "label",
    val couleur     : String   = "#1F3864",  // hex
    val isActive    : Boolean  = true,
    val ordre       : Int      = 0,
)
