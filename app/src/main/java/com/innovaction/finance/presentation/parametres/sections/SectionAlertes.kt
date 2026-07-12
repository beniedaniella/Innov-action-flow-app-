package com.innovaction.finance.presentation.parametres.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.inputs.AmountTextField
import com.innovaction.finance.presentation.components.inputs.InnovTextField
import com.innovaction.finance.presentation.parametres.ParametresUiState

@Composable
fun SectionAlertes(
    state              : ParametresUiState,
    onSeuilCdfChange   : (String) -> Unit,
    onSeuilUsdChange   : (String) -> Unit,
    onTauxChange       : (String) -> Unit,
    onRappelJoursChange: (String) -> Unit,
    onSauvegarder      : () -> Unit,
    modifier           : Modifier = Modifier,
) {
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text("Seuils d'alerte de solde",
            style = MaterialTheme.typography.titleMedium)

        Surface(color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small) {
            Row(Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Info, null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("L'application vous alertera quand le solde descend sous ces seuils.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }

        AmountTextField(
            value         = state.seuilAlerteCdf,
            onValueChange = onSeuilCdfChange,
            label         = "Seuil d'alerte — CDF",
            currency      = "FC",
            supportingText = "Notification si solde < ce montant",
        )

        AmountTextField(
            value         = state.seuilAlerteUsd,
            onValueChange = onSeuilUsdChange,
            label         = "Seuil d'alerte — USD",
            currency      = "$",
        )

        HorizontalDivider()
        Text("Taux de change", style = MaterialTheme.typography.titleMedium)

        AmountTextField(
            value         = state.tauxUsdCdf,
            onValueChange = onTauxChange,
            label         = "1 USD = ? CDF",
            currency      = "CDF",
            supportingText = "Utilisé pour les rapports combinés CDF+USD",
        )

        HorizontalDivider()
        Text("Rappels avances", style = MaterialTheme.typography.titleMedium)

        InnovTextField(
            value         = state.rappelAvanceJours,
            onValueChange = onRappelJoursChange,
            label         = "Rappel avant échéance (jours)",
            placeholder   = "7",
            supportingText = "Notification X jours avant la date limite d'une avance",
        )

        PrimaryButton("Enregistrer les alertes", onSauvegarder)
    }
}
