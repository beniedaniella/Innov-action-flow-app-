package com.innovaction.finance.presentation.components.amount

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorNeutre
import com.innovaction.finance.presentation.theme.ColorSortie
import java.text.DecimalFormat

// Formate un montant selon la devise et le type
fun formatAmount(amount: Double, currency: String, isEntry: Boolean? = null): String {
    val formatter = DecimalFormat("#,###.##")
    val formatted = formatter.format(amount)
    val prefix = when (isEntry) {
        true  -> "+"
        false -> "-"
        null  -> ""
    }
    return "$prefix$formatted $currency"
}

@Composable
fun AmountDisplay(
    amount   : Double,
    currency : String,
    isEntry  : Boolean? = null,   // null = neutre (solde), true = entrée, false = sortie
    style    : TextStyle = MaterialTheme.typography.titleMedium,
    modifier : Modifier = Modifier,
) {
    val color = when (isEntry) {
        true  -> ColorEntree
        false -> ColorSortie
        null  -> MaterialTheme.colorScheme.onSurface
    }
    Text(
        text     = formatAmount(amount, currency, isEntry),
        style    = style,
        color    = color,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier,
    )
}
