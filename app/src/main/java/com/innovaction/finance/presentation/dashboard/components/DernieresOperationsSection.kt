package com.innovaction.finance.presentation.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

private val fmtDate   = SimpleDateFormat("dd/MM", Locale.getDefault())
private val fmtAmount = DecimalFormat("#,###.##")

@Composable
fun DernieresOperationsSection(
    operations : List<OperationWithDetails>,
    onVoirTout : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    Column(modifier) {
        operations.forEach { op ->
            OperationItem(op)
            if (op != operations.last()) {
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            }
        }
        if (operations.isNotEmpty()) {
            TextButton(
                onClick  = onVoirTout,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Voir toutes les opérations →",
                    style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun OperationItem(op: OperationWithDetails) {
    val isEntree    = op.operation.type == "ENTREE"
    val isTransfert = op.operation.type == "TRANSFERT"
    val montantColor = when {
        isEntree    -> ColorEntree
        isTransfert -> ColorNeutre
        else        -> ColorSortie
    }
    val icon = when {
        isEntree    -> Icons.Filled.ArrowDownward
        isTransfert -> Icons.Filled.SwapHoriz
        else        -> Icons.Filled.ArrowUpward
    }
    val iconBg = when {
        isEntree    -> MaterialTheme.colorScheme.tertiaryContainer
        isTransfert -> MaterialTheme.colorScheme.surfaceVariant
        else        -> MaterialTheme.colorScheme.errorContainer
    }

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Icône
        Surface(shape = MaterialTheme.shapes.small, color = iconBg,
            modifier = Modifier.size(38.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null,
                    tint = montantColor, modifier = Modifier.size(20.dp))
            }
        }

        // Libellé + méta
        Column(Modifier.weight(1f)) {
            Text(op.operation.libelle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(
                "${fmtDate.format(Date(op.operation.date))} · ${op.categorie.nom} · ${op.compte.nom}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Montant
        val prefix = if (isEntree) "+" else "-"
        Text(
            text       = "$prefix${fmtAmount.format(op.operation.montant)} ${op.devise.symbole}",
            style      = MaterialTheme.typography.titleSmall,
            color      = montantColor,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
