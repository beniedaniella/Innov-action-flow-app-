package com.innovaction.finance.presentation.rapports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PeriodSelector(
    label      : String,
    onPrevious : () -> Unit,
    onNext     : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.Filled.ChevronLeft, "Période précédente")
            }
            Text(label,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary)
            IconButton(onClick = onNext) {
                Icon(Icons.Filled.ChevronRight, "Période suivante")
            }
        }
    }
}
