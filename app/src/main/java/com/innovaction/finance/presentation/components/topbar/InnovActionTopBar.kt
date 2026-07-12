package com.innovaction.finance.presentation.components.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

// Top bar standard (avec hamburger ou flèche retour)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnovActionTopBar(
    title          : String,
    subtitle       : String?  = null,
    onMenuClick    : (() -> Unit)? = null,   // null = pas de bouton gauche
    onBackClick    : (() -> Unit)? = null,   // prioritaire sur onMenuClick
    actions        : @Composable () -> Unit = {},
) {
    TopAppBar(
        title = {
            if (subtitle != null) {
                ListItem(
                    headlineContent  = { Text(title, style = MaterialTheme.typography.titleLarge) },
                    supportingContent = { Text(subtitle, style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primary),
                )
            } else {
                Text(title)
            }
        },
        navigationIcon = {
            when {
                onBackClick != null -> IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                }
                onMenuClick != null -> IconButton(onClick = onMenuClick) {
                    Icon(Icons.Filled.Menu, "Menu")
                }
            }
        },
        actions   = { actions() },
        colors    = TopAppBarDefaults.topAppBarColors(
            containerColor       = MaterialTheme.colorScheme.primary,
            titleContentColor    = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor    = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}
