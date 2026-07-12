package com.innovaction.finance.presentation.components.feedback

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

// Dialog de confirmation (suppression, action irréversible)
@Composable
fun ConfirmDialog(
    title       : String,
    message     : String,
    confirmLabel: String = "Confirmer",
    cancelLabel : String = "Annuler",
    onConfirm   : () -> Unit,
    onDismiss   : () -> Unit,
    icon        : ImageVector? = null,
    destructive : Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon             = if (icon != null) {{ Icon(icon, null) }} else null,
        title            = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text             = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton    = {
            if (destructive) {
                Button(onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor   = MaterialTheme.colorScheme.onError,
                    )) { Text(confirmLabel) }
            } else {
                Button(onClick = onConfirm) { Text(confirmLabel) }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(cancelLabel) } },
    )
}

// Dialog d'information simple
@Composable
fun InfoDialog(
    title    : String,
    message  : String,
    onDismiss: () -> Unit,
    icon     : ImageVector? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon    = if (icon != null) {{ Icon(icon, null) }} else null,
        title   = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text    = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
    )
}
