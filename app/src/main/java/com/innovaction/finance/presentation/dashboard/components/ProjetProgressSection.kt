package com.innovaction.finance.presentation.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.dashboard.ProjetResume
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovGold
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat

private fun fmt(v: Double) = DecimalFormat("#,###").format(v)

@Composable
fun ProjetProgressSection(projets: List<ProjetResume>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        projets.filter { it.budgetCdf > 0 }.forEach { projet ->
            ProjetProgressRow(projet)
        }
    }
}

@Composable
private fun ProjetProgressRow(projet: ProjetResume) {
    val pct   = projet.pourcentage
    val color = when {
        pct >= 1.0f -> ColorSortie      // dépassement
        pct >= 0.8f -> InnovGold        // avertissement
        else        -> InnovNavy        // normal
    }

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text     = projet.nom,
                style    = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text  = "%.0f%%".format(pct * 100),
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(5.dp))
        LinearProgressIndicator(
            progress         = { pct.coerceIn(0f, 1f) },
            modifier         = Modifier.fillMaxWidth().height(8.dp),
            color            = color,
            trackColor       = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.height(3.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text  = "${fmt(projet.depensesCdf)} FC dépensés",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text  = "Budget : ${fmt(projet.budgetCdf)} FC",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
