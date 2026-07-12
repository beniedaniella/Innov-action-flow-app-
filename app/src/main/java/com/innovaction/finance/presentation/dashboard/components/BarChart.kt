package com.innovaction.finance.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.dashboard.MoisData
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovNavy

/**
 * Graphique en barres groupées — Entrées vs Sorties CDF par mois.
 * Construit avec Canvas Compose, sans dépendance externe.
 */
@Composable
fun BarChart(
    data     : List<MoisData>,
    modifier : Modifier = Modifier,
) {
    if (data.isEmpty()) return

    val maxVal = data.maxOf { maxOf(it.entreesCdf, it.sortiesCdf) }.coerceAtLeast(1.0)

    Column(modifier) {
        // Légende
        Row(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            LegendDot(ColorEntree, "Entrées")
            Spacer(Modifier.width(16.dp))
            LegendDot(ColorSortie, "Sorties")
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 4.dp)
        ) {
            val canvasW    = size.width
            val canvasH    = size.height
            val groupW     = canvasW / data.size
            val barW       = groupW * 0.32f
            val gap        = groupW * 0.04f

            data.forEachIndexed { idx, mois ->
                val x = idx * groupW + gap

                // Barre Entrées
                val hE = ((mois.entreesCdf / maxVal) * canvasH).toFloat().coerceAtLeast(4f)
                drawRoundRect(
                    color       = ColorEntree,
                    topLeft     = Offset(x, canvasH - hE),
                    size        = Size(barW, hE),
                    cornerRadius = CornerRadius(4f, 4f),
                )

                // Barre Sorties
                val hS = ((mois.sortiesCdf / maxVal) * canvasH).toFloat().coerceAtLeast(4f)
                drawRoundRect(
                    color       = ColorSortie,
                    topLeft     = Offset(x + barW + gap, canvasH - hS),
                    size        = Size(barW, hS),
                    cornerRadius = CornerRadius(4f, 4f),
                )
            }
        }

        // Labels des mois
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            data.forEach { mois ->
                Text(
                    text      = mois.label,
                    modifier  = Modifier.weight(1f),
                    style     = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(10.dp)) {
            drawRoundRect(color = color, cornerRadius = CornerRadius(3f))
        }
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
