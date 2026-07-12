package com.innovaction.finance.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.innovaction.finance.data.local.entity.*

/** Opération enrichie avec toutes ses entités liées — utilisée pour l'affichage. */
data class OperationWithDetails(
    @Embedded val operation: OperationEntity,

    @Relation(parentColumn = "compteId",       entityColumn = "id")
    val compte        : CompteEntity,

    @Relation(parentColumn = "categorieId",    entityColumn = "id")
    val categorie     : CategorieEntity,

    @Relation(parentColumn = "modePaiementId", entityColumn = "id")
    val modePaiement  : ModePaiementEntity,

    @Relation(parentColumn = "deviseId",       entityColumn = "id")
    val devise        : DeviseEntity,

    @Relation(parentColumn = "projetId",       entityColumn = "id")
    val projet        : ProjetEntity?,

    @Relation(parentColumn = "federationId",   entityColumn = "id")
    val federation    : FederationEntity?,

    @Relation(parentColumn = "compteDestId",   entityColumn = "id")
    val compteDest    : CompteEntity?,
)
