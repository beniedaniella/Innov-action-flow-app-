package com.innovaction.finance.presentation.components.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnovBottomSheet(
    onDismiss : () -> Unit,
    title     : String? = null,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content   : @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
    ) {
        Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)) {
            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
            }
            content()
        }
    }
}
