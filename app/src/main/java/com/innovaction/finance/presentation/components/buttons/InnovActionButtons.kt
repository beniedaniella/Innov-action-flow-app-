package com.innovaction.finance.presentation.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text     : String,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true,
    icon     : ImageVector? = null,
) {
    Button(onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp)) {
        if (icon != null) { Icon(icon, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(
    text     : String,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true,
    icon     : ImageVector? = null,
) {
    OutlinedButton(onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp)) {
        if (icon != null) { Icon(icon, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun DangerButton(
    text     : String,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true,
) {
    Button(onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor   = MaterialTheme.colorScheme.onError,
        )
    ) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

@Composable
fun TextActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun InnovActionFAB(
    onClick : () -> Unit,
    contentDescription: String = "Ajouter",
    icon    : ImageVector = Icons.Filled.Add,
) {
    FloatingActionButton(
        onClick        = onClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor   = MaterialTheme.colorScheme.onSecondary,
    ) { Icon(icon, contentDescription = contentDescription) }
}
