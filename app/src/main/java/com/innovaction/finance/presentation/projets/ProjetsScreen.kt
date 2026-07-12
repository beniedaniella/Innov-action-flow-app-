package com.innovaction.finance.presentation.projets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.presentation.components.buttons.InnovActionFAB
import com.innovaction.finance.presentation.components.empty.EmptyState
import com.innovaction.finance.presentation.components.feedback.ConfirmDialog
import com.innovaction.finance.presentation.components.feedback.InnovBottomSheet
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.projets.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetsScreen(
    modifier  : Modifier = Modifier,
    viewModel : ProjetsViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.effacerErreur() }
    }

    Box(modifier.fillMaxSize()) {

        when {
            state.isLoading -> FullScreenLoading()

            // ── Détail d'un projet ────────────────────────────────────────
            state.projetSelectionne != null -> {
                ProjetDetailView(
                    stats      = state.projetSelectionne!!,
                    operations = state.operationsProjet,
                    graphique  = state.graphiqueMois,
                    onBack     = viewModel::deselectionnerProjet,
                    onEdit     = { viewModel.ouvrirFormulaire(state.projetSelectionne) },
                )
            }

            // ── Liste des projets ─────────────────────────────────────────
            else -> {
                if (state.projetsStats.isEmpty()) {
                    EmptyState(
                        icon        = Icons.Filled.FolderOpen,
                        title       = "Aucun projet",
                        description = "Appuyez sur + pour créer
un premier projet.",
                        modifier    = Modifier.fillMaxSize(),
                        action      = "Créer un projet" to { viewModel.ouvrirFormulaire() },
                    )
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding        = PaddingValues(16.dp),
                        verticalArrangement   = Arrangement.spacedBy(14.dp),
                    ) {
                        // Résumé global
                        item {
                            ResumeProjets(state.projetsStats)
                        }
                        items(state.projetsStats, key = { it.projet.id }) { stats ->
                            ProjetCard(
                                stats    = stats,
                                onClick  = { viewModel.selectionnerProjet(stats) },
                                onEdit   = { viewModel.ouvrirFormulaire(stats) },
                                onDelete = { viewModel.demanderDesactivation(stats) },
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }

                // FAB
                InnovActionFAB(
                    onClick            = { viewModel.ouvrirFormulaire() },
                    contentDescription = "Nouveau projet",
                    icon               = Icons.Filled.Add,
                    modifier           = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                )
            }
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }

    // ── Bottom Sheet formulaire ───────────────────────────────────────────
    if (state.showFormulaire) {
        InnovBottomSheet(onDismiss = viewModel::fermerFormulaire) {
            ProjetForm(
                isEdition         = state.formProjetId != null,
                nom               = state.formNom,
                description       = state.formDescription,
                budgetCdf         = state.formBudgetCdf,
                budgetUsd         = state.formBudgetUsd,
                isSaving          = state.formIsSaving,
                onNomChange       = viewModel::onNomChange,
                onDescChange      = viewModel::onDescChange,
                onBudgetCdfChange = viewModel::onBudgetCdfChange,
                onBudgetUsdChange = viewModel::onBudgetUsdChange,
                onSauvegarder     = viewModel::sauvegarder,
                onAnnuler         = viewModel::fermerFormulaire,
            )
        }
    }

    // ── Confirmation désactivation ─────────────────────────────────────────
    if (state.showConfirmDesactiver != null) {
        ConfirmDialog(
            title        = "Désactiver ce projet ?",
            message      = ""${state.showConfirmDesactiver!!.projet.nom}" ne sera plus disponible dans les formulaires. Ses opérations sont conservées.",
            confirmLabel = "Désactiver",
            onConfirm    = viewModel::confirmerDesactivation,
            onDismiss    = viewModel::annulerDesactivation,
            destructive  = false,
        )
    }
}

@Composable
private fun ResumeProjets(stats: List<ProjetStats>) {
    val totalEntrees = stats.sumOf { it.entreesCdf }
    val totalSorties = stats.sumOf { it.sortiesCdf }
    val totalBudget  = stats.sumOf { it.projet.budgetCdf }
    val fmt = java.text.DecimalFormat("#,###")

    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            ResumeStat("${stats.size} projets",       "Actifs")
            ResumeStat("${fmt.format(totalEntrees)} FC", "Recettes totales")
            ResumeStat("${fmt.format(totalSorties)} FC", "Dépenses totales")
            if (totalBudget > 0)
                ResumeStat("${fmt.format(totalBudget)} FC",  "Budget total")
        }
    }
}

@Composable
private fun ResumeStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
