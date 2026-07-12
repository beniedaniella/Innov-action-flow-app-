package com.innovaction.finance.presentation.comptes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.comptes.CompteDetail
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import java.text.DecimalFormat

private fun fmt(v: Double) = DecimalFormat("#,###.##").format(v)

@Composable
fun CompteCard(
    detail   : CompteDetail,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val devise  = detail.compteWithDevise.devise
    val compte  = detail.compteWithDevise.compte
    val solde   = detail.solde
    val couleur = runCatching {
        Color(android.graphics.Color.parseColor(compte.couleur))
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp),
        colors    = CardDefaults.cardColors(containerColor = couleur),
    ) {
        Column(Modifier.padding(18.dp)) {
            // Nom + devise
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Text(
                    text       = compte.nom,
                    style      = MaterialTheme.typography.titleMedium,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = Color.White.copy(alpha = 0.2f),
                ) {
                    Text(
                        text     = devise.code,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Solde actuel
            Text(
                text       = "${fmt(solde)} ${devise.symbole}",
                style      = MaterialTheme.typography.displaySmall,
                color      = Color.White,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text  = "Solde actuel",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
            )

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.25f))
            Spacer(Modifier.height(10.dp))

            // Entrées / Sorties
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CompteFlux("📥 Entrées", "+${fmt(detail.totalEntrees)} ${devise.symbole}", Color.White)
                CompteFlux("📤 Sorties", "-${fmt(detail.totalSorties)} ${devise.symbole}", Color.White.copy(0.85f))
            }
        }
    }
}

@Composable
private fun CompteFlux(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
    }
}
