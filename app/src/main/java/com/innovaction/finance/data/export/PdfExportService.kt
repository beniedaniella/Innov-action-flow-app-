package com.innovaction.finance.data.export

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Export PDF via android.graphics.pdf.PdfDocument (API intégrée Android).
 * Aucune dépendance externe requise.
 */
@Singleton
class PdfExportService @Inject constructor() {

    private val fmt    = DecimalFormat("#,###.##")
    private val fmtDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fmtNow  = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // ── Couleurs INNOV'ACTION ─────────────────────────────────────────────
    private val NAVY   = Color.rgb(31,  56,  100)
    private val GOLD   = Color.rgb(212, 160, 23)
    private val GREEN  = Color.rgb(22,  163, 74)
    private val RED    = Color.rgb(220, 38,  38)
    private val LGREY  = Color.rgb(243, 245, 249)
    private val DGREY  = Color.rgb(107, 114, 128)
    private val WHITE  = Color.WHITE
    private val BLACK  = Color.BLACK

    // ── Page A4 en points ─────────────────────────────────────────────────
    private val PAGE_W = 595
    private val PAGE_H = 842
    private val MARGIN = 40f
    private val COL_W  = PAGE_W - 2 * MARGIN

    suspend fun exporterRapportMensuel(
        context : Context,
        donnees : DonneesExport,
        mois    : Int,
        annee   : Int,
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val nomMois = SimpleDateFormat("MMMM", Locale.FRENCH)
                .format(Calendar.getInstance().also {
                    it.set(Calendar.MONTH, mois - 1) }.time)
                .replaceFirstChar { it.uppercase() }

            val fileName = "${donnees.nomAssociation.replace(" ", "_")}_Rapport_${nomMois}_$annee.pdf"
            val file     = File(context.cacheDir, fileName)

            val doc  = android.graphics.pdf.PdfDocument()
            val info = android.graphics.pdf.PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create()
            val page = doc.startPage(info)
            val c    = page.canvas

            var y = MARGIN

            // ── En-tête ────────────────────────────────────────────────────
            y = drawHeader(c, y, donnees.nomAssociation, "Rapport mensuel — $nomMois $annee")

            // ── Résumé ─────────────────────────────────────────────────────
            val opsMois = donnees.operations.filter { op ->
                val parts = op.date.split("/")
                parts.size == 3 && parts[1].toIntOrNull() == mois && parts[2].toIntOrNull() == annee
            }
            val entCdf = opsMois.filter { it.type=="ENTREE" && it.devise=="CDF"}.sumOf { it.montant }
            val sorCdf = opsMois.filter { it.type=="SORTIE" && it.devise=="CDF"}.sumOf { it.montant }
            val entUsd = opsMois.filter { it.type=="ENTREE" && it.devise=="USD"}.sumOf { it.montant }
            val sorUsd = opsMois.filter { it.type=="SORTIE" && it.devise=="USD"}.sumOf { it.montant }

            y = drawSectionTitle(c, y, "Résumé du mois")
            y = drawKpiRow(c, y, listOf(
                Triple("Entrées CDF",  "${fmt.format(entCdf)} FC",  GREEN),
                Triple("Sorties CDF",  "${fmt.format(sorCdf)} FC",  RED),
                Triple("Solde net CDF","${fmt.format(entCdf - sorCdf)} FC",
                    if (entCdf >= sorCdf) GREEN else RED),
            ))
            y = drawKpiRow(c, y, listOf(
                Triple("Entrées USD",  "$${fmt.format(entUsd)}", GREEN),
                Triple("Sorties USD",  "$${fmt.format(sorUsd)}", RED),
                Triple("Opérations",  "${opsMois.size}",         NAVY),
            ))

            // ── Par projet ─────────────────────────────────────────────────
            y += 12f
            y = drawSectionTitle(c, y, "Répartition par projet (CDF)")
            y = drawTableHeader(c, y, listOf("Projet", "Entrées FC", "Sorties FC", "Solde FC"),
                listOf(0.35f, 0.22f, 0.22f, 0.21f))

            val parProjet = opsMois.groupBy { it.projet.ifBlank { "Non assigné" } }
            parProjet.entries.take(8).forEachIndexed { idx, (proj, ops) ->
                val eP = ops.filter { it.type=="ENTREE" && it.devise=="CDF" }.sumOf { it.montant }
                val sP = ops.filter { it.type=="SORTIE" && it.devise=="CDF" }.sumOf { it.montant }
                y = drawTableRow(c, y,
                    listOf(proj, fmt.format(eP), fmt.format(sP), fmt.format(eP - sP)),
                    listOf(0.35f, 0.22f, 0.22f, 0.21f),
                    isAlt = idx % 2 == 1,
                )
            }

            // ── Dernières opérations ───────────────────────────────────────
            y += 12f
            y = drawSectionTitle(c, y, "Opérations du mois (${opsMois.size})")
            y = drawTableHeader(c, y, listOf("Date", "Libellé", "Type", "Montant", "Devise"),
                listOf(0.12f, 0.38f, 0.12f, 0.22f, 0.16f))
            opsMois.take(20).forEachIndexed { idx, op ->
                if (y > PAGE_H - 80) return@forEachIndexed // sécurité dépassement
                y = drawTableRow(c, y,
                    listOf(op.date, op.libelle.take(30), op.type, fmt.format(op.montant), op.devise),
                    listOf(0.12f, 0.38f, 0.12f, 0.22f, 0.16f),
                    isAlt = idx % 2 == 1,
                    textColor = when (op.type) { "ENTREE" -> GREEN; "SORTIE" -> RED; else -> BLACK },
                )
            }
            if (opsMois.size > 20) {
                drawText(c, y + 8f, "… et ${opsMois.size - 20} autres opérations (voir export CSV pour le détail complet)",
                    12f, DGREY, Typeface.ITALIC)
            }

            // ── Pied de page ───────────────────────────────────────────────
            drawFooter(c, donnees.nomAssociation)

            doc.finishPage(page)
            FileOutputStream(file).use { doc.writeTo(it) }
            doc.close()

            ExportResult.Success(fileToUri(context, file), fileName)
        } catch (e: Exception) {
            ExportResult.Error("Erreur PDF : ${e.message}", e)
        }
    }

    // ══ Helpers de dessin ══════════════════════════════════════════════════

    private fun drawHeader(c: Canvas, y: Float, nomAsso: String, titre: String): Float {
        // Bande navy
        val paint = Paint().apply { color = NAVY; style = Paint.Style.FILL }
        c.drawRect(0f, 0f, PAGE_W.toFloat(), 80f, paint)

        // Logo/nom
        drawText(c, 22f, nomAsso, 18f, GOLD, Typeface.BOLD)
        drawText(c, 48f, titre, 13f, WHITE)
        drawText(c, 65f, "Généré le ${fmtNow.format(Date())}", 9f, WHITE.withAlpha(180))

        return 96f
    }

    private fun drawSectionTitle(c: Canvas, y: Float, title: String): Float {
        val paint = Paint().apply { color = NAVY; style = Paint.Style.FILL }
        c.drawRect(MARGIN, y, PAGE_W - MARGIN, y + 22f, paint)
        drawText(c, y + 15f, title, 11f, WHITE, Typeface.BOLD, MARGIN + 6f)
        return y + 30f
    }

    private fun drawKpiRow(c: Canvas, y: Float, kpis: List<Triple<String,String,Int>>): Float {
        val w = COL_W / kpis.size
        val bgPaint = Paint().apply { color = LGREY; style = Paint.Style.FILL }
        kpis.forEachIndexed { idx, (label, value, color) ->
            val x = MARGIN + idx * w + 4f
            c.drawRoundRect(x, y, x + w - 8f, y + 44f, 6f, 6f, bgPaint)
            drawText(c, y + 14f, label, 9f, DGREY, x = x + 8f)
            drawText(c, y + 32f, value, 12f, color, Typeface.BOLD, x + 8f)
        }
        return y + 52f
    }

    private fun drawTableHeader(c: Canvas, y: Float, cols: List<String>, ratios: List<Float>): Float {
        val paint = Paint().apply { color = Color.rgb(220,226,240); style = Paint.Style.FILL }
        c.drawRect(MARGIN, y, PAGE_W - MARGIN, y + 20f, paint)
        var x = MARGIN + 4f
        cols.forEachIndexed { i, col ->
            drawText(c, y + 14f, col, 9f, NAVY, Typeface.BOLD, x)
            x += ratios[i] * COL_W
        }
        return y + 22f
    }

    private fun drawTableRow(
        c        : Canvas,
        y        : Float,
        cols     : List<String>,
        ratios   : List<Float>,
        isAlt    : Boolean = false,
        textColor: Int = BLACK,
    ): Float {
        if (isAlt) {
            val p = Paint().apply { color = LGREY; style = Paint.Style.FILL }
            c.drawRect(MARGIN, y, PAGE_W - MARGIN, y + 18f, p)
        }
        var x = MARGIN + 4f
        cols.forEachIndexed { i, col ->
            drawText(c, y + 13f, col.take(if (i == 1) 32 else 20), 8f, textColor, x = x)
            x += ratios[i] * COL_W
        }
        val divider = Paint().apply { color = Color.rgb(230,230,230); strokeWidth = 0.5f }
        c.drawLine(MARGIN, y + 18f, PAGE_W - MARGIN.toFloat(), y + 18f, divider)
        return y + 20f
    }

    private fun drawFooter(c: Canvas, nomAsso: String) {
        val y = PAGE_H - 30f
        val paint = Paint().apply { color = LGREY; style = Paint.Style.FILL }
        c.drawRect(0f, y - 10f, PAGE_W.toFloat(), PAGE_H.toFloat(), paint)
        drawText(c, y + 8f, "$nomAsso — Document confidentiel — Généré par INNOV'ACTION Finance",
            8f, DGREY, x = MARGIN)
    }

    private fun drawText(
        c        : Canvas,
        y        : Float,
        text     : String,
        size     : Float,
        color    : Int,
        style    : Int = Typeface.NORMAL,
        x        : Float = MARGIN,
    ) {
        val paint = Paint().apply {
            this.color     = color
            textSize       = size * 2.2f  // points → pixels approximatif
            typeface       = Typeface.create(Typeface.DEFAULT, style)
            isAntiAlias    = true
        }
        c.drawText(text, x, y, paint)
    }

    private fun Int.withAlpha(alpha: Int): Int = Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))

    private fun fileToUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
