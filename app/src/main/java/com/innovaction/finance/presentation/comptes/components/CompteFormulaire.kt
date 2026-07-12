package com.innovaction.finance.presentation.comptes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.entity.DeviseEntity
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.buttons.TextActionButton
import com.innovaction.finance.presentation.components.inputs.AmountTextField
import com.innovaction.finance.presentation.components.inputs.InnovDropdown
import com.innovaction.finance.presentation.components.inputs.InnovTextField

@Composable
fun CompteFormulaire(
    nom            : String,
    deviseId       : Long?,
    soldeInitial   : String,
    devises        : List<DeviseEntity>,
    isSaving       : Boolean,
    onNomChange    : (String) -> Unit,
    onDeviseChange : (Long) -> Unit,
    onSoldeChange  : (String) -> Unit,
    onSauvegarder  : () -> Unit,
    onAnnuler      : () -> Unit,
    modifier       : Modifier = Modifier,
) {
    val deviseSelectionnee = devises.find { it.id == deviseId }

    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text("Nouveau compte", style = MaterialTheme.typography.titleLarge)

        InnovTextField(
            value         = nom,
            onValueChange = onNomChange,
            label         = "Nom du compte",
            placeholder   = "Ex : Caisse CDF, M-Pesa, Banque USD…",
        )

        InnovDropdown(
            items          = devises,
            selectedItem   = deviseSelectionnee,
            onItemSelected = { onDeviseChange(it.id) },
            label          = "Devise",
            itemLabel      = { "${it.code} — ${it.nom}" },
        )

        AmountTextField(
            value         = soldeInitial,
            onValueChange = onSoldeChange,
            label         = "Solde initial",
            currency      = deviseSelectionnee?.symbole ?: "",
            supportingText = "Montant déjà présent avant d'utiliser l'application",
        )

        Spacer(Modifier.height(4.dp))

        if (isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(text = "Enregistrer le compte", onClick = onSauvegarder)
            TextActionButton(text = "Annuler", onClick = onAnnuler,
                modifier = Modifier.fillMaxWidth())
        }
    }
}
