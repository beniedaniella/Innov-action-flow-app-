package com.innovaction.finance.data.export

import android.content.Context
import android.net.Uri

/** Résultat d'un export. */
sealed class ExportResult {
    data class Success(val uri: Uri, val fileName: String) : ExportResult()
    data class Error(val message: String, val cause: Throwable? = null) : ExportResult()
}

/** Interface commune à tous les services d'export. */
interface ExportService {
    /** Génère le fichier et retourne son URI (partage ou FileProvider). */
    suspend fun exporterJournal(
        context  : Context,
        donnees  : DonneesExport,
    ): ExportResult

    suspend fun exporterRapportMensuel(
        context  : Context,
        donnees  : DonneesExport,
        mois     : Int,
        annee    : Int,
    ): ExportResult

    suspend fun exporterRapportAnnuel(
        context  : Context,
        donnees  : DonneesExport,
        annee    : Int,
    ): ExportResult
}

/** Données consolidées passées aux services d'export. */
data class DonneesExport(
    val nomAssociation  : String,
    val exercice        : String,
    val operations      : List<OperationExport>,
    val avances         : List<AvanceExport>,
    val projets         : List<ProjetExport>,
    val comptes         : List<CompteExport>,
)

data class OperationExport(
    val numero         : String,
    val date           : String,
    val libelle        : String,
    val type           : String,
    val categorie      : String,
    val montant        : Double,
    val devise         : String,
    val compte         : String,
    val modePaiement   : String,
    val projet         : String,
    val federation     : String,
    val numeroPiece    : String,
    val remarques      : String,
)

data class AvanceExport(
    val numero         : String,
    val beneficiaire   : String,
    val objet          : String,
    val montant        : Double,
    val devise         : String,
    val dateEmission   : String,
    val dateEcheance   : String,
    val statut         : String,
    val montantRembourse: Double,
    val projet         : String,
    val numeroDecharge : String,
)

data class ProjetExport(
    val nom            : String,
    val budgetCdf      : Double,
    val budgetUsd      : Double,
    val entreesCdf     : Double,
    val sortiesCdf     : Double,
    val soldeCdf       : Double,
)

data class CompteExport(
    val nom            : String,
    val devise         : String,
    val solde          : Double,
)
