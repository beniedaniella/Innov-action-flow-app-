package com.innovaction.finance.presentation.avances.components

import androidx.compose.foundation.BorderStroke
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
import com.innovaction.finance.data.local.relation.AvanceWithDetails
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovGold
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private val fmtDate   = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
private val fmtAmount = DecimalFormat("#,###.##")

@Composable
fun AvanceCard(
    av         : AvanceWithDetails,
    onClick    : () -> Unit,
    onRembours : () -> Unit,
    onEdit     : () -> Unit,
    onDelete   : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    val avance    = av.avance
    val devise    = av.devise
    val restant   = avance.montant - avance.montantRembourse
    val now       = System.currentTimeMillis()
    val isRetard  = avance.statut == "ACTIVE" && avance.dateEcheance < now
    val joursEchu = if (isRetard)
        TimeUnit.MILLISECONDS.toDays(now - avance.dateEcheance) else 0L
    val joursRestants = if (!isRetard && avance.statut == "ACTIVE")
        TimeUnit.MILLISECONDS.toDays(avance.dateEcheance - now) else 0L

    val borderColor = when {
        isRetard                           -> ColorSortie
        avance.statut == "ACTIVE" && joursRestants <= 7 -> InnovGold
        avance.statut == "REMBOURSEE_TOTALE" -> ColorEntree
        else                               -> Color.Transparent
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp),
        border    = if (borderColor != Color.Transparent)
            BorderStroke(2.dp, borderColor) else null,
    ) {
        Column(Modifier.padding(16.dp)) {

            // ── En-tête : bénéficiaire + statut ───────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(avance.beneficiaire,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text(avance.objet,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatutBadge(avance.statut)
                    Box {
                        IconButton(onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.MoreVert, "Options",
                                modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }) {
                            if (avance.statut != "REMBOURSEE_TOTALE") {
                                DropdownMenuItem(
                                    text = { Text("Enregistrer un remboursement") },
                                    onClick = { menuExpanded = false; onRembours() },
                                    leadingIcon = { Icon(Icons.Filled.Payments, null) },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Modifier") },
                                onClick = { menuExpanded = false; onEdit() },
                                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Supprimer",
                                    color = MaterialTheme.colorScheme.error) },
                                onClick = { menuExpanded = false; onDelete() },
                                leadingIcon = { Icon(Icons.Filled.Delete, null,
                                    tint = MaterialTheme.colorScheme.error) },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Montants ──────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically) {
                Column {
                    Text("Montant avancé",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${fmtAmount.format(avance.montant)} ${devise.symbole}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface)
                }
                if (avance.statut != "REMBOURSEE_TOTALE") {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Restant dû",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${fmtAmount.format(restant)} ${devise.symbole}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isRetard) ColorSortie else MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Barre de remboursement
            if (avance.montantRembourse > 0) {
                Spacer(Modifier.height(8.dp))
                val pct = (avance.montantRembourse / avance.montant).toFloat().coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress   = { pct },
                    modifier   = Modifier.fillMaxWidth().height(6.dp),
                    color      = ColorEntree,
                    trackColor = MaterialTheme.colorScheme.errorContainer,
                )
                Spacer(Modifier.height(2.dp))
                Text("${fmtAmount.format(avance.montantRembourse)} remboursés sur ${fmtAmount.format(avance.montant)} ${devise.symbole}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // ── Infos bas de card ─────────────────────────────────────────
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                InfoChip(Icons.Filled.CalendarMonth,
                    "Émis le ${fmtDate.format(Date(avance.dateEmission))}")
                InfoChip(
                    if (isRetard) Icons.Filled.Warning else Icons.Filled.Event,
                    when {
                        isRetard        -> "Échu il y a $joursEchu j."
                        avance.statut == "REMBOURSEE_TOTALE" ->
                            "Remboursé le ${avance.dateRemboursement?.let { fmtDate.format(Date(it)) } ?: "—"}"
                        joursRestants <= 7 -> "⚠️ Reste $joursRestants j."
                        else            -> "Limite : ${fmtDate.format(Date(avance.dateEcheance))}"
                    },
                    tint = when {
                        isRetard             -> ColorSortie
                        joursRestants <= 7   -> InnovGold
                        avance.statut == "REMBOURSEE_TOTALE" -> ColorEntree
                        else                 -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Projet
            if (av.projet != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.FolderOpen, null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(av.projet!!.nom,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                }
            }

            // N° décharge
            if (avance.numeroDecharge.isNotBlank()) {
                Text("Décharge : ${avance.numeroDecharge}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp))
            }

            // Bouton remboursement rapide (si active)
            if (avance.statut in listOf("ACTIVE","REMBOURSEE_PARTIELLE")) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick  = onRembours,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = ColorEntree)
                ) {
                    Icon(Icons.Filled.Payments, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Enregistrer un remboursement",
                        style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun StatutBadge(statut: String) {
    val (label, color, bg) = when (statut) {
        "ACTIVE"              -> Triple("⏳ Active",      ColorSortie,  MaterialTheme.colorScheme.errorContainer)
        "REMBOURSEE_PARTIELLE"-> Triple("⚡ Partiel",     InnovGold,    MaterialTheme.colorScheme.secondaryContainer)
        "REMBOURSEE_TOTALE"   -> Triple("✅ Remboursé",  ColorEntree,  MaterialTheme.colorScheme.tertiaryContainer)
        "EN_LITIGE"           -> Triple("⚖️ En litige",  InnovGold,    MaterialTheme.colorScheme.secondaryContainer)
        else                  -> Triple(statut,           MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surfaceVariant)
    }
    Surface(shape = MaterialTheme.shapes.extraSmall, color = bg) {
        Text(label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InfoChip(
    icon    : androidx.compose.ui.graphics.vector.ImageVector,
    label   : String,
    tint    : Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = tint)
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}
