package com.innovaction.finance.presentation.parametres.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.buttons.TextActionButton
import com.innovaction.finance.presentation.components.inputs.AmountTextField
import com.innovaction.finance.presentation.components.inputs.InnovDropdown
import com.innovaction.finance.presentation.components.inputs.InnovTextField

/** Formulaire réutilisable pour projets, modes, catégories, fédérations. */
@Composable
fun FormulaireGenerique(
    titre          : String,
    isEdition      : Boolean,
    labelPrincipal : String,
    valeurLabel    : String,
    onLabelChange  : (String) -> Unit,
    // Champs optionnels selon le contexte
    label2         : String?  = null,
    valeur2        : String   = "",
    onValeur2Change: (String) -> Unit = {},
    unite2         : String   = "",
    label3         : String?  = null,
    valeur3        : String   = "",
    onValeur3Change: (String) -> Unit = {},
    unite3         : String   = "",
    // Dropdown optionnel (pour typeDefaut des catégories)
    dropdownItems  : List<String> = emptyList(),
    dropdownLabel  : String  = "",
    dropdownValue  : String  = "",
    onDropdownChange: (String) -> Unit = {},
    isSaving       : Boolean = false,
    onSauvegarder  : () -> Unit,
    onAnnuler      : () -> Unit,
    modifier       : Modifier = Modifier,
) {
    Column(modifier.padding(16.dp).padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text(if (isEdition) "Modifier — $titre" else "Nouveau — $titre",
            style = MaterialTheme.typography.titleLarge)

        InnovTextField(
            value         = valeurLabel,
            onValueChange = onLabelChange,
            label         = labelPrincipal,
        )

        if (label2 != null) {
            AmountTextField(
                value         = valeur2,
                onValueChange = onValeur2Change,
                label         = label2,
                currency      = unite2,
            )
        }

        if (label3 != null) {
            AmountTextField(
                value         = valeur3,
                onValueChange = onValeur3Change,
                label         = label3,
                currency      = unite3,
            )
        }

        if (dropdownItems.isNotEmpty()) {
            InnovDropdown(
                items          = dropdownItems,
                selectedItem   = dropdownValue.ifBlank { dropdownItems.firstOrNull() },
                onItemSelected = { onDropdownChange(it ?: "") },
                label          = dropdownLabel,
                itemLabel      = { it ?: "" },
            )
        }

        Spacer(Modifier.height(4.dp))

        if (isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                text    = if (isEdition) "Enregistrer les modifications" else "Créer",
                onClick = onSauvegarder,
            )
            TextActionButton("Annuler", onAnnuler, Modifier.fillMaxWidth())
        }
    }
}
