package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Projet de l'association (Personnalité Juridique, Confestival…).
 * Chaque projet a un budget CDF et USD configurables depuis l'application.
 * Aucun nom ni budget n'est codé en dur dans la logique métier.
 */
@Entity(tableName = "projets")
data class ProjetEntity(
    @PrimaryKey(autoGenerate = true)
    val id          : Long    = 0,
    val nom         : String,
    val description : String  = "",
    val budgetCdf   : Double  = 0.0,
    val budgetUsd   : Double  = 0.0,
    val dateDebut   : Long?   = null,
    val dateFin     : Long?   = null,
    val isActive    : Boolean = true,
    val couleur     : String  = "#1F3864",
    val ordre       : Int     = 0,
)
