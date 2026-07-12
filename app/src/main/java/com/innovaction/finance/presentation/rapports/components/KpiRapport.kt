package com.innovaction.finance.presentation.rapports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun KpiRapportRow(
    items    : List<Triple<String, String, Color>>,
    modifier : Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { (label, value, color) ->
            Card(modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(
                    Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(label,
                        style     = MaterialTheme.typography.labelSmall,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(value,
                        style      = MaterialTheme.typography.titleSmall,
                        color      = color,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign  = TextAlign.Center)
                }
            }
        }
    }
}
