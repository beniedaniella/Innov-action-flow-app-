package com.innovaction.finance.presentation.rapports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat

@Composable
fun RepartitionList(
    titre    : String,
    items    : List<Pair<String, Double>>,
    unite    : String  = "FC",
    modifier : Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val fmt    = DecimalFormat("#,###")
    val total  = items.sumOf { it.second }.coerceAtLeast(1.0)

    Column(modifier) {
        Text(titre, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp))

        items.take(8).forEach { (label, valeur) ->
            val pct = (valeur / total).toFloat().coerceIn(0f, 1f)
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f))
                    Text("${fmt.format(valeur)} $unite  (%.1f%%)".format(pct * 100),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress   = { pct },
                    modifier   = Modifier.fillMaxWidth().height(5.dp),
                    color      = InnovNavy,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}
