package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class FiltreChip(val label: String, val actif: Boolean, val onClick: () -> Unit)

@Composable
fun FiltresBar(chips: List<FiltreChip>, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        chips.forEach { chip ->
            FilterChip(
                selected = chip.actif,
                onClick  = chip.onClick,
                label    = { Text(chip.label, style = MaterialTheme.typography.labelSmall) },
            )
        }
    }
}
