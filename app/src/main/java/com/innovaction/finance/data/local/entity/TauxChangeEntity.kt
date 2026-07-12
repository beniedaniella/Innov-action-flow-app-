package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Taux de change entre deux devises.
 * Mis à jour manuellement depuis les Paramètres de l'application.
 * Ex : 1 USD = 2800 CDF → deviseSource="USD", deviseCible="CDF", taux=2800.0
 */
@Entity(
    tableName = "taux_change",
    foreignKeys = [
        ForeignKey(entity = DeviseEntity::class,
            parentColumns  = ["id"], childColumns  = ["deviseSourceId"],
            onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = DeviseEntity::class,
            parentColumns  = ["id"], childColumns  = ["deviseCibleId"],
            onDelete = ForeignKey.RESTRICT),
    ],
    indices = [Index("deviseSourceId"), Index("deviseCibleId")]
)
data class TauxChangeEntity(
    @PrimaryKey(autoGenerate = true)
    val id             : Long   = 0,
    val deviseSourceId : Long,
    val deviseCibleId  : Long,
    val taux           : Double,
    val dateEffet      : Long = System.currentTimeMillis(),  // timestamp
)
