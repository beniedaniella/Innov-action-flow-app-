package com.innovaction.finance.presentation.projets.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.projets.ProjetStats
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovGold
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat

private fun fmt(v: Double) = DecimalFormat("#,###").format(v)

@Composable
fun ProjetCard(
    stats    : ProjetStats,
    onClick  : () -> Unit,
    onEdit   : () -> Unit,
    onDelete : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val couleur = runCatching {
        Color(android.graphics.Color.parseColor(stats.projet.couleur))
    }.getOrDefault(InnovNavy)

    val pct       = stats.pctBudgetCdf
    val barColor  = when {
        pct >= 1.0f -> ColorSortie
        pct >= 0.8f -> InnovGold
        else        -> couleur
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp),
    ) {
        Column(Modifier.padding(18.dp)) {

            // ── En-tête ──────────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Pastille couleur
                    Surface(
                        modifier = Modifier.size(14.dp),
                        shape    = MaterialTheme.shapes.extraSmall,
                        color    = couleur,
                    ) {}
                    Text(
                        stats.projet.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.MoreVert, "Options",
                            modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text    = { Text("Modifier le projet") },
                            onClick = { menuExpanded = false; onEdit() },
                            leadingIcon = { Icon(Icons.Filled.Edit, null) },
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text    = { Text("Désactiver",
                                color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onDelete() },
                        )
                    }
                }
            }

            if (stats.projet.description.isNotBlank()) {
                Text(
                    stats.projet.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Spacer(Modifier.height(14.dp))

            // ── Budget CDF ────────────────────────────────────────────────
            if (stats.projet.budgetCdf > 0) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Budget CDF", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "%.0f%%".format(pct * 100),
                        style = MaterialTheme.typography.labelSmall,
                        color = barColor, fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress     = { pct.coerceIn(0f, 1f) },
                    modifier     = Modifier.fillMaxWidth().height(8.dp),
                    color        = barColor,
                    trackColor   = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${fmt(stats.sortiesCdf)} FC dépensés",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("/ ${fmt(stats.projet.budgetCdf)} FC",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
            }

            HorizontalDivider()
            Spacer(Modifier.height(10.dp))

            // ── Résumé ────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                StatMini("Recettes", "${fmt(stats.entreesCdf)} FC",
                    MaterialTheme.colorScheme.tertiary)
                StatMini("Dépenses", "${fmt(stats.sortiesCdf)} FC",
                    MaterialTheme.colorScheme.error)
                StatMini("Solde", "${fmt(stats.soldeCdf)} FC",
                    if (stats.soldeCdf >= 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error)
                StatMini("Opérations", "${stats.nbOperations}",
                    MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // ── Statut budget ─────────────────────────────────────────────
            if (stats.projet.budgetCdf > 0) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = when {
                        pct >= 1.0f -> MaterialTheme.colorScheme.errorContainer
                        pct >= 0.8f -> MaterialTheme.colorScheme.secondaryContainer
                        else        -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                ) {
                    Text(
                        stats.statutBudget,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = when {
                            pct >= 1.0f -> MaterialTheme.colorScheme.onErrorContainer
                            pct >= 0.8f -> MaterialTheme.colorScheme.onSecondaryContainer
                            else        -> MaterialTheme.colorScheme.onTertiaryContainer
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMini(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelMedium,
            color = color, fontWeight = FontWeight.Bold)
    }
}
