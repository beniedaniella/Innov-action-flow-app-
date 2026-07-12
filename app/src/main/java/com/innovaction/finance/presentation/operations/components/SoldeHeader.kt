package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat

private fun fmt(v: Double) = DecimalFormat("#,###.##").format(v)

@Composable
fun SoldeHeader(soldeCdf: Double, soldeUsd: Double, nbOps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(InnovNavy)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column {
            Text("Solde CDF", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
            Text("${fmt(soldeCdf)} FC",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.ExtraBold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$nbOps op.", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Solde USD", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
            Text("$${fmt(soldeUsd)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}
