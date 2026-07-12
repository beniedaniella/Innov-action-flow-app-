package com.innovaction.finance.presentation.rapports.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.rapports.DonneesMois
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovNavy

@Composable
fun BarChartAnnuel(donnees: List<DonneesMois>, modifier: Modifier = Modifier) {
    if (donnees.isEmpty()) return
    val maxVal = donnees.maxOf { maxOf(it.entreesCdf, it.sortiesCdf) }.coerceAtLeast(1.0)

    Column(modifier) {
        // Légende
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
            LegendItem(ColorEntree, "Entrées CDF")
            Spacer(Modifier.width(16.dp))
            LegendItem(ColorSortie, "Sorties CDF")
        }

        Canvas(Modifier.fillMaxWidth().height(120.dp)) {
            val gW = size.width / donnees.size
            val bW = gW * 0.30f
            val gap = gW * 0.05f
            donnees.forEachIndexed { i, m ->
                val x  = i * gW + gap
                val hE = ((m.entreesCdf / maxVal) * size.height).toFloat().coerceAtLeast(4f)
                val hS = ((m.sortiesCdf / maxVal) * size.height).toFloat().coerceAtLeast(4f)
                drawRoundRect(ColorEntree, Offset(x, size.height - hE),
                    Size(bW, hE), CornerRadius(4f))
                drawRoundRect(ColorSortie, Offset(x + bW + gap, size.height - hS),
                    Size(bW, hS), CornerRadius(4f))
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            donnees.forEach { m ->
                Text(m.nomMois.take(3), Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(Modifier.size(10.dp)) { drawRoundRect(color, cornerRadius = CornerRadius(3f)) }
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
