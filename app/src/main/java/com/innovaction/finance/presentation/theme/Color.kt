package com.innovaction.finance.presentation.theme

import androidx.compose.ui.graphics.Color

// ── Palette INNOV'ACTION ────────────────────────────────────────────────────
val InnovNavy       = Color(0xFF1F3864)   // Bleu marine principal
val InnovNavyDark   = Color(0xFF162A4E)   // Navy foncé (hover, pressed)
val InnovNavyLight  = Color(0xFF3D5FA0)   // Navy clair (secondary containers)
val InnovGold       = Color(0xFFD4A017)   // Or — accent primaire
val InnovGoldDark   = Color(0xFFB8880F)   // Or foncé
val InnovGoldLight  = Color(0xFFFFF3CD)   // Or très clair (background chips)

// ── Sémantique financière ───────────────────────────────────────────────────
val ColorEntree     = Color(0xFF16A34A)   // Vert — entrée / crédit
val ColorSortie     = Color(0xFFDC2626)   // Rouge — sortie / débit
val ColorAlerte     = Color(0xFFEA580C)   // Orange — alerte / avertissement
val ColorNeutre     = Color(0xFF6B7280)   // Gris — neutre / désactivé

// ── Surfaces ────────────────────────────────────────────────────────────────
val SurfaceLight    = Color(0xFFF3F5F9)
val SurfaceDark     = Color(0xFF0F1923)
val CardLight       = Color(0xFFFFFFFF)
val CardDark        = Color(0xFF1A2535)

// ── Material 3 Color Scheme — Thème CLAIR ──────────────────────────────────
val md_primary                  = InnovNavy
val md_onPrimary                = Color(0xFFFFFFFF)
val md_primaryContainer         = InnovNavyLight
val md_onPrimaryContainer       = Color(0xFFFFFFFF)
val md_secondary                = InnovGold
val md_onSecondary              = Color(0xFF1A1A00)
val md_secondaryContainer       = InnovGoldLight
val md_onSecondaryContainer     = Color(0xFF2A1C00)
val md_tertiary                 = ColorEntree
val md_onTertiary               = Color(0xFFFFFFFF)
val md_tertiaryContainer        = Color(0xFFD4EDDA)
val md_onTertiaryContainer      = Color(0xFF002110)
val md_error                    = ColorSortie
val md_onError                  = Color(0xFFFFFFFF)
val md_errorContainer           = Color(0xFFFFDAD6)
val md_onErrorContainer         = Color(0xFF410002)
val md_background               = SurfaceLight
val md_onBackground             = Color(0xFF1A1A2E)
val md_surface                  = CardLight
val md_onSurface                = Color(0xFF1A1A2E)
val md_surfaceVariant           = Color(0xFFE8ECF4)
val md_onSurfaceVariant         = Color(0xFF44546A)
val md_outline                  = Color(0xFFE5E7EB)
val md_outlineVariant           = Color(0xFFBFC8D8)

// ── Material 3 Color Scheme — Thème SOMBRE ─────────────────────────────────
val md_dark_primary             = Color(0xFF8FAEE8)
val md_dark_onPrimary           = Color(0xFF0B1F48)
val md_dark_primaryContainer    = InnovNavy
val md_dark_onPrimaryContainer  = Color(0xFFD6E3FF)
val md_dark_secondary           = Color(0xFFE8C56A)
val md_dark_onSecondary         = Color(0xFF3A2D00)
val md_dark_secondaryContainer  = Color(0xFF544300)
val md_dark_onSecondaryContainer= Color(0xFFFFDF9D)
val md_dark_tertiary            = Color(0xFF6FCF97)
val md_dark_onTertiary          = Color(0xFF003822)
val md_dark_tertiaryContainer   = Color(0xFF005232)
val md_dark_onTertiaryContainer = Color(0xFF96F6B8)
val md_dark_error               = Color(0xFFFF8A80)
val md_dark_onError             = Color(0xFF690001)
val md_dark_errorContainer      = Color(0xFF93000A)
val md_dark_onErrorContainer    = Color(0xFFFFDAD6)
val md_dark_background          = SurfaceDark
val md_dark_onBackground        = Color(0xFFE8ECF4)
val md_dark_surface             = CardDark
val md_dark_onSurface           = Color(0xFFE8ECF4)
val md_dark_surfaceVariant      = Color(0xFF1E2D3D)
val md_dark_onSurfaceVariant    = Color(0xFFC0CADA)
val md_dark_outline             = Color(0xFF2A3A4A)
val md_dark_outlineVariant      = Color(0xFF3A4A5A)
