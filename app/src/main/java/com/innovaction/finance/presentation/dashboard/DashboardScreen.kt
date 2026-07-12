package com.innovaction.finance.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.presentation.components.cards.InnovActionCard
import com.innovaction.finance.presentation.components.cards.SectionHeader
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.dashboard.components.*

@Composable
fun DashboardScreen(
    onNavigateToOperations : () -> Unit,
    onNavigateToAvances    : () -> Unit,
    modifier               : Modifier = Modifier,
    viewModel              : DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Snackbar pour les erreurs
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.effacerErreur()
        }
    }

    Box(modifier.fillMaxSize()) {
        if (state.isLoading) {
            FullScreenLoading()
            return@Box
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Bandeau soldes ────────────────────────────────────────────
            SoldeStrip(state)

            Spacer(Modifier.height(12.dp))

            // ── Alertes ───────────────────────────────────────────────────
            if (state.alerteSoldeBasCdf) {
                AlerteBanner(
                    message = "Solde CDF inférieur au seuil d'alerte (${
                        "%,.0f".format(state.seuilAlerteCdf)} FC)",
                    isError = true,
                )
            }
            if (state.alerteSoldeBasUsd) {
                AlerteBanner(
                    message = "Solde USD inférieur au seuil d'alerte ($${state.seuilAlerteUsd})",
                    isError = true,
                )
            }
            if (state.nbAvancesEnRetard > 0) {
                AlerteBanner(
                    message     = "${state.nbAvancesEnRetard} avance(s) non remboursée(s) après échéance",
                    isError     = true,
                    onAction    = onNavigateToAvances,
                    actionLabel = "Voir",
                )
            }

            // ── KPIs Entrées / Sorties / Nb opérations ────────────────────
            Spacer(Modifier.height(8.dp))
            FluxKpiRow(state, Modifier.padding(top = 4.dp))

            // ── Soldes par compte ─────────────────────────────────────────
            if (state.comptes.isNotEmpty()) {
                SectionHeader(
                    title  = "Soldes par compte",
                    modifier = Modifier.padding(top = 8.dp),
                )
                InnovActionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    state.comptes.forEachIndexed { idx, compte ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(compte.nom,
                                style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text  = "${"%,.2f".format(compte.solde)} ${compte.deviseSymbole}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (compte.solde < 0)
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary,
                            )
                        }
                        if (idx < state.comptes.lastIndex)
                            HorizontalDivider()
                    }
                }
            }

            // ── Graphique 6 mois ──────────────────────────────────────────
            SectionHeader(
                title    = "Activité — 6 derniers mois (CDF)",
                modifier = Modifier.padding(top = 8.dp),
            )
            InnovActionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (state.donneesGraphique.isNotEmpty()) {
                    BarChart(data = state.donneesGraphique,
                        modifier = Modifier.fillMaxWidth())
                } else {
                    Text("Aucune donnée disponible",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // ── Projets ───────────────────────────────────────────────────
            if (state.projets.isNotEmpty()) {
                SectionHeader(
                    title    = "Projets — Exécution budgétaire",
                    modifier = Modifier.padding(top = 8.dp),
                )
                InnovActionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ProjetProgressSection(projets = state.projets)
                }
            }

            // ── Dernières opérations ──────────────────────────────────────
            SectionHeader(
                title  = "Dernières opérations",
                action = "Tout voir" to onNavigateToOperations,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (state.dernieresOperations.isEmpty()) {
                InnovActionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text  = "Aucune opération enregistrée.
Appuyez sur + pour commencer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    DernieresOperationsSection(
                        operations = state.dernieresOperations,
                        onVoirTout = onNavigateToOperations,
                    )
                }
            }

            // ── Résumé USD ─────────────────────────────────────────────────
            if (state.totalEntreesUsd > 0 || state.totalSortiesUsd > 0) {
                SectionHeader(title = "Flux USD", modifier = Modifier.padding(top = 8.dp))
                InnovActionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Entrées USD", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("+${"$"}${"%.2f".format(state.totalEntreesUsd)}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.tertiary)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                            Text("Sorties USD", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("-${"$"}${"%.2f".format(state.totalSortiesUsd)}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp)) // espace au-dessus de la Bottom Nav
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(androidx.compose.ui.Alignment.BottomCenter),
        )
    }
}
