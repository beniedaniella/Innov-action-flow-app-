package com.innovaction.finance.presentation.rapports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovGold

@Composable
fun ExportButtons(
    onExportCsv : () -> Unit,
    onExportPdf : () -> Unit,
    isLoading   : Boolean  = false,
    modifier    : Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // CSV / Excel
        OutlinedButton(
            onClick  = onExportCsv,
            enabled  = !isLoading,
            modifier = Modifier.weight(1f).height(48.dp),
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Filled.TableChart, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Export Excel", style = MaterialTheme.typography.labelMedium)
            }
        }
        // PDF
        Button(
            onClick  = onExportPdf,
            enabled  = !isLoading,
            modifier = Modifier.weight(1f).height(48.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = ColorSortie,
                contentColor   = MaterialTheme.colorScheme.onError,
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onError)
            } else {
                Icon(Icons.Filled.PictureAsPdf, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Export PDF", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
