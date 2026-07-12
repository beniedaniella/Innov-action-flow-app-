package com.innovaction.finance.presentation.parametres.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Composant générique pour afficher et gérer une liste configurable
 * (projets, modes de paiement, catégories, fédérations).
 */
@Composable
fun <T> SectionListeGenerique(
    titre      : String,
    items      : List<T>,
    nomItem    : (T) -> String,
    sousNom    : (T) -> String = { "" },
    onAjouter  : () -> Unit,
    onEditer   : (T) -> Unit,
    modifier   : Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(titre, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            FilledTonalButton(
                onClick = onAjouter,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(Icons.Filled.Add, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Ajouter", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (items.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center) {
                Text("Aucun élément. Appuyez sur Ajouter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn {
                items(items) { item ->
                    ListItem(
                        headlineContent   = { Text(nomItem(item)) },
                        supportingContent = if (sousNom(item).isNotBlank()) {
                            { Text(sousNom(item),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else null,
                        trailingContent = {
                            IconButton(onClick = { onEditer(item) }) {
                                Icon(Icons.Filled.Edit, "Modifier",
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
