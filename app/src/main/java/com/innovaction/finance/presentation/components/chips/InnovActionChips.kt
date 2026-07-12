package com.innovaction.finance.presentation.components.chips

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InnovFilterChip(
    label     : String,
    selected  : Boolean,
    onClick   : () -> Unit,
    modifier  : Modifier = Modifier,
) {
    FilterChip(selected = selected, onClick = onClick, modifier = modifier,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) })
}

// Rangée de chips filtres (ex : Tout / Entrées / Sorties)
@Composable
fun FilterChipRow(
    options   : List<String>,
    selected  : String,
    onSelect  : (String) -> Unit,
    modifier  : Modifier = Modifier,
) {
    Row(modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            InnovFilterChip(label = opt, selected = opt == selected, onClick = { onSelect(opt) })
        }
    }
}

// Badge coloré (statut avance, opération, etc.)
@Composable
fun StatusBadge(
    label          : String,
    containerColor : Color,
    contentColor   : Color,
    modifier       : Modifier = Modifier,
) {
    Surface(modifier = modifier, color = containerColor,
        shape = MaterialTheme.shapes.extraSmall) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall, color = contentColor)
    }
}
