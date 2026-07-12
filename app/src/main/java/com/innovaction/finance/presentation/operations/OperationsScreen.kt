package com.innovaction.finance.presentation.operations

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
import com.innovaction.finance.presentation.components.empty.EmptyState
import com.innovaction.finance.presentation.components.feedback.ConfirmDialog
import com.innovaction.finance.presentation.components.feedback.InnovBottomSheet
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.operations.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsScreen(
    modifier  : Modifier = Modifier,
    viewModel : OperationsViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.effacerErreur() }
    }

    Box(modifier.fillMaxSize()) {
        if (state.isLoading) { FullScreenLoading(); return@Box }

        Column(Modifier.fillMaxSize()) {

            // ── Bandeau soldes ────────────────────────────────────────────
            SoldeHeader(
                soldeCdf = state.soldeCdf,
                soldeUsd = state.soldeUsd,
                nbOps    = state.operations.size,
            )

            // ── Barre de recherche ────────────────────────────────────────
            OperationSearchBar(
                query   = state.filtres.recherche,
                onQuery = viewModel::setRecherche,
            )

            // ── Chips filtres rapides ─────────────────────────────────────
            FiltresBar(chips = listOf(
                FiltreChip("Tout",      state.filtres.type == null,       { viewModel.setFiltreType(null) }),
                FiltreChip("📥 Entrées", state.filtres.type == "ENTREE",   { viewModel.setFiltreType("ENTREE") }),
                FiltreChip("📤 Sorties", state.filtres.type == "SORTIE",   { viewModel.setFiltreType("SORTIE") }),
                FiltreChip("🔄 Transferts", state.filtres.type == "TRANSFERT", { viewModel.setFiltreType("TRANSFERT") }),
                FiltreChip("💸 Frais",  state.filtres.type == "FRAIS",    { viewModel.setFiltreType("FRAIS") }),
            ))

            // Indicateur filtres actifs
            if (state.filtres.actifs) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = viewModel::reinitialiserFiltres) {
                        Icon(Icons.Filled.FilterListOff, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Réinitialiser les filtres", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            HorizontalDivider()

            // ── Liste des opérations ──────────────────────────────────────
            if (state.operations.isEmpty()) {
                EmptyState(
                    icon        = Icons.Filled.Receipt,
                    title       = if (state.filtres.actifs) "Aucun résultat" else "Aucune opération",
                    description = if (state.filtres.actifs)
                        "Modifiez vos filtres ou votre recherche."
                    else
                        "Appuyez sur + pour enregistrer
la première opération.",
                    modifier    = Modifier.fillMaxSize(),
                    action      = if (!state.filtres.actifs) "Nouvelle opération" to {
                        viewModel.ouvrirNouvelleOperation()
                    } else null,
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.operations, key = { it.operation.id }) { op ->
                        OperationListItem(
                            op       = op,
                            onClick  = { viewModel.voirDetail(op) },
                            onEdit   = { viewModel.ouvrirEdition(op) },
                            onDelete = { viewModel.demanderSuppression(op) },
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // ── FAB avec 3 options (Speed Dial simplifié) ─────────────────────
        var fabExpanded by remember { mutableStateOf(false) }
        Column(
            Modifier.align(Alignment.BottomEnd).padding(20.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (fabExpanded) {
                FabOption("📥 Entrée",    { fabExpanded = false; viewModel.ouvrirNouvelleOperation("ENTREE") })
                FabOption("📤 Sortie",    { fabExpanded = false; viewModel.ouvrirNouvelleOperation("SORTIE") })
                FabOption("🔄 Transfert", { fabExpanded = false; viewModel.ouvrirNouvelleOperation("TRANSFERT") })
                FabOption("💸 Frais",     { fabExpanded = false; viewModel.ouvrirNouvelleOperation("FRAIS") })
            }
            FloatingActionButton(
                onClick        = { fabExpanded = !fabExpanded },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor   = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(if (fabExpanded) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = "Ajouter une opération")
            }
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }

    // ── Bottom Sheet : formulaire ─────────────────────────────────────────
    if (state.showFormulaire) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::fermerFormulaire,
            sheetState       = sheetState,
        ) {
            OperationForm(
                formulaire        = state.formulaire,
                comptes           = state.comptes,
                projets           = state.projets,
                categories        = state.categories,
                modesPaiement     = state.modesPaiement,
                devises           = state.devises,
                federations       = state.federations,
                onTypeChange      = viewModel::onTypeChange,
                onLibelleChange   = viewModel::onLibelleChange,
                onMontantChange   = viewModel::onMontantChange,
                onCompteChange    = viewModel::onCompteChange,
                onCompteDestChange= viewModel::onCompteDestChange,
                onProjetChange    = viewModel::onProjetChange,
                onCategorieChange = viewModel::onCategorieChange,
                onModeChange      = viewModel::onModeChange,
                onDeviseChange    = viewModel::onDeviseChange,
                onFederationChange= viewModel::onFederationChange,
                onPieceChange     = viewModel::onPieceChange,
                onRemarquesChange = viewModel::onRemarquesChange,
                onDateChange      = viewModel::onDateChange,
                onSauvegarder     = viewModel::sauvegarder,
                onAnnuler         = viewModel::fermerFormulaire,
            )
        }
    }

    // ── Bottom Sheet : détail ─────────────────────────────────────────────
    if (state.showDetail && state.operationDetail != null) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = viewModel::fermerDetail,
            sheetState       = sheetState,
        ) {
            OperationDetailSheet(
                op       = state.operationDetail!!,
                onEdit   = { viewModel.fermerDetail(); viewModel.ouvrirEdition(state.operationDetail!!) },
                onDelete = { viewModel.fermerDetail(); viewModel.demanderSuppression(state.operationDetail!!) },
            )
        }
    }

    // ── Dialog de confirmation de suppression ─────────────────────────────
    if (state.showConfirmDelete != null) {
        ConfirmDialog(
            title        = "Supprimer cette opération ?",
            message      = ""${state.showConfirmDelete!!.operation.libelle}" sera supprimée définitivement.",
            confirmLabel = "Supprimer",
            onConfirm    = viewModel::confirmerSuppression,
            onDismiss    = viewModel::annulerSuppression,
            destructive  = true,
        )
    }
}

@Composable
private fun FabOption(label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp,
        ) {
            Text(label, Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium)
        }
    }
}
