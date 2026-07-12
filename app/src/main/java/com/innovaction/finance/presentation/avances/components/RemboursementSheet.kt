package com.innovaction.finance.presentation.avances.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.avances.FormulaireRemboursement
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.buttons.SecondaryButton
import com.innovaction.finance.presentation.components.buttons.TextActionButton
import com.innovaction.finance.presentation.components.inputs.AmountTextField
import com.innovaction.finance.presentation.components.inputs.InnovDateField
import com.innovaction.finance.presentation.theme.ColorEntree
import java.text.DecimalFormat

@Composable
fun RemboursementSheet(
    fr            : FormulaireRemboursement,
    onMontantChange: (String) -> Unit,
    onDateChange  : (Long) -> Unit,
    onRembourserTout: () -> Unit,
    onConfirmer   : () -> Unit,
    onAnnuler     : () -> Unit,
    modifier      : Modifier = Modifier,
) {
    val fmt = DecimalFormat("#,###.##")

    Column(modifier.padding(16.dp).padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text("Enregistrer un remboursement",
            style = MaterialTheme.typography.titleLarge)

        // Montant restant affiché
        Surface(
            color    = ColorEntree.copy(alpha = 0.1f),
            shape    = MaterialTheme.shapes.medium,
        ) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text("Montant restant dû",
                    style = MaterialTheme.typography.bodyMedium)
                Text("${fmt.format(fr.montantRestant)} ${fr.deviseSymbole}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorEntree)
            }
        }

        // Montant à rembourser
        AmountTextField(
            value          = fr.montant,
            onValueChange  = onMontantChange,
            label          = "Montant remboursé",
            currency       = fr.deviseSymbole,
            supportingText = fr.erreurMontant,
            isError        = fr.erreurMontant != null,
        )

        // Bouton "Tout rembourser"
        SecondaryButton(
            text    = "Tout rembourser (${fmt.format(fr.montantRestant)} ${fr.deviseSymbole})",
            onClick = onRembourserTout,
        )

        // Date du remboursement
        InnovDateField(
            selectedDate   = fr.dateAff,
            onDateSelected = { /* géré via timestamp */ },
            label          = "Date du remboursement",
        )

        Spacer(Modifier.height(4.dp))

        if (fr.isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                text    = "Confirmer le remboursement",
                onClick = onConfirmer,
                icon    = Icons.Filled.Payments,
            )
            TextActionButton("Annuler", onAnnuler, Modifier.fillMaxWidth())
        }
    }
}
