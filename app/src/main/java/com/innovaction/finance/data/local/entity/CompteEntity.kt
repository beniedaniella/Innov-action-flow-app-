package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Compte de caisse (Espèces CDF, Espèces USD, M-Pesa, Airtel, Banque CDF, Banque USD…).
 * Chaque compte est lié à une devise et a un solde initial configurable.
 * Aucun compte n'est créé en dur dans le code — tous viennent du Seeder ou des Paramètres.
 */
@Entity(
    tableName = "comptes",
    foreignKeys = [ForeignKey(entity = DeviseEntity::class,
        parentColumns = ["id"], childColumns = ["deviseId"],
        onDelete = ForeignKey.RESTRICT)],
    indices = [Index("deviseId")]
)
data class CompteEntity(
    @PrimaryKey(autoGenerate = true)
    val id           : Long    = 0,
    val nom          : String,
    val deviseId     : Long,
    val soldeInitial : Double  = 0.0,
    val icone        : String  = "account_balance_wallet",
    val couleur      : String  = "#1F3864",
    val isActive     : Boolean = true,
    val ordre        : Int     = 0,
)
