package com.innovaction.finance.presentation.dashboard.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlerteBanner(
    message  : String,
    modifier : Modifier = Modifier,
    isError  : Boolean  = true,
    onAction : (() -> Unit)? = null,
    actionLabel: String = "Voir",
) {
    val containerColor = if (isError)
        MaterialTheme.colorScheme.errorContainer
    else
        MaterialTheme.colorScheme.tertiaryContainer

    val contentColor = if (isError)
        MaterialTheme.colorScheme.onErrorContainer
    else
        MaterialTheme.colorScheme.onTertiaryContainer

    Surface(
        modifier      = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color         = containerColor,
        shape         = MaterialTheme.shapes.medium,
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector        = Icons.Filled.Warning,
                contentDescription = null,
                tint               = contentColor,
                modifier           = Modifier.size(20.dp),
            )
            Text(
                text     = message,
                style    = MaterialTheme.typography.labelMedium,
                color    = contentColor,
                modifier = Modifier.weight(1f),
            )
            if (onAction != null) {
                TextButton(onClick = onAction) {
                    Text(actionLabel, color = contentColor,
                        style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
