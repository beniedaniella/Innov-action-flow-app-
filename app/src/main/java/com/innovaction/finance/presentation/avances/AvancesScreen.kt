package com.innovaction.finance.presentation.avances

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.presentation.components.buttons.InnovActionFAB
import com.innovaction.finance.presentation.components.empty.EmptyState
import com.innovaction.finance.presentation.components.feedback.ConfirmDialog
import com.innovaction.finance.presentation.components.feedback.InnovBottomSheet
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.avances.components.*
import com.innovaction.finance.presentation.theme.ColorEntree
import com.innovaction.finance.presentation.theme.ColorSortie
import com.innovaction.finance.presentation.theme.InnovGold
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvancesScreen(
    modifier  : Modifier = Modifier,
    viewModel : AvancesViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.effacerErreur() }
    }

    Box(modifier.fillMaxSize()) {
        if (state.isLoading) { FullScreenLoading(); return@Box }

        Column(Modifier.fillMaxSize()) {

            // ── Bannière alertes ──────────────────────────────────────────
            if (state.nbEnRetard > 0) {
                AlerteRetardBanner(
                    nb      = state.nbEnRetard,
                    montant = state.montantTotalRetard,
                    devise  = state.devises.find { it.code == "CDF" }?.symbole ?: "FC",
                    onClick = { viewModel.setFiltre(FiltreAvance.EN_RETARD) },
                )
            }

            // ── Onglets filtres ───────────────────────────────────────────
            val filtres = listOf(
                FiltreAvance.TOUTES      to "Toutes (${state.toutes.size})",
                FiltreAvance.ACTIVES     to "Actives",
                FiltreAvance.EN_RETARD   to "En retard${if (state.nbEnRetard > 0) " ⚠️${state.nbEnRetard}" else ""}",
                FiltreAvance.REMBOURSEES to "Remboursées",
            )
            ScrollableTabRow(
                selectedTabIndex = filtres.indexOfFirst { it.first == state.filtre },
                modifier         = Modifier.fillMaxWidth(),
                edgePadding      = 0.dp,
            ) {
                filtres.forEach { (filtre, label) ->
                    Tab(
                        selected = state.filtre == filtre,
                        onClick  = { viewModel.setFiltre(filtre) },
                        text     = { Text(label, style = MaterialTheme.typography.labelMedium) },
                    )
                }
            }

            // ── Liste ─────────────────────────────────────────────────────
            val liste = state.affichees
            if (liste.isEmpty()) {
                EmptyState(
                    icon        = Icons.Filled.Assignment,
                    title       = when (state.filtre) {
                        FiltreAvance.EN_RETARD   -> "Aucune avance en retard"
                        FiltreAvance.REMBOURSEES -> "Aucune avance remboursée"
                        FiltreAvance.ACTIVES     -> "Aucune avance active"
                        else                     -> "Aucune avance enregistrée"
                    },
                    description = if (state.filtre == FiltreAvance.TOUTES)
                        "Appuyez sur + pour créer
une première avance."
                    else "Changez de filtre ou créez une avance.",
                    modifier    = Modifier.fillMaxSize(),
                    action      = if (state.filtre == FiltreAvance.TOUTES)
                        "Nouvelle avance" to { viewModel.ouvrirFormulaire() } else null,
                )
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding        = PaddingValues(16.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                ) {
                    items(liste, key = { it.avance.id }) { av ->
                        AvanceCard(
                            av         = av,
                            onClick    = { viewModel.voirDetail(av) },
                            onRembours = { viewModel.ouvrirRemboursement(av) },
                            onEdit     = { viewModel.ouvrirFormulaire(av) },
                            onDelete   = { viewModel.demanderSuppression(av) },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // FAB
        InnovActionFAB(
            onClick            = { viewModel.ouvrirFormulaire() },
            contentDescription = "Nouvelle avance",
            modifier           = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        )

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }

    // ── Bottom Sheet : formulaire avance ──────────────────────────────────
    if (state.showFormulaire) {
        InnovBottomSheet(onDismiss = viewModel::fermerFormulaire) {
            AvanceForm(
                formulaire          = state.formulaire,
                devises             = state.devises,
                projets             = state.projets,
                isEdition           = state.formulaire.id != null,
                onBenefChange       = viewModel::onBenefChange,
                onObjetChange       = viewModel::onObjetChange,
                onMontantChange     = viewModel::onMontantChange,
                onDeviseChange      = viewModel::onDeviseChange,
                onProjetChange      = viewModel::onProjetChange,
                onDechargeChange    = viewModel::onDechargeChange,
                onRemarquesChange   = viewModel::onRemarquesChange,
                onDateEmissionChange= viewModel::onDateEmissionChange,
                onDateEcheanceChange= viewModel::onDateEcheanceChange,
                onSauvegarder       = viewModel::sauvegarder,
                onAnnuler           = viewModel::fermerFormulaire,
            )
        }
    }

    // ── Bottom Sheet : remboursement ──────────────────────────────────────
    if (state.showRemboursement) {
        InnovBottomSheet(onDismiss = viewModel::fermerRemboursement) {
            RemboursementSheet(
                fr              = state.frmRembours,
                onMontantChange = viewModel::onMontantRemboursChange,
                onDateChange    = viewModel::onDateRemboursChange,
                onRembourserTout = viewModel::rembourserTout,
                onConfirmer     = viewModel::confirmerRemboursement,
                onAnnuler       = viewModel::fermerRemboursement,
            )
        }
    }

    // ── Dialog suppression ────────────────────────────────────────────────
    if (state.showConfirmDelete != null) {
        ConfirmDialog(
            title        = "Supprimer cette avance ?",
            message      = "L'avance de "${state.showConfirmDelete!!.avance.beneficiaire}" sera supprimée définitivement.",
            confirmLabel = "Supprimer",
            onConfirm    = viewModel::confirmerSuppression,
            onDismiss    = viewModel::annulerSuppression,
            destructive  = true,
        )
    }
}

@Composable
private fun AlerteRetardBanner(nb: Int, montant: Double, devise: String, onClick: () -> Unit) {
    val fmt = DecimalFormat("#,###")
    Surface(
        onClick = onClick,
        color   = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Filled.Warning, null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "$nb avance${if (nb > 1) "s" else ""} en retard de remboursement",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    "Total dû : ${fmt.format(montant)} $devise",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                )
            }
            Text("Voir →",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold)
        }
    }
}
