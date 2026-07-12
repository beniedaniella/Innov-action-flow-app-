package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.relation.OperationWithDetails
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorNeutre
import com.innovaction.finance.presentation.theme.ColorSortie
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

private val fmtDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
private val fmtAmount = DecimalFormat("#,###.##")

@Composable
fun OperationDetailSheet(
    op       : OperationWithDetails,
    onEdit   : () -> Unit,
    onDelete : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val isEntree = op.operation.type == "ENTREE"
    val color    = when {
        isEntree                       -> ColorEntree
        op.operation.type == "TRANSFERT" -> ColorNeutre
        else                           -> ColorSortie
    }
    val prefix   = if (isEntree || op.operation.type == "TRANSFERT") "+" else "-"

    Column(modifier.padding(16.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {

        // Montant en vedette
        Text(
            "$prefix${fmtAmount.format(op.operation.montant)} ${op.devise.symbole}",
            style = MaterialTheme.typography.displaySmall,
            color = color, fontWeight = FontWeight.ExtraBold,
        )
        Text(op.operation.libelle, style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        // Détails
        DetailRow("Numéro",        op.operation.numero)
        DetailRow("Date",          fmtDate.format(Date(op.operation.date)))
        DetailRow("Type",          op.operation.type)
        DetailRow("Catégorie",     op.categorie.nom)
        DetailRow("Compte",        op.compte.nom)
        DetailRow("Mode paiement", op.modePaiement.nom)
        if (op.projet != null)
            DetailRow("Projet", op.projet!!.nom)
        if (op.federation != null)
            DetailRow("Fédération", op.federation!!.nom)
        if (op.operation.numeroPiece.isNotBlank())
            DetailRow("N° pièce", op.operation.numeroPiece)
        if (op.operation.remarques.isNotBlank())
            DetailRow("Remarques", op.operation.remarques)

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onEdit,   modifier = Modifier.weight(1f)) { Text("Modifier") }
            Button(onClick = onDelete, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor   = MaterialTheme.colorScheme.onError,
                )) { Text("Supprimer") }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
