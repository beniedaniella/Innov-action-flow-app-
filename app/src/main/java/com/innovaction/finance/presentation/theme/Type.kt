package com.innovaction.finance.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typographie Material 3 — utilise la police système par défaut.
 * Remplacer FontFamily.Default par une police personnalisée à l'étape 2
 * si une fonte spécifique est choisie pour INNOV'ACTION.
 */
val InnovActionTypography = Typography(
    // Titres d'écran
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 28.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 24.sp, lineHeight = 32.sp),
    headlineSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),

    // Titres de section / cards
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 22.sp),
    titleSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 13.sp, lineHeight = 20.sp),

    // Corps de texte
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 16.sp),

    // Labels (chips, badges, boutons)
    labelLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.04.sp),
    labelMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.04.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.06.sp),

    // Montants financiers (surcharge bodyLarge pour les chiffres)
    displayLarge   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 44.sp),
    displayMedium  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 36.sp),
    displaySmall   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,      fontSize = 22.sp, lineHeight = 30.sp),
)
