package com.innovaction.finance.presentation.parametres.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.inputs.InnovTextField
import com.innovaction.finance.presentation.parametres.ParametresUiState

@Composable
fun SectionAssociation(
    state           : ParametresUiState,
    onNomChange     : (String) -> Unit,
    onExerciceChange: (String) -> Unit,
    onSauvegarder   : () -> Unit,
    modifier        : Modifier = Modifier,
) {
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Informations de l'association",
            style = MaterialTheme.typography.titleMedium)
        InnovTextField(
            value         = state.nomAssociation,
            onValueChange = onNomChange,
            label         = "Nom de l'association",
            placeholder   = "Ex : INNOV'ACTION ASBL",
        )
        InnovTextField(
            value         = state.exercice,
            onValueChange = onExerciceChange,
            label         = "Exercice fiscal en cours",
            placeholder   = "2026",
        )
        PrimaryButton("Enregistrer", onSauvegarder)
    }
}
