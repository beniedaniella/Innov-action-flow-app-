package com.innovaction.finance.presentation.projets.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.relation.OperationWithDetails
import com.innovaction.finance.presentation.projets.ProjetMoisData
import com.innovaction.finance.presentation.projets.ProjetStats
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

private val fmtDate   = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
private val fmtAmount = DecimalFormat("#,###.##")
private fun fmt(v: Double) = DecimalFormat("#,###").format(v)

@Composable
fun ProjetDetailView(
    stats      : ProjetStats,
    operations : List<OperationWithDetails>,
    graphique  : List<ProjetMoisData>,
    onBack     : () -> Unit,
    onEdit     : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    val couleur = runCatching {
        Color(android.graphics.Color.parseColor(stats.projet.couleur))
    }.getOrDefault(InnovNavy)

    LazyColumn(modifier.fillMaxSize()) {

        // ── En-tête coloré ────────────────────────────────────────────────
        item {
            Surface(color = couleur, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour",
                                tint = Color.White)
                        }
                        Text(
                            stats.projet.nom,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White, fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Filled.Edit, "Modifier", tint = Color.White)
                        }
                    }
                    if (stats.projet.description.isNotBlank()) {
                        Text(stats.projet.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 48.dp))
                    }
                }
            }
        }

        // ── KPIs ─────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                KpiTile("Recettes
CDF", "${fmt(stats.entreesCdf)} FC",
                    ColorEntree, Modifier.weight(1f))
                KpiTile("Dépenses
CDF", "${fmt(stats.sortiesCdf)} FC",
                    ColorSortie, Modifier.weight(1f))
                KpiTile("Solde
CDF",
                    "${fmt(stats.soldeCdf)} FC",
                    if (stats.soldeCdf >= 0) MaterialTheme.colorScheme.primary
                    else ColorSortie,
                    Modifier.weight(1f))
            }
        }

        // ── Budget CDF ────────────────────────────────────────────────────
        if (stats.projet.budgetCdf > 0) {
            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Budget CDF", style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                            Text("%.0f%%".format(stats.pctBudgetCdf * 100),
                                style = MaterialTheme.typography.titleSmall,
                                color = if (stats.pctBudgetCdf >= 1f) ColorSortie else couleur,
                                fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress   = { stats.pctBudgetCdf.coerceIn(0f, 1f) },
                            modifier   = Modifier.fillMaxWidth().height(10.dp),
                            color      = if (stats.pctBudgetCdf >= 1f) ColorSortie else couleur,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${fmt(stats.sortiesCdf)} FC dépensés",
                                style = MaterialTheme.typography.bodySmall)
                            Text("Budget : ${fmt(stats.projet.budgetCdf)} FC",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Restant : ${fmt((stats.projet.budgetCdf - stats.sortiesCdf).coerceAtLeast(0.0))} FC",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (stats.pctBudgetCdf >= 1f) ColorSortie
                                    else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // ── Graphique 6 mois ──────────────────────────────────────────────
        if (graphique.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Activité — 6 derniers mois (CDF)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        ProjetBarChart(graphique, couleur)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // ── Budget USD ────────────────────────────────────────────────────
        if (stats.projet.budgetUsd > 0) {
            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Budget USD", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress   = { stats.pctBudgetUsd.coerceIn(0f, 1f) },
                            modifier   = Modifier.fillMaxWidth().height(8.dp),
                            color      = if (stats.pctBudgetUsd >= 1f) ColorSortie else couleur,
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$${fmtAmount.format(stats.sortiesUsd)} dépensés",
                                style = MaterialTheme.typography.bodySmall)
                            Text("Budget : $${fmtAmount.format(stats.projet.budgetUsd)}",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // ── Opérations du projet ──────────────────────────────────────────
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically) {
                Text("${operations.size} opération(s)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
            }
        }

        if (operations.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center) {
                    Text("Aucune opération pour ce projet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                }
            }
        } else {
            items(operations, key = { it.operation.id }) { op ->
                val isEntree = op.operation.type == "ENTREE"
                val color    = if (isEntree) ColorEntree else ColorSortie
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(op.operation.libelle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text("${fmtDate.format(Date(op.operation.date))} · ${op.categorie.nom}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "${if (isEntree) "+" else "-"}${fmtAmount.format(op.operation.montant)} ${op.devise.symbole}",
                        style = MaterialTheme.typography.titleSmall,
                        color = color, fontWeight = FontWeight.ExtraBold,
                    )
                }
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun KpiTile(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.labelMedium,
                color = color, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProjetBarChart(data: List<ProjetMoisData>, couleur: Color) {
    val maxVal = data.maxOf { maxOf(it.entreesCdf, it.sortiesCdf) }.coerceAtLeast(1.0)

    Canvas(Modifier.fillMaxWidth().height(80.dp)) {
        val gW = size.width / data.size
        val bW = gW * 0.32f
        val gap = gW * 0.04f
        data.forEachIndexed { idx, m ->
            val x = idx * gW + gap
            val hE = ((m.entreesCdf / maxVal) * size.height).toFloat().coerceAtLeast(4f)
            val hS = ((m.sortiesCdf / maxVal) * size.height).toFloat().coerceAtLeast(4f)
            drawRoundRect(color = couleur.copy(alpha = 0.7f),
                topLeft = Offset(x, size.height - hE),
                size    = Size(bW, hE), cornerRadius = CornerRadius(4f))
            drawRoundRect(color = ColorSortie.copy(alpha = 0.7f),
                topLeft = Offset(x + bW + gap, size.height - hS),
                size    = Size(bW, hS), cornerRadius = CornerRadius(4f))
        }
    }
    Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        data.forEach { m ->
            Text(m.label, Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
