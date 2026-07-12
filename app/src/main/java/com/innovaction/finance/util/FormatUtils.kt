package com.innovaction.finance.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilitaires de formatage centralisés.
 * Utilisés partout dans l'UI — jamais de formatage ad-hoc dans les Composables.
 */
object FormatUtils {

    private val fmtEntier    = DecimalFormat("#,###")
    private val fmtDecimal   = DecimalFormat("#,###.##")
    private val fmtDate      = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fmtDateCourt = SimpleDateFormat("dd/MM", Locale.getDefault())
    private val fmtDateHeure = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val fmtMois      = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
    private val fmtMoisCourt = SimpleDateFormat("MMM", Locale.FRENCH)

    fun montant(valeur: Double, devise: String, avecSigne: Boolean = false): String {
        val signe  = if (avecSigne && valeur > 0) "+" else if (avecSigne && valeur < 0) "" else ""
        val nombre = fmtDecimal.format(valeur)
        return when {
            devise == "USD" -> "$signe$$nombre"
            else            -> "$signe$nombre $devise"
        }
    }

    fun montantCdf(valeur: Double, avecSigne: Boolean = false): String =
        montant(valeur, "FC", avecSigne)

    fun montantUsd(valeur: Double, avecSigne: Boolean = false): String =
        montant(valeur, "USD", avecSigne)

    fun entier(valeur: Double): String = fmtEntier.format(valeur)

    fun date(timestamp: Long): String = fmtDate.format(Date(timestamp))
    fun dateCourt(timestamp: Long): String = fmtDateCourt.format(Date(timestamp))
    fun dateHeure(timestamp: Long): String = fmtDateHeure.format(Date(timestamp))
    fun mois(timestamp: Long): String = fmtMois.format(Date(timestamp))
        .replaceFirstChar { it.uppercase() }
    fun moisCourt(timestamp: Long): String = fmtMoisCourt.format(Date(timestamp))
        .replaceFirstChar { it.uppercase() }

    fun pourcentage(valeur: Float): String = "%.1f%%".format(valeur * 100)

    fun joursDepuis(timestamp: Long): String {
        val jours = ((System.currentTimeMillis() - timestamp) /
                     (1000L * 60 * 60 * 24)).toInt()
        return when {
            jours == 0  -> "Aujourd'hui"
            jours == 1  -> "Hier"
            jours < 30  -> "Il y a $jours jour${if (jours > 1) "s" else ""}"
            jours < 365 -> "Il y a ${jours / 30} mois"
            else        -> "Il y a ${jours / 365} an${if (jours / 365 > 1) "s" else ""}"
        }
    }

    fun joursAvant(timestamp: Long): String {
        val jours = ((timestamp - System.currentTimeMillis()) /
                     (1000L * 60 * 60 * 24)).toInt()
        return when {
            jours < 0   -> "Échu il y a ${-jours} jour${if (-jours > 1) "s" else ""}"
            jours == 0  -> "Aujourd'hui"
            jours == 1  -> "Demain"
            else        -> "Dans $jours jour${if (jours > 1) "s" else ""}"
        }
    }
}
