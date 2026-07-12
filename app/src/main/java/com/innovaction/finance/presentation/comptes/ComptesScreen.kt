package com.innovaction.finance.presentation.comptes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.data.local.entity.DeviseEntity
import com.innovaction.finance.presentation.components.buttons.InnovActionFAB
import com.innovaction.finance.presentation.components.cards.SectionHeader
import com.innovaction.finance.presentation.components.empty.EmptyState
import com.innovaction.finance.presentation.components.feedback.InnovBottomSheet
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.comptes.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComptesScreen(
    modifier  : Modifier = Modifier,
    viewModel : ComptesViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    // Devises disponibles (pour le formulaire)
    // Chargées depuis le ViewModel — ici simulé via ConfigRepository à l'étape 10
    val devises = remember { mutableStateOf<List<DeviseEntity>>(emptyList()) }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.effacerErreur() }
    }

    Box(modifier.fillMaxSize()) {

        when {
            state.isLoading -> FullScreenLoading()

            // ── Détail d'un compte sélectionné ──────────────────────────
            state.compteSelectionne != null -> {
                val detail = state.compteSelectionne!!
                Column(Modifier.fillMaxSize()) {
                    // Mini top bar de retour
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = viewModel::deselectionnerCompte) {
                            Icon(Icons.Filled.ArrowBack, "Retour")
                        }
                        Column {
                            Text(detail.compteWithDevise.compte.nom,
                                style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Solde : ${"%.2f".format(detail.solde)} ${detail.compteWithDevise.devise.symbole}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    HorizontalDivider()
                    SectionHeader(title = "Historique des opérations",
                        modifier = Modifier.padding(top = 4.dp))
                    CompteHistorique(
                        operations = state.historiqueOps,
                        modifier   = Modifier.fillMaxSize(),
                    )
                }
            }

            // ── Liste des comptes ─────────────────────────────────────────
            else -> {
                if (state.comptes.isEmpty()) {
                    EmptyState(
                        icon        = Icons.Filled.Add,
                        title       = "Aucun compte configuré",
                        description = "Appuyez sur + pour ajouter
un compte de caisse.",
                        modifier    = Modifier.align(Alignment.Center),
                    )
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding        = PaddingValues(16.dp),
                        verticalArrangement   = Arrangement.spacedBy(14.dp),
                    ) {
                        item {
                            SectionHeader(title = "${state.comptes.size} compte(s) actif(s)")
                        }
                        items(state.comptes, key = { it.compteWithDevise.compte.id }) { detail ->
                            CompteCard(
                                detail  = detail,
                                onClick = { viewModel.selectionnerCompte(detail) },
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // FAB — ajouter un compte
        if (state.compteSelectionne == null) {
            InnovActionFAB(
                onClick  = { viewModel.ouvrirFormulaire() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
            )
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }

    // ── Bottom Sheet : formulaire d'ajout ────────────────────────────────
    if (state.showFormulaire) {
        InnovBottomSheet(
            onDismiss = viewModel::fermerFormulaire,
            title     = "Nouveau compte",
        ) {
            CompteFormulaire(
                nom            = state.formNom,
                deviseId       = state.formDeviseId,
                soldeInitial   = state.formSoldeInitial,
                devises        = devises.value,
                isSaving       = state.formIsSaving,
                onNomChange    = viewModel::onNomChange,
                onDeviseChange = viewModel::onDeviseChange,
                onSoldeChange  = viewModel::onSoldeInitialChange,
                onSauvegarder  = viewModel::sauvegarderCompte,
                onAnnuler      = viewModel::fermerFormulaire,
            )
        }
    }
}
