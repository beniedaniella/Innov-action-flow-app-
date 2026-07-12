package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.entity.CompteEntity
import com.innovaction.finance.data.local.entity.ProjetEntity
import com.innovaction.finance.presentation.operations.OperationFiltre
import com.innovaction.finance.presentation.operations.TypeOperation

@Composable
fun OperationFiltresBar(
    filtre             : OperationFiltre,
    comptes            : List<CompteEntity>,
    projets            : List<ProjetEntity>,
    onFiltreType       : (TypeOperation?) -> Unit,
    onFiltreCompte     : (Long?) -> Unit,
    onFiltreProjet     : (Long?) -> Unit,
    onReinit           : () -> Unit,
    modifier           : Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Filled.FilterList, null,
            modifier = Modifier.size(18.dp),
            tint     = MaterialTheme.colorScheme.onSurfaceVariant)

        // Filtre par type
        TypeOperation.entries.forEach { type ->
            FilterChip(
                selected = filtre.type == type,
                onClick  = { onFiltreType(if (filtre.type == type) null else type) },
                label    = { Text(type.label, style = MaterialTheme.typography.labelSmall) },
            )
        }

        if (comptes.isNotEmpty()) {
            HorizontalDivider(
                modifier  = Modifier.height(24.dp).width(1.dp),
                thickness = 1.dp,
            )
            // Filtre par compte
            comptes.forEach { compte ->
                FilterChip(
                    selected = filtre.compteId == compte.id,
                    onClick  = { onFiltreCompte(if (filtre.compteId == compte.id) null else compte.id) },
                    label    = { Text(compte.nom, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }

        if (projets.isNotEmpty()) {
            HorizontalDivider(
                modifier  = Modifier.height(24.dp).width(1.dp),
                thickness = 1.dp,
            )
            projets.forEach { projet ->
                FilterChip(
                    selected = filtre.projetId == projet.id,
                    onClick  = { onFiltreProjet(if (filtre.projetId == projet.id) null else projet.id) },
                    label    = { Text(projet.nom, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }

        if (filtre.isActif) {
            AssistChip(
                onClick     = onReinit,
                label       = { Text("Effacer", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = { Icon(Icons.Filled.Clear, null, Modifier.size(14.dp)) },
            )
        }
    }
}
