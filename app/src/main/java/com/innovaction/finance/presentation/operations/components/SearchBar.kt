package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OperationSearchBar(
    query    : String,
    onQuery  : (String) -> Unit,
    modifier : Modifier = Modifier,
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQuery,
        modifier      = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder   = { Text("Rechercher…") },
        leadingIcon   = { Icon(Icons.Filled.Search, null) },
        trailingIcon  = if (query.isNotBlank()) {
            { IconButton(onClick = { onQuery("") }) { Icon(Icons.Filled.Clear, "Effacer") } }
        } else null,
        singleLine    = true,
        shape         = MaterialTheme.shapes.large,
    )
}
