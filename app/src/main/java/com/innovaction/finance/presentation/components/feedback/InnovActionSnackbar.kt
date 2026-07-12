package com.innovaction.finance.presentation.components.feedback

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

// Helpers pour afficher des Snackbars typées depuis les ViewModels
suspend fun SnackbarHostState.showSuccess(message: String) {
    showSnackbar(message = "✅ $message", duration = SnackbarDuration.Short)
}

suspend fun SnackbarHostState.showError(message: String) {
    showSnackbar(message = "❌ $message", duration = SnackbarDuration.Long)
}

suspend fun SnackbarHostState.showWarning(message: String) {
    showSnackbar(message = "⚠️ $message", duration = SnackbarDuration.Short)
}

suspend fun SnackbarHostState.showWithAction(
    message   : String,
    actionLabel: String,
    onAction  : () -> Unit,
) {
    val result = showSnackbar(message = message, actionLabel = actionLabel,
        duration = SnackbarDuration.Long)
    if (result == SnackbarResult.ActionPerformed) onAction()
}
