package com.innovaction.finance.presentation.securite

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.presentation.components.buttons.DangerButton
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.loading.FullScreenLoading

/**
 * Écran de gestion de la sécurité (accessible depuis Paramètres).
 * PIN activation/désactivation + biométrie.
 */
@Composable
fun SecuriteScreen(
    modifier  : Modifier = Modifier,
    viewModel : SecuriteViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.succesMessage) {
        state.succesMessage?.let {
            snackbar.showSnackbar("✅ $it")
            viewModel.effacerMessages()
        }
    }

    Box(modifier.fillMaxSize()) {
        if (state.isLoading) { FullScreenLoading(); return@Box }

        // ── Configuration PIN ─────────────────────────────────────────────
        if (state.etat == EtatVerrou.CONFIGURATION_PIN) {
            PinScreen(
                titre          = if (!state.etapeConfirmation)
                    "Choisissez un PIN (4 à 6 chiffres)"
                else "Confirmez votre PIN",
                pinSaisi       = state.pinSaisi,
                erreur         = state.erreur,
                showBiometric  = false,
                onChiffreSaisi = { viewModel.onPinSaisiChange(state.pinSaisi + it) },
                onEffacer      = {
                    if (state.pinSaisi.isNotEmpty())
                        viewModel.onPinSaisiChange(state.pinSaisi.dropLast(1))
                },
                onValider      = viewModel::validerEtapeSaisie,
            )
            return@Box
        }

        // ── Écran de paramétrage sécurité ─────────────────────────────────
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Statut actuel
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.pinEnabled)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        if (state.pinEnabled) Icons.Filled.Lock else Icons.Filled.LockOpen,
                        null,
                        tint = if (state.pinEnabled)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp),
                    )
                    Column {
                        Text(
                            if (state.pinEnabled) "Application verrouillée par PIN" else "Aucun verrou actif",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (state.pinEnabled) "PIN requis à chaque ouverture"
                            else "Toute personne peut accéder à l'application",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Action PIN
            if (!state.pinEnabled) {
                PrimaryButton(
                    text  = "Activer le verrou PIN",
                    onClick = viewModel::commencerConfigPin,
                    icon  = Icons.Filled.Lock,
                )
            } else {
                // Biométrie
                if (state.biometricDispo) {
                    ListItem(
                        headlineContent   = { Text("Déverrouillage biométrique") },
                        supportingContent = { Text("Empreinte ou reconnaissance faciale") },
                        leadingContent    = { Icon(Icons.Filled.Fingerprint, null) },
                        trailingContent   = {
                            Switch(
                                checked         = state.biometricEnabled,
                                onCheckedChange = viewModel::toggleBiometric,
                            )
                        }
                    )
                    HorizontalDivider()
                }

                // Changer PIN
                OutlinedButton(
                    onClick  = viewModel::commencerConfigPin,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Icon(Icons.Filled.Edit, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Changer le PIN")
                }

                Spacer(Modifier.height(4.dp))

                DangerButton(
                    text    = "Désactiver le verrou PIN",
                    onClick = viewModel::desactiverPin,
                )
            }

            // Info
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.Info, null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp))
                    Text(
                        "Le PIN est stocké de façon sécurisée (haché SHA-256). " +
                        "Si vous l'oubliez, vous devrez réinstaller l'application.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
