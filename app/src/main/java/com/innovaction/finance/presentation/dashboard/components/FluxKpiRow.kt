package com.innovaction.finance.presentation.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.components.cards.InnovActionCard
import com.innovaction.finance.presentation.dashboard.DashboardUiState
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorNeutre
import com.innovaction.finance.presentation.theme.ColorSortie
import java.text.DecimalFormat

private fun fmt(v: Double) = DecimalFormat("#,###").format(v)

@Composable
fun FluxKpiRow(state: DashboardUiState, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FluxKpi("📥 Entrées CDF",  "+${fmt(state.totalEntreesCdf)} FC", ColorEntree,  Modifier.weight(1f))
        FluxKpi("📤 Sorties CDF",  "-${fmt(state.totalSortiesCdf)} FC", ColorSortie,  Modifier.weight(1f))
        FluxKpi("📊 Opérations",   "${state.nbOperations}",             ColorNeutre,  Modifier.weight(1f))
    }
}

@Composable
private fun FluxKpi(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    InnovActionCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleSmall,
            color = color, fontWeight = FontWeight.Bold)
    }
}
