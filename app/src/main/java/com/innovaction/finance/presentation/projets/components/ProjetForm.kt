package com.innovaction.finance.presentation.projets.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.buttons.TextActionButton
import com.innovaction.finance.presentation.components.inputs.AmountTextField
import com.innovaction.finance.presentation.components.inputs.InnovTextField

@Composable
fun ProjetForm(
    isEdition      : Boolean,
    nom            : String,
    description    : String,
    budgetCdf      : String,
    budgetUsd      : String,
    isSaving       : Boolean,
    onNomChange    : (String) -> Unit,
    onDescChange   : (String) -> Unit,
    onBudgetCdfChange: (String) -> Unit,
    onBudgetUsdChange: (String) -> Unit,
    onSauvegarder  : () -> Unit,
    onAnnuler      : () -> Unit,
    modifier       : Modifier = Modifier,
) {
    Column(modifier.padding(16.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text(
            if (isEdition) "Modifier le projet" else "Nouveau projet",
            style = MaterialTheme.typography.titleLarge,
        )

        InnovTextField(
            value         = nom,
            onValueChange = onNomChange,
            label         = "Nom du projet",
            placeholder   = "Ex : Confestival, Fonctionnement…",
        )

        InnovTextField(
            value         = description,
            onValueChange = onDescChange,
            label         = "Description (optionnel)",
            singleLine    = false,
        )

        Text("Budgets", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)

        AmountTextField(
            value         = budgetCdf,
            onValueChange = onBudgetCdfChange,
            label         = "Budget CDF",
            currency      = "FC",
            supportingText = "Laisser vide si pas de budget défini",
        )

        AmountTextField(
            value         = budgetUsd,
            onValueChange = onBudgetUsdChange,
            label         = "Budget USD",
            currency      = "$",
        )

        Spacer(Modifier.height(4.dp))

        if (isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                text    = if (isEdition) "Enregistrer les modifications" else "Créer le projet",
                onClick = onSauvegarder,
            )
            TextActionButton("Annuler", onAnnuler, Modifier.fillMaxWidth())
        }
    }
}
