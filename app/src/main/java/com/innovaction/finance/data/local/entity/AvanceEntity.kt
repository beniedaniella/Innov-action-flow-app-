package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Avance / décharge remise à un membre.
 *
 * statut : ACTIVE | REMBOURSEE_PARTIELLE | REMBOURSEE_TOTALE | EN_LITIGE
 *   → enum technique de suivi, géré par l'application
 *
 * operationId : opération de sortie associée (optionnel — liaison avec le Journal)
 */
@Entity(
    tableName = "avances",
    foreignKeys = [
        ForeignKey(entity = ProjetEntity::class,
            parentColumns = ["id"], childColumns = ["projetId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = DeviseEntity::class,
            parentColumns = ["id"], childColumns = ["deviseId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = OperationEntity::class,
            parentColumns = ["id"], childColumns = ["operationId"], onDelete = ForeignKey.SET_NULL),
    ],
    indices = [Index("projetId"), Index("deviseId"), Index("operationId"), Index("dateEcheance")]
)
data class AvanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id               : Long    = 0,
    val numero           : String  = "",   // "AVA-001"
    val beneficiaire     : String,
    val objet            : String,
    val montant          : Double,
    val montantRembourse : Double  = 0.0,
    val deviseId         : Long,
    val projetId         : Long?   = null,
    val operationId      : Long?   = null,
    val dateEmission     : Long,
    val dateEcheance     : Long,
    val dateRemboursement: Long?   = null,
    val statut           : String  = "ACTIVE",  // ACTIVE | REMBOURSEE_PARTIELLE | REMBOURSEE_TOTALE | EN_LITIGE
    val numeroDecharge   : String  = "",
    val remarques        : String  = "",
    val createdAt        : Long    = System.currentTimeMillis(),
    val updatedAt        : Long    = System.currentTimeMillis(),
)
