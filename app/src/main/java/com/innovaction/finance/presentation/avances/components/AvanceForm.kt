package com.innovaction.finance.presentation.avances.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.entity.DeviseEntity
import com.innovaction.finance.data.local.entity.ProjetEntity
import com.innovaction.finance.presentation.avances.FormulaireAvance
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.buttons.TextActionButton
import com.innovaction.finance.presentation.components.inputs.*

@Composable
fun AvanceForm(
    formulaire        : FormulaireAvance,
    devises           : List<DeviseEntity>,
    projets           : List<ProjetEntity>,
    isEdition         : Boolean,
    onBenefChange     : (String) -> Unit,
    onObjetChange     : (String) -> Unit,
    onMontantChange   : (String) -> Unit,
    onDeviseChange    : (Long) -> Unit,
    onProjetChange    : (Long?) -> Unit,
    onDechargeChange  : (String) -> Unit,
    onRemarquesChange : (String) -> Unit,
    onDateEmissionChange: (Long) -> Unit,
    onDateEcheanceChange: (Long) -> Unit,
    onSauvegarder     : () -> Unit,
    onAnnuler         : () -> Unit,
    modifier          : Modifier = Modifier,
) {
    val deviseSelectionnee = devises.find { it.id == formulaire.deviseId }
    val projetSelectionne  = projets.find { it.id == formulaire.projetId }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            if (isEdition) "Modifier l'avance" else "Nouvelle avance / décharge",
            style = MaterialTheme.typography.titleLarge,
        )

        // Bénéficiaire
        InnovTextField(
            value          = formulaire.beneficiaire,
            onValueChange  = onBenefChange,
            label          = "Bénéficiaire",
            placeholder    = "Nom et prénom du bénéficiaire",
            supportingText = formulaire.erreurBenef,
            isError        = formulaire.erreurBenef != null,
        )

        // Objet
        InnovTextField(
            value          = formulaire.objet,
            onValueChange  = onObjetChange,
            label          = "Objet de l'avance",
            placeholder    = "Ex : Transport délégués, Achat matériel…",
            supportingText = formulaire.erreurObjet,
            isError        = formulaire.erreurObjet != null,
        )

        // Devise + Montant
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InnovDropdown(
                items          = devises,
                selectedItem   = deviseSelectionnee,
                onItemSelected = { onDeviseChange(it.id) },
                label          = "Devise",
                itemLabel      = { it.code },
                modifier       = Modifier.weight(0.35f),
                isError        = formulaire.erreurDevise != null,
                supportingText = formulaire.erreurDevise,
            )
            AmountTextField(
                value          = formulaire.montant,
                onValueChange  = onMontantChange,
                label          = "Montant",
                currency       = deviseSelectionnee?.symbole ?: "",
                modifier       = Modifier.weight(0.65f),
                supportingText = formulaire.erreurMontant,
                isError        = formulaire.erreurMontant != null,
            )
        }

        // Dates
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InnovDateField(
                selectedDate   = formulaire.dateEmissionAff,
                onDateSelected = { /* géré via timestamp */ },
                label          = "Date d'émission",
                modifier       = Modifier.weight(1f),
            )
            InnovDateField(
                selectedDate   = formulaire.dateEcheanceAff,
                onDateSelected = { /* géré via timestamp */ },
                label          = "Date limite ⚠️",
                modifier       = Modifier.weight(1f),
            )
        }
        if (formulaire.erreurEcheance != null) {
            Text(formulaire.erreurEcheance,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error)
        }

        // Projet (optionnel)
        InnovDropdown(
            items          = listOf(null) + projets,
            selectedItem   = projetSelectionne,
            onItemSelected = { onProjetChange(it?.id) },
            label          = "Projet (optionnel)",
            itemLabel      = { it?.nom ?: "— Aucun projet —" },
        )

        // N° décharge
        InnovTextField(
            value         = formulaire.numeroDecharge,
            onValueChange = onDechargeChange,
            label         = "N° décharge / référence",
            placeholder   = "Généré automatiquement si vide",
        )

        // Remarques
        InnovTextField(
            value         = formulaire.remarques,
            onValueChange = onRemarquesChange,
            label         = "Remarques (optionnel)",
            singleLine    = false,
        )

        Spacer(Modifier.height(4.dp))

        if (formulaire.isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                text    = if (isEdition) "Enregistrer les modifications"
                          else "Créer l'avance",
                onClick = onSauvegarder,
            )
            TextActionButton("Annuler", onAnnuler, Modifier.fillMaxWidth())
        }
    }
}
