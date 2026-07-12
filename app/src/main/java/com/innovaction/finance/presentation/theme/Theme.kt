package com.innovaction.finance.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// ── Schémas de couleurs ─────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = md_primary,
    onPrimary            = md_onPrimary,
    primaryContainer     = md_primaryContainer,
    onPrimaryContainer   = md_onPrimaryContainer,
    secondary            = md_secondary,
    onSecondary          = md_onSecondary,
    secondaryContainer   = md_secondaryContainer,
    onSecondaryContainer = md_onSecondaryContainer,
    tertiary             = md_tertiary,
    onTertiary           = md_onTertiary,
    tertiaryContainer    = md_tertiaryContainer,
    onTertiaryContainer  = md_onTertiaryContainer,
    error                = md_error,
    onError              = md_onError,
    errorContainer       = md_errorContainer,
    onErrorContainer     = md_onErrorContainer,
    background           = md_background,
    onBackground         = md_onBackground,
    surface              = md_surface,
    onSurface            = md_onSurface,
    surfaceVariant       = md_surfaceVariant,
    onSurfaceVariant     = md_onSurfaceVariant,
    outline              = md_outline,
    outlineVariant       = md_outlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary              = md_dark_primary,
    onPrimary            = md_dark_onPrimary,
    primaryContainer     = md_dark_primaryContainer,
    onPrimaryContainer   = md_dark_onPrimaryContainer,
    secondary            = md_dark_secondary,
    onSecondary          = md_dark_onSecondary,
    secondaryContainer   = md_dark_secondaryContainer,
    onSecondaryContainer = md_dark_onSecondaryContainer,
    tertiary             = md_dark_tertiary,
    onTertiary           = md_dark_onTertiary,
    tertiaryContainer    = md_dark_tertiaryContainer,
    onTertiaryContainer  = md_dark_onTertiaryContainer,
    error                = md_dark_error,
    onError              = md_dark_onError,
    errorContainer       = md_dark_errorContainer,
    onErrorContainer     = md_dark_onErrorContainer,
    background           = md_dark_background,
    onBackground         = md_dark_onBackground,
    surface              = md_dark_surface,
    onSurface            = md_dark_onSurface,
    surfaceVariant       = md_dark_surfaceVariant,
    onSurfaceVariant     = md_dark_onSurfaceVariant,
    outline              = md_dark_outline,
    outlineVariant       = md_dark_outlineVariant,
)

// ── LocalColors — couleurs sémantiques financières ──────────────────────────

data class FinanceColors(
    val entree : androidx.compose.ui.graphics.Color = ColorEntree,
    val sortie : androidx.compose.ui.graphics.Color = ColorSortie,
    val alerte : androidx.compose.ui.graphics.Color = ColorAlerte,
    val neutre : androidx.compose.ui.graphics.Color = ColorNeutre,
    val gold   : androidx.compose.ui.graphics.Color = InnovGold,
    val navy   : androidx.compose.ui.graphics.Color = InnovNavy,
)

val LocalFinanceColors = staticCompositionLocalOf { FinanceColors() }

// ── InnovActionTheme ─────────────────────────────────────────────────────────

/**
 * Thème principal de l'application.
 *
 * @param darkTheme   Force le thème sombre (null = suit le système).
 * @param dynamicColor Utilise Material You sur Android 12+ (désactivé par défaut
 *                    pour conserver l'identité INNOV'ACTION).
 */
@Composable
fun InnovActionTheme(
    darkTheme    : Boolean = isSystemInDarkTheme(),
    dynamicColor : Boolean = false,   // false = palette INNOV'ACTION toujours appliquée
    content      : @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else           dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    CompositionLocalProvider(LocalFinanceColors provides FinanceColors()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = InnovActionTypography,
            shapes      = InnovActionShapes,
            content     = content
        )
    }
}

/** Raccourci pour accéder aux couleurs sémantiques financières dans n'importe quel Composable. */
val MaterialTheme.financeColors: FinanceColors
    @Composable get() = LocalFinanceColors.current
