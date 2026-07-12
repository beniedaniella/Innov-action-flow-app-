package com.innovaction.finance.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.dashboard.DashboardUiState
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat

private fun fmt(amount: Double) = DecimalFormat("#,###.##").format(amount)

@Composable
fun SoldeStrip(state: DashboardUiState, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(InnovNavy)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SoldeKpi(
            label    = "Caisse CDF",
            value    = "${fmt(state.soldeTotalCdf)} FC",
            subtitle = if (state.alerteSoldeBasCdf) "⚠️ Solde bas" else null,
            modifier = Modifier.weight(1f),
        )
        SoldeKpi(
            label    = "Caisse USD",
            value    = "$${fmt(state.soldeTotalUsd)}",
            subtitle = if (state.alerteSoldeBasUsd) "⚠️ Solde bas" else null,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SoldeKpi(
    label    : String,
    value    : String,
    subtitle : String?,
    modifier : Modifier = Modifier,
) {
    Column(
        modifier
            .background(
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.08f),
                shape = MaterialTheme.shapes.medium,
            )
            .padding(14.dp)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text       = value,
            style      = MaterialTheme.typography.titleLarge,
            color      = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.ExtraBold,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.errorContainer,
            )
        }
    }
}
