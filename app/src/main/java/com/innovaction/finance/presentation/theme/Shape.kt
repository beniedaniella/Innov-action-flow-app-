package com.innovaction.finance.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val InnovActionShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),   // chips, badges
    small      = RoundedCornerShape(10.dp),  // champs de saisie, boutons secondaires
    medium     = RoundedCornerShape(16.dp),  // cards, dialogs
    large      = RoundedCornerShape(24.dp),  // bottom sheets, modales
    extraLarge = RoundedCornerShape(32.dp),  // FAB, grands éléments
)
