package com.innovaction.finance.presentation.comptes.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.relation.OperationWithDetails
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorNeutre
import com.innovaction.finance.presentation.theme.ColorSortie
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

private val fmtDate   = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
private val fmtAmount = DecimalFormat("#,###.##")

@Composable
fun CompteHistorique(
    operations : List<OperationWithDetails>,
    modifier   : Modifier = Modifier,
) {
    if (operations.isEmpty()) {
        Box(modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("Aucune opération sur ce compte",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(modifier) {
        items(operations, key = { it.operation.id }) { op ->
            HistoriqueRow(op)
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun HistoriqueRow(op: OperationWithDetails) {
    val isEntree    = op.operation.type == "ENTREE"
    val isTransfert = op.operation.type == "TRANSFERT"
    val color : Color = when {
        isEntree    -> ColorEntree
        isTransfert -> ColorNeutre
        else        -> ColorSortie
    }
    val icon = when {
        isEntree    -> Icons.Filled.ArrowDownward
        isTransfert -> Icons.Filled.SwapHoriz
        else        -> Icons.Filled.ArrowUpward
    }

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape    = MaterialTheme.shapes.small,
            color    = color.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
        }

        Column(Modifier.weight(1f)) {
            Text(op.operation.libelle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(
                "${fmtDate.format(Date(op.operation.date))} · ${op.categorie.nom}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text  = "${if (isEntree) "+" else "-"}${fmtAmount.format(op.operation.montant)} ${op.devise.symbole}",
                style = MaterialTheme.typography.titleSmall,
                color = color, fontWeight = FontWeight.ExtraBold,
            )
            Text(
                op.operation.numero,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
