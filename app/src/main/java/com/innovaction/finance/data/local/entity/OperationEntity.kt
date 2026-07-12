package com.innovaction.finance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Opération financière — table centrale de l'application.
 *
 * type : ENTREE | SORTIE | TRANSFERT | FRAIS
 *   → seul enum technique, pas métier (pas de "Cotisation" ni "Décaissement" ici)
 *
 * La catégorie métier (Cotisation, Subvention, Fournitures…) est portée
 * par la table categories, configurable depuis l'application.
 *
 * Relations :
 *   compteId      → compte débité/crédité (obligatoire)
 *   compteDestId  → compte destinataire pour les transferts (optionnel)
 *   projetId      → projet associé (optionnel)
 *   categorieId   → catégorie métier (obligatoire)
 *   modePaiementId→ mode de paiement (obligatoire)
 *   deviseId      → devise de l'opération (obligatoire)
 *   federationId  → fédération concernée (optionnel)
 */
@Entity(
    tableName = "operations",
    foreignKeys = [
        ForeignKey(entity = CompteEntity::class,
            parentColumns = ["id"], childColumns = ["compteId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = CompteEntity::class,
            parentColumns = ["id"], childColumns = ["compteDestId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = ProjetEntity::class,
            parentColumns = ["id"], childColumns = ["projetId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = CategorieEntity::class,
            parentColumns = ["id"], childColumns = ["categorieId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = ModePaiementEntity::class,
            parentColumns = ["id"], childColumns = ["modePaiementId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = DeviseEntity::class,
            parentColumns = ["id"], childColumns = ["deviseId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = FederationEntity::class,
            parentColumns = ["id"], childColumns = ["federationId"], onDelete = ForeignKey.SET_NULL),
    ],
    indices = [
        Index("compteId"), Index("compteDestId"), Index("projetId"),
        Index("categorieId"), Index("modePaiementId"), Index("deviseId"),
        Index("federationId"), Index("date"),
    ]
)
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id             : Long    = 0,
    val numero         : String  = "",    // généré automatiquement : "2026-0001"
    val date           : Long,            // timestamp UTC
    val libelle        : String,
    val type           : String,          // "ENTREE" | "SORTIE" | "TRANSFERT" | "FRAIS"
    val montant        : Double,
    val compteId       : Long,
    val compteDestId   : Long?   = null,  // pour les transferts
    val projetId       : Long?   = null,
    val categorieId    : Long,
    val modePaiementId : Long,
    val deviseId       : Long,
    val federationId   : Long?   = null,
    val numeroPiece    : String  = "",
    val remarques      : String  = "",
    val createdAt      : Long    = System.currentTimeMillis(),
    val updatedAt      : Long    = System.currentTimeMillis(),
)
