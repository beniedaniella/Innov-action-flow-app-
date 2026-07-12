package com.innovaction.finance.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.innovaction.finance.data.local.entity.*

data class AvanceWithDetails(
    @Embedded val avance : AvanceEntity,

    @Relation(parentColumn = "deviseId",  entityColumn = "id")
    val devise  : DeviseEntity,

    @Relation(parentColumn = "projetId",  entityColumn = "id")
    val projet  : ProjetEntity?,
)
