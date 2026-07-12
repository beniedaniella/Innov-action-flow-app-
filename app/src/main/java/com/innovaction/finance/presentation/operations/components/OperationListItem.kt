package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

private val fmtDate   = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
private val fmtAmount = DecimalFormat("#,###.##")

@Composable
fun OperationListItem(
    op       : OperationWithDetails,
    onClick  : () -> Unit,
    onEdit   : () -> Unit,
    onDelete : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val isEntree    = op.operation.type == "ENTREE"
    val isTransfert = op.operation.type == "TRANSFERT"
    val color: Color = when {
        isEntree    -> ColorEntree
        isTransfert -> ColorNeutre
        else        -> ColorSortie
    }
    val icon = when (op.operation.type) {
        "ENTREE"    -> Icons.Filled.ArrowDownward
        "TRANSFERT" -> Icons.Filled.SwapHoriz
        "FRAIS"     -> Icons.Filled.MoneyOff
        else        -> Icons.Filled.ArrowUpward
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth(),
        color    = Color.Transparent,
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Icône type
            Surface(
                shape    = MaterialTheme.shapes.small,
                color    = color.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }

            // Infos
            Column(Modifier.weight(1f)) {
                Text(
                    op.operation.libelle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(fmtDate.format(Date(op.operation.date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("·", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(op.categorie.nom,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1)
                    if (op.projet != null) {
                        Text("·", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(op.projet!!.nom,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1)
                    }
                }
            }

            // Montant + menu
            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (isEntree || isTransfert) "+" else "-"
                Text(
                    "$prefix${fmtAmount.format(op.operation.montant)} ${op.devise.symbole}",
                    style = MaterialTheme.typography.titleSmall,
                    color = color, fontWeight = FontWeight.ExtraBold,
                )
                Text(op.operation.numero,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Menu contextuel
            Box {
                IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.MoreVert, "Options",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text    = { Text("Modifier") },
                        onClick = { menuExpanded = false; onEdit() },
                        leadingIcon = { Icon(Icons.Filled.Edit, null) },
                    )
                    DropdownMenuItem(
                        text    = { Text("Dupliquer") },
                        onClick = { menuExpanded = false; /* étape 16 */ },
                        leadingIcon = { Icon(Icons.Filled.ContentCopy, null) },
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text    = { Text("Supprimer", color = MaterialTheme.colorScheme.error) },
                        onClick = { menuExpanded = false; onDelete() },
                        leadingIcon = { Icon(Icons.Filled.Delete, null,
                            tint = MaterialTheme.colorScheme.error) },
                    )
                }
            }
        }
    }
}
