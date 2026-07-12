package com.innovaction.finance.presentation.securite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.innovaction.finance.presentation.theme.InnovGold
import com.innovaction.finance.presentation.theme.InnovNavy

/**
 * Écran de saisie du PIN — affiché au démarrage si PIN activé.
 * Clavier numérique personnalisé (pas de clavier système → plus sécurisé).
 */
@Composable
fun PinScreen(
    titre              : String  = "Entrez votre PIN",
    pinSaisi           : String  = "",
    erreur             : String? = null,
    showBiometric      : Boolean = false,
    onChiffreSaisi     : (String) -> Unit,
    onEffacer          : () -> Unit,
    onValider          : () -> Unit,
    onBiometric        : () -> Unit = {},
    modifier           : Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(InnovNavy)
            .padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.SpaceEvenly,
    ) {

        // Logo / titre
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("💼", fontSize = 52.sp)
            Spacer(Modifier.height(12.dp))
            Text("INNOV'ACTION Finance",
                style      = MaterialTheme.typography.titleLarge,
                color      = Color.White,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(titre,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f))
        }

        // Indicateurs PIN (cercles)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(6) { idx ->
                Box(
                    Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (idx < pinSaisi.length) InnovGold
                            else Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }

        // Message d'erreur
        if (erreur != null) {
            Text(erreur,
                style  = MaterialTheme.typography.bodySmall,
                color  = MaterialTheme.colorScheme.errorContainer,
                fontWeight = FontWeight.SemiBold)
        }

        // Clavier numérique
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                listOf("1","2","3"),
                listOf("4","5","6"),
                listOf("7","8","9"),
                listOf("bio","0","⌫"),
            ).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { key ->
                        when (key) {
                            "bio" -> {
                                if (showBiometric) {
                                    ToucheSpeciale(onClick = onBiometric) {
                                        Icon(Icons.Filled.Fingerprint, "Biométrie",
                                            tint = InnovGold, modifier = Modifier.size(28.dp))
                                    }
                                } else {
                                    Box(Modifier.size(72.dp))
                                }
                            }
                            "⌫" -> {
                                ToucheSpeciale(onClick = onEffacer) {
                                    Icon(Icons.Filled.Backspace, "Effacer",
                                        tint = Color.White.copy(alpha = 0.75f),
                                        modifier = Modifier.size(24.dp))
                                }
                            }
                            else -> {
                                ToucheChiffre(
                                    chiffre   = key,
                                    onClick   = { onChiffreSaisi(key) },
                                    activated = if (key == "0" && pinSaisi.length >= 4)
                                        true else pinSaisi.length < 6,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bouton valider (si PIN complet)
        if (pinSaisi.length >= 4) {
            Button(
                onClick  = onValider,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = InnovGold,
                    contentColor   = Color.White,
                ),
            ) {
                Text("Déverrouiller", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ToucheChiffre(chiffre: String, onClick: () -> Unit, activated: Boolean) {
    Surface(
        onClick = onClick,
        enabled = activated,
        shape   = CircleShape,
        color   = Color.White.copy(alpha = 0.12f),
        modifier = Modifier.size(72.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(chiffre, fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun ToucheSpeciale(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        onClick  = onClick,
        shape    = CircleShape,
        color    = Color.Transparent,
        modifier = Modifier.size(72.dp),
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}
