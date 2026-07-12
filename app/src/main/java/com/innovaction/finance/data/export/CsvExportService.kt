package com.innovaction.finance.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Export CSV — compatible Excel et Google Sheets.
 * Séparateur : point-virgule (standard européen/africain).
 * Encodage : UTF-8 avec BOM pour la compatibilité Excel Windows.
 */
@Singleton
class CsvExportService @Inject constructor() : ExportService {

    private val fmt = DecimalFormat("#,###.##")

    override suspend fun exporterJournal(
        context: Context,
        donnees: DonneesExport,
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val fileName = "${donnees.nomAssociation.replace(" ", "_")}_Journal_${donnees.exercice}.csv"
            val file     = File(context.cacheDir, fileName)

            FileWriter(file, Charsets.UTF_8).use { writer ->
                // BOM UTF-8 pour compatibilité Excel
                writer.write("﻿")

                // En-tête
                writer.writeLine("JOURNAL DE CAISSE — ${donnees.nomAssociation} — Exercice ${donnees.exercice}")
                writer.writeLine("Généré le : ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
                writer.writeLine("")

                // Colonnes
                writer.writeCsv(listOf(
                    "N° Opération", "Date", "Libellé", "Type", "Catégorie",
                    "Montant", "Devise", "Compte", "Mode de paiement",
                    "Projet", "Fédération", "N° Pièce", "Remarques"
                ))

                // Données
                donnees.operations.forEach { op ->
                    writer.writeCsv(listOf(
                        op.numero, op.date, op.libelle, op.type, op.categorie,
                        fmt.format(op.montant), op.devise, op.compte, op.modePaiement,
                        op.projet, op.federation, op.numeroPiece, op.remarques
                    ))
                }

                // Totaux
                writer.writeLine("")
                writer.writeLine(";TOTAL ENTRÉES CDF;${fmt.format(
                    donnees.operations.filter { it.type == "ENTREE" && it.devise == "CDF" }
                        .sumOf { it.montant })};CDF")
                writer.writeLine(";TOTAL SORTIES CDF;${fmt.format(
                    donnees.operations.filter { it.type == "SORTIE" && it.devise == "CDF" }
                        .sumOf { it.montant })};CDF")
                writer.writeLine(";TOTAL ENTRÉES USD;${fmt.format(
                    donnees.operations.filter { it.type == "ENTREE" && it.devise == "USD" }
                        .sumOf { it.montant })};USD")
                writer.writeLine(";TOTAL SORTIES USD;${fmt.format(
                    donnees.operations.filter { it.type == "SORTIE" && it.devise == "USD" }
                        .sumOf { it.montant })};USD")
            }

            ExportResult.Success(
                uri      = fileToUri(context, file),
                fileName = fileName,
            )
        } catch (e: Exception) {
            ExportResult.Error("Erreur export CSV : ${e.message}", e)
        }
    }

    override suspend fun exporterRapportMensuel(
        context : Context,
        donnees : DonneesExport,
        mois    : Int,
        annee   : Int,
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val nomMois  = java.text.SimpleDateFormat("MMMM", java.util.Locale.FRENCH)
                .format(java.util.Calendar.getInstance().also {
                    it.set(java.util.Calendar.MONTH, mois - 1) }.time)
                .replaceFirstChar { it.uppercase() }
            val fileName = "${donnees.nomAssociation.replace(" ", "_")}_Rapport_${nomMois}_$annee.csv"
            val file     = File(context.cacheDir, fileName)

            val opsMois = donnees.operations.filter { op ->
                val cal = java.util.Calendar.getInstance()
                // La date est déjà formatée dd/MM/yyyy
                val parts = op.date.split("/")
                if (parts.size == 3)
                    parts[1].toIntOrNull() == mois && parts[2].toIntOrNull() == annee
                else false
            }

            FileWriter(file, Charsets.UTF_8).use { writer ->
                writer.write("﻿")
                writer.writeLine("RAPPORT MENSUEL — $nomMois $annee")
                writer.writeLine("${donnees.nomAssociation}")
                writer.writeLine("Généré le : ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
                writer.writeLine("")

                // Résumé
                writer.writeLine("RÉSUMÉ DU MOIS")
                val entCdf = opsMois.filter { it.type=="ENTREE" && it.devise=="CDF"}.sumOf { it.montant }
                val sorCdf = opsMois.filter { it.type=="SORTIE" && it.devise=="CDF"}.sumOf { it.montant }
                val entUsd = opsMois.filter { it.type=="ENTREE" && it.devise=="USD"}.sumOf { it.montant }
                val sorUsd = opsMois.filter { it.type=="SORTIE" && it.devise=="USD"}.sumOf { it.montant }
                writer.writeLine("Total entrées CDF;${fmt.format(entCdf)};FC")
                writer.writeLine("Total sorties CDF;${fmt.format(sorCdf)};FC")
                writer.writeLine("Solde net CDF;${fmt.format(entCdf - sorCdf)};FC")
                writer.writeLine("Total entrées USD;${fmt.format(entUsd)};USD")
                writer.writeLine("Total sorties USD;${fmt.format(sorUsd)};USD")
                writer.writeLine("Nombre d'opérations;${opsMois.size}")
                writer.writeLine("")

                // Par projet
                writer.writeLine("PAR PROJET (CDF)")
                writer.writeCsv(listOf("Projet","Entrées CDF","Sorties CDF","Solde CDF"))
                val parProjet = opsMois.groupBy { it.projet }
                parProjet.forEach { (projet, ops) ->
                    val eP = ops.filter { it.type == "ENTREE" && it.devise == "CDF" }.sumOf { it.montant }
                    val sP = ops.filter { it.type == "SORTIE" && it.devise == "CDF" }.sumOf { it.montant }
                    writer.writeCsv(listOf(
                        projet.ifBlank { "Non assigné" },
                        fmt.format(eP), fmt.format(sP), fmt.format(eP - sP)
                    ))
                }
                writer.writeLine("")

                // Détail opérations
                writer.writeLine("DÉTAIL DES OPÉRATIONS")
                writer.writeCsv(listOf("N°","Date","Libellé","Type","Montant","Devise","Projet","Mode"))
                opsMois.forEach { op ->
                    writer.writeCsv(listOf(
                        op.numero, op.date, op.libelle, op.type,
                        fmt.format(op.montant), op.devise, op.projet, op.modePaiement
                    ))
                }
            }

            ExportResult.Success(fileToUri(context, file), fileName)
        } catch (e: Exception) {
            ExportResult.Error("Erreur : ${e.message}", e)
        }
    }

    override suspend fun exporterRapportAnnuel(
        context : Context,
        donnees : DonneesExport,
        annee   : Int,
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val fileName = "${donnees.nomAssociation.replace(" ", "_")}_Rapport_Annuel_$annee.csv"
            val file     = File(context.cacheDir, fileName)
            val opsAnnee = donnees.operations.filter { op ->
                val parts = op.date.split("/")
                parts.size == 3 && parts[2].toIntOrNull() == annee
            }

            FileWriter(file, Charsets.UTF_8).use { writer ->
                writer.write("﻿")
                writer.writeLine("RAPPORT ANNUEL $annee — ${donnees.nomAssociation}")
                writer.writeLine("")

                // Tableau 12 mois
                writer.writeLine("ACTIVITÉ PAR MOIS (CDF)")
                writer.writeCsv(listOf("Mois","Entrées CDF","Sorties CDF","Solde CDF"))
                val moisNoms = listOf("","Janv","Févr","Mars","Avr","Mai","Juin",
                    "Juil","Août","Sept","Oct","Nov","Déc")
                for (m in 1..12) {
                    val opsMois = opsAnnee.filter { op ->
                        val parts = op.date.split("/")
                        parts.size == 3 && parts[1].toIntOrNull() == m
                    }
                    val e = opsMois.filter { it.type=="ENTREE" && it.devise=="CDF"}.sumOf { it.montant }
                    val s = opsMois.filter { it.type=="SORTIE" && it.devise=="CDF"}.sumOf { it.montant }
                    writer.writeCsv(listOf(moisNoms[m], fmt.format(e), fmt.format(s), fmt.format(e-s)))
                }
                writer.writeLine("")

                // Résumé par projet
                writer.writeLine("PAR PROJET — Exécution budgétaire")
                writer.writeCsv(listOf("Projet","Budget CDF","Recettes CDF","Dépenses CDF","Solde CDF","% Exécution"))
                donnees.projets.forEach { p ->
                    val pct = if (p.budgetCdf > 0) "%.1f%%".format(p.sortiesCdf / p.budgetCdf * 100) else "—"
                    writer.writeCsv(listOf(
                        p.nom, fmt.format(p.budgetCdf), fmt.format(p.entreesCdf),
                        fmt.format(p.sortiesCdf), fmt.format(p.soldeCdf), pct
                    ))
                }
                writer.writeLine("")

                // Avances en cours
                val avancesActives = donnees.avances.filter { it.statut == "ACTIVE" }
                if (avancesActives.isNotEmpty()) {
                    writer.writeLine("AVANCES NON REMBOURSÉES")
                    writer.writeCsv(listOf("N°","Bénéficiaire","Objet","Montant","Devise","Échéance","Restant dû"))
                    avancesActives.forEach { av ->
                        writer.writeCsv(listOf(
                            av.numero, av.beneficiaire, av.objet,
                            fmt.format(av.montant), av.devise, av.dateEcheance,
                            fmt.format(av.montant - av.montantRembourse)
                        ))
                    }
                }
            }

            ExportResult.Success(fileToUri(context, file), fileName)
        } catch (e: Exception) {
            ExportResult.Error("Erreur : ${e.message}", e)
        }
    }

    private fun FileWriter.writeLine(line: String) {
        write(line + "
")
    }

    private fun FileWriter.writeCsv(cols: List<String>) {
        write(cols.joinToString(";") { ""${it.replace(""", "'''"''"'')}"" } + "
")
    }

    private fun fileToUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
}
