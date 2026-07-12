package com.innovaction.finance.presentation.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InnovActionCard(
    modifier : Modifier = Modifier,
    onClick  : (() -> Unit)? = null,
    content  : @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp), content = content)
        }
    } else {
        Card(modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
fun KpiCard(
    label    : String,
    value    : String,
    subtitle : String?  = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor  : Color = MaterialTheme.colorScheme.onPrimary,
    modifier : Modifier = Modifier,
) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.75f))
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.displaySmall,
                color = contentColor, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
fun TransactionRow(
    label       : String,
    meta        : String,
    amount      : String,
    amountColor : Color,
    onClick     : () -> Unit,
    modifier    : Modifier = Modifier,
    leadingIcon : @Composable (() -> Unit)? = null,
) {
    Surface(onClick = onClick, modifier = modifier.fillMaxWidth(), color = Color.Transparent) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) { leadingIcon(); Spacer(Modifier.width(12.dp)) }
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(meta, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Text(amount, style = MaterialTheme.typography.titleMedium,
                color = amountColor, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SectionHeader(
    title    : String,
    modifier : Modifier = Modifier,
    action   : Pair<String, () -> Unit>? = null,
) {
    Row(modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (action != null) {
            TextButton(onClick = action.second) {
                Text(action.first, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
