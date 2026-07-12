package com.innovaction.finance.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.innovaction.finance.data.local.entity.*

data class CompteWithDevise(
    @Embedded val compte : CompteEntity,

    @Relation(parentColumn = "deviseId", entityColumn = "id")
    val devise : DeviseEntity,
)
