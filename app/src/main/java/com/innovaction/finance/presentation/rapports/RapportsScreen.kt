package com.innovaction.finance.presentation.rapports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.presentation.components.cards.InnovActionCard
import com.innovaction.finance.presentation.components.cards.SectionHeader
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.rapports.components.*
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovNavy
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RapportsScreen(
    modifier  : Modifier = Modifier,
    viewModel : RapportsViewModel = hiltViewModel(),
) {
    val state    by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar  = remember { SnackbarHostState() }
    val fmt       = DecimalFormat("#,###")
    val fmtMois   = SimpleDateFormat("MMMM", Locale.FRENCH)

    // Messages snackbar
    LaunchedEffect(state.exportSucces) {
        state.exportSucces?.let {
            snackbar.showSnackbar("✅ Fichier exporté : $it")
            viewModel.effacerMessages()
        }
    }
    LaunchedEffect(state.exportErreur) {
        state.exportErreur?.let {
            snackbar.showSnackbar("❌ $it")
            viewModel.effacerMessages()
        }
    }

    Box(modifier.fillMaxSize()) {
        if (state.isLoading) { FullScreenLoading(); return@Box }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // ── Onglets type de rapport ───────────────────────────────────
            val tabs = listOf("Mensuel", "Annuel", "Par projet")
            TabRow(selectedTabIndex = state.typeActif.ordinal) {
                tabs.forEachIndexed { i, label ->
                    Tab(
                        selected = state.typeActif.ordinal == i,
                        onClick  = { viewModel.setType(TypeRapport.values()[i]) },
                        text     = { Text(label, style = MaterialTheme.typography.labelMedium) },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            when (state.typeActif) {

                // ════════════════════════════════════════════════════════
                // RAPPORT MENSUEL
                // ════════════════════════════════════════════════════════
                TypeRapport.MENSUEL -> {
                    val nomMois = state.donneesMois?.nomMois ?: ""

                    // Sélecteur mois
                    PeriodSelector(
                        label      = "$nomMois ${state.anneeSelectionnee}",
                        onPrevious = viewModel::moisPrecedent,
                        onNext     = viewModel::moisSuivant,
                        modifier   = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(Modifier.height(12.dp))

                    // KPIs
                    val dm = state.donneesMois
                    if (dm != null) {
                        KpiRapportRow(
                            listOf(
                                Triple("📥 Entrées
CDF",
                                    "${fmt.format(dm.entreesCdf)} FC", ColorEntree),
                                Triple("📤 Sorties
CDF",
                                    "${fmt.format(dm.sortiesCdf)} FC", ColorSortie),
                                Triple("💰 Solde
CDF",
                                    "${fmt.format(dm.entreesCdf - dm.sortiesCdf)} FC",
                                    if (dm.entreesCdf >= dm.sortiesCdf) ColorEntree else ColorSortie),
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        KpiRapportRow(
                            listOf(
                                Triple("📥 Entrées
USD",
                                    "$${fmt.format(dm.entreesUsd)}", ColorEntree),
                                Triple("📤 Sorties
USD",
                                    "$${fmt.format(dm.sortiesUsd)}", ColorSortie),
                                Triple("📊 Opérations",
                                    "${dm.nbOps}", InnovNavy),
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    // Répartition par projet
                    if (state.repartitionProjet.isNotEmpty()) {
                        SectionHeader("Dépenses par projet (CDF)",
                            modifier = Modifier.padding(top = 16.dp))
                        InnovActionCard(Modifier.padding(horizontal = 16.dp)) {
                            RepartitionList(
                                titre = "",
                                items = state.repartitionProjet,
                            )
                        }
                    }

                    // Répartition par mode de paiement
                    if (state.repartitionMode.isNotEmpty()) {
                        SectionHeader("Entrées par mode de paiement",
                            modifier = Modifier.padding(top = 4.dp))
                        InnovActionCard(Modifier.padding(horizontal = 16.dp)) {
                            RepartitionList("", state.repartitionMode)
                        }
                    }

                    // Exports
                    SectionHeader("Exporter ce rapport",
                        modifier = Modifier.padding(top = 8.dp))
                    ExportButtons(
                        onExportCsv = viewModel::exporterCsv,
                        onExportPdf = viewModel::exporterPdf,
                        isLoading   = state.exportEnCours,
                        modifier    = Modifier.padding(horizontal = 16.dp),
                    )
                    if (state.exportEnCours) {
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text("Génération du fichier en cours…",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // ════════════════════════════════════════════════════════
                // RAPPORT ANNUEL
                // ════════════════════════════════════════════════════════
                TypeRapport.ANNUEL -> {
                    PeriodSelector(
                        label      = "Exercice ${state.anneeSelectionnee}",
                        onPrevious = { viewModel.setAnnee(state.anneeSelectionnee - 1) },
                        onNext     = { viewModel.setAnnee(state.anneeSelectionnee + 1) },
                        modifier   = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(Modifier.height(12.dp))

                    // KPIs annuels
                    KpiRapportRow(
                        listOf(
                            Triple("Total entrées CDF",
                                "${fmt.format(state.totalAnneeEntCdf)} FC", ColorEntree),
                            Triple("Total sorties CDF",
                                "${fmt.format(state.totalAnneeSorCdf)} FC", ColorSortie),
                            Triple("Solde annuel CDF",
                                "${fmt.format(state.totalAnneeEntCdf - state.totalAnneeSorCdf)} FC",
                                if (state.totalAnneeEntCdf >= state.totalAnneeSorCdf) ColorEntree
                                else ColorSortie),
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(Modifier.height(12.dp))

                    // Graphique 12 mois
                    SectionHeader("Activité mensuelle (CDF)")
                    InnovActionCard(Modifier.padding(horizontal = 16.dp)) {
                        BarChartAnnuel(
                            donnees  = state.donnees12Mois,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // Tableau mensuel
                    SectionHeader("Détail par mois", modifier = Modifier.padding(top = 4.dp))
                    InnovActionCard(Modifier.padding(horizontal = 16.dp)) {
                        // En-tête tableau
                        Row(Modifier.fillMaxWidth()) {
                            Text("Mois", Modifier.weight(0.25f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Entrées FC", Modifier.weight(0.30f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ColorEntree)
                            Text("Sorties FC", Modifier.weight(0.30f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ColorSortie)
                            Text("Solde", Modifier.weight(0.15f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = InnovNavy)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 6.dp))
                        state.donnees12Mois.forEach { m ->
                            val solde = m.entreesCdf - m.sortiesCdf
                            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                                Text(m.nomMois.take(4), Modifier.weight(0.25f),
                                    style = MaterialTheme.typography.bodySmall)
                                Text(fmt.format(m.entreesCdf), Modifier.weight(0.30f),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (m.entreesCdf > 0) ColorEntree
                                            else MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(fmt.format(m.sortiesCdf), Modifier.weight(0.30f),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (m.sortiesCdf > 0) ColorSortie
                                            else MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(fmt.format(solde), Modifier.weight(0.15f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (solde >= 0) ColorEntree else ColorSortie)
                            }
                        }
                    }

                    // Exports
                    SectionHeader("Exporter", modifier = Modifier.padding(top = 8.dp))
                    ExportButtons(
                        onExportCsv = viewModel::exporterCsv,
                        onExportPdf = viewModel::exporterPdf,
                        isLoading   = state.exportEnCours,
                        modifier    = Modifier.padding(horizontal = 16.dp),
                    )
                }

                // ════════════════════════════════════════════════════════
                // PAR PROJET
                // ════════════════════════════════════════════════════════
                TypeRapport.PAR_PROJET -> {
                    SectionHeader("Dépenses par projet (toutes périodes)")
                    InnovActionCard(Modifier.padding(horizontal = 16.dp)) {
                        if (state.repartitionProjet.isEmpty()) {
                            Text("Aucune donnée disponible",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            RepartitionList(
                                titre = "",
                                items = state.repartitionProjet,
                            )
                        }
                    }
                    SectionHeader("Exporter le journal complet",
                        modifier = Modifier.padding(top = 8.dp))
                    ExportButtons(
                        onExportCsv = viewModel::exporterCsv,
                        onExportPdf = viewModel::exporterPdf,
                        isLoading   = state.exportEnCours,
                        modifier    = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
