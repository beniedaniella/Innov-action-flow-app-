package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.operations.TypeOperation

@Composable
fun TypeSelector(
    selected  : TypeOperation,
    onSelect  : (TypeOperation) -> Unit,
    modifier  : Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TypeOperation.entries.forEach { type ->
            val isSelected = type == selected
            val containerColor = when {
                !isSelected -> MaterialTheme.colorScheme.surfaceVariant
                type == TypeOperation.ENTREE    -> MaterialTheme.colorScheme.tertiary
                type == TypeOperation.SORTIE    -> MaterialTheme.colorScheme.error
                type == TypeOperation.TRANSFERT -> MaterialTheme.colorScheme.primary
                else                            -> MaterialTheme.colorScheme.secondary
            }
            val contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant

            FilterChip(
                selected = isSelected,
                onClick  = { onSelect(type) },
                label    = { Text(type.label, style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.weight(1f),
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = containerColor,
                    selectedLabelColor     = contentColor,
                ),
            )
        }
    }
}
