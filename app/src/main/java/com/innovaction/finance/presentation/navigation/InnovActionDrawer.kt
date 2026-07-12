package com.innovaction.finance.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovaction.finance.presentation.theme.InnovGold
import com.innovaction.finance.presentation.theme.InnovNavy

data class DrawerItem(
    val route    : String,
    val label    : String,
    val icon     : ImageVector,
    val badgeCount: Int = 0,
)

private val drawerItems = listOf(
    DrawerItem(NavRoutes.Dashboard.route,  "Tableau de bord",       Icons.Filled.Dashboard),
    DrawerItem(NavRoutes.Operations.route, "Opérations financières", Icons.Filled.AccountBalance),
    DrawerItem(NavRoutes.Comptes.route,    "Comptes",               Icons.Filled.Wallet),
    DrawerItem(NavRoutes.Avances.route,    "Avances",               Icons.Filled.Assignment),
    DrawerItem(NavRoutes.Projets.route,    "Projets",               Icons.Filled.FolderOpen),
    DrawerItem(NavRoutes.Rapports.route,   "Rapports",              Icons.Filled.Analytics),
    DrawerItem(NavRoutes.Exports.route,    "Exports",               Icons.Filled.FileDownload),
    DrawerItem(NavRoutes.Securite.route,   "Sécurité",              Icons.Filled.Lock),
    DrawerItem(NavRoutes.Parametres.route, "Paramètres",            Icons.Filled.Settings),
)

@Composable
fun InnovActionDrawer(
    currentRoute : String?,
    onNavigate   : (String) -> Unit,
    onClose      : () -> Unit,
) {
    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        // En-tête
        Box(
            Modifier
                .fillMaxWidth()
                .background(InnovNavy)
                .padding(24.dp)
                .padding(top = 32.dp)
        ) {
            Column {
                Icon(Icons.Filled.AccountBalance,
                    contentDescription = null,
                    tint = InnovGold,
                    modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(12.dp))
                Text("INNOV'ACTION",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary)
                Text("Gestion Financière",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                Spacer(Modifier.height(8.dp))
                Surface(shape = MaterialTheme.shapes.extraSmall,
                    color = InnovGold.copy(alpha = 0.2f)) {
                    Text("Trésorier",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = InnovGold,
                        fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Items de navigation
        drawerItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationDrawerItem(
                icon     = { Icon(item.icon, contentDescription = null) },
                label    = { Text(item.label) },
                selected = selected,
                badge    = if (item.badgeCount > 0) {
                    { Badge { Text(item.badgeCount.toString()) } }
                } else null,
                onClick  = {
                    onNavigate(item.route)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }

        Spacer(Modifier.weight(1f))
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        Text("v1.0.0",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
