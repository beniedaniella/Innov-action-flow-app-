package com.innovaction.finance.presentation.parametres

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovaction.finance.presentation.components.feedback.InnovBottomSheet
import com.innovaction.finance.presentation.components.loading.FullScreenLoading
import com.innovaction.finance.presentation.parametres.sections.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    modifier  : Modifier = Modifier,
    viewModel : ParametresViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.succesMessage) {
        state.succesMessage?.let {
            snackbar.showSnackbar("✅ $it")
            viewModel.effacerMessages()
        }
    }

    Box(modifier.fillMaxSize()) {
        if (state.isLoading) { FullScreenLoading(); return@Box }

        Column(Modifier.fillMaxSize()) {

            // ── Mini top bar de retour (quand dans une sous-section) ──────
            if (state.section != SectionParametres.ACCUEIL) {
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = viewModel::retourAccueil) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                    Text(
                        sectionTitre(state.section),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                HorizontalDivider()
            }

            // ── Contenu selon la section ──────────────────────────────────
            when (state.section) {

                // ── Accueil Paramètres ─────────────────────────────────────
                SectionParametres.ACCUEIL -> {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                        // En-tête association
                        Surface(
                            color    = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape    = MaterialTheme.shapes.medium,
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(state.nomAssociation,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Exercice ${state.exercice}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                        .copy(alpha = 0.7f))
                            }
                        }

                        // Sections
                        ParametresGroupTitle("Configuration métier")
                        ParametresItem(Icons.Filled.Business,         "Association",        "Nom, exercice",
                            { viewModel.allerSection(SectionParametres.ASSOCIATION) })
                        ParametresItem(Icons.Filled.FolderOpen,       "Projets",            "${state.projets.size} projet(s)",
                            { viewModel.allerSection(SectionParametres.PROJETS) })
                        ParametresItem(Icons.Filled.Payments,         "Modes de paiement",  "${state.modes.size} mode(s)",
                            { viewModel.allerSection(SectionParametres.MODES_PAIEMENT) })
                        ParametresItem(Icons.Filled.Label,            "Catégories",         "${state.categories.size} catégorie(s)",
                            { viewModel.allerSection(SectionParametres.CATEGORIES) })
                        ParametresItem(Icons.Filled.Groups,           "Fédérations",        "${state.federations.size} fédération(s)",
                            { viewModel.allerSection(SectionParametres.FEDERATIONS) })

                        ParametresGroupTitle("Alertes & taux")
                        ParametresItem(Icons.Filled.NotificationsActive, "Alertes & Seuils",
                            "Solde bas · Taux USD/CDF · Rappels",
                            { viewModel.allerSection(SectionParametres.ALERTES) })

                        ParametresGroupTitle("Préférences")
                        ListItem(
                            headlineContent   = { Text("Thème sombre") },
                            supportingContent = { Text("Suivre le système ou forcer le mode sombre") },
                            leadingContent    = { Icon(Icons.Filled.DarkMode, null) },
                            trailingContent   = {
                                Switch(
                                    checked         = state.darkMode,
                                    onCheckedChange = viewModel::toggleDarkMode,
                                )
                            }
                        )
                        HorizontalDivider()

                        ParametresGroupTitle("À propos")
                        ListItem(
                            headlineContent   = { Text("INNOV'ACTION Finance") },
                            supportingContent = { Text("Version 1.0.0 · Architecture MVVM + Room") },
                            leadingContent    = { Icon(Icons.Filled.Info, null) },
                        )
                        HorizontalDivider()
                        Spacer(Modifier.height(80.dp))
                    }
                }

                // ── Association ─────────────────────────────────────────────
                SectionParametres.ASSOCIATION -> SectionAssociation(
                    state            = state,
                    onNomChange      = viewModel::onNomAssocChange,
                    onExerciceChange = viewModel::onExerciceChange,
                    onSauvegarder    = viewModel::sauvegarderAssociation,
                )

                // ── Projets ─────────────────────────────────────────────────
                SectionParametres.PROJETS -> SectionListeGenerique(
                    titre     = "Projets",
                    items     = state.projets,
                    nomItem   = { it.nom },
                    sousNom   = { "Budget : ${"%.0f".format(it.budgetCdf)} FC" },
                    onAjouter = { viewModel.ouvrirFormulaireProjet() },
                    onEditer  = { viewModel.ouvrirFormulaireProjet(it) },
                )

                // ── Modes de paiement ───────────────────────────────────────
                SectionParametres.MODES_PAIEMENT -> SectionListeGenerique(
                    titre     = "Modes de paiement",
                    items     = state.modes,
                    nomItem   = { it.nom },
                    onAjouter = { viewModel.ouvrirFormulaireMode() },
                    onEditer  = { viewModel.ouvrirFormulaireMode(it) },
                )

                // ── Catégories ──────────────────────────────────────────────
                SectionParametres.CATEGORIES -> SectionListeGenerique(
                    titre     = "Catégories d'opération",
                    items     = state.categories,
                    nomItem   = { it.nom },
                    sousNom   = { it.typeDefaut },
                    onAjouter = { viewModel.ouvrirFormulaireCategorie() },
                    onEditer  = { viewModel.ouvrirFormulaireCategorie(it) },
                )

                // ── Fédérations ─────────────────────────────────────────────
                SectionParametres.FEDERATIONS -> SectionListeGenerique(
                    titre     = "Fédérations",
                    items     = state.federations,
                    nomItem   = { it.nom },
                    sousNom   = { it.description },
                    onAjouter = { viewModel.ouvrirFormulaireFederation() },
                    onEditer  = { viewModel.ouvrirFormulaireFederation(it) },
                )

                // ── Alertes ─────────────────────────────────────────────────
                SectionParametres.ALERTES -> SectionAlertes(
                    state               = state,
                    onSeuilCdfChange    = viewModel::onSeuilCdfChange,
                    onSeuilUsdChange    = viewModel::onSeuilUsdChange,
                    onTauxChange        = viewModel::onTauxChange,
                    onRappelJoursChange = viewModel::onRappelJoursChange,
                    onSauvegarder       = viewModel::sauvegarderAlertes,
                )

                else -> {}
            }
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }

    // ── Bottom Sheet formulaire générique ─────────────────────────────────
    if (state.showFormulaire) {
        InnovBottomSheet(onDismiss = viewModel::fermerFormulaire) {
            when (state.section) {
                SectionParametres.PROJETS -> FormulaireGenerique(
                    titre           = "Projet",
                    isEdition       = state.formEditId != null,
                    labelPrincipal  = "Nom du projet",
                    valeurLabel     = state.formLabel,
                    onLabelChange   = viewModel::onFormLabelChange,
                    label2          = "Budget CDF",
                    valeur2         = state.formValeur1,
                    onValeur2Change = viewModel::onFormValeur1Change,
                    unite2          = "FC",
                    label3          = "Budget USD",
                    valeur3         = state.formValeur2,
                    onValeur3Change = viewModel::onFormValeur2Change,
                    unite3          = "$",
                    isSaving        = state.formIsSaving,
                    onSauvegarder   = viewModel::sauvegarderProjet,
                    onAnnuler       = viewModel::fermerFormulaire,
                )
                SectionParametres.MODES_PAIEMENT -> FormulaireGenerique(
                    titre           = "Mode de paiement",
                    isEdition       = state.formEditId != null,
                    labelPrincipal  = "Nom du mode",
                    valeurLabel     = state.formLabel,
                    onLabelChange   = viewModel::onFormLabelChange,
                    isSaving        = state.formIsSaving,
                    onSauvegarder   = viewModel::sauvegarderMode,
                    onAnnuler       = viewModel::fermerFormulaire,
                )
                SectionParametres.CATEGORIES -> FormulaireGenerique(
                    titre            = "Catégorie",
                    isEdition        = state.formEditId != null,
                    labelPrincipal   = "Nom de la catégorie",
                    valeurLabel      = state.formLabel,
                    onLabelChange    = viewModel::onFormLabelChange,
                    dropdownItems    = listOf("ENTREE", "SORTIE", "TOUS"),
                    dropdownLabel    = "Type d'opération associé",
                    dropdownValue    = state.formValeur1,
                    onDropdownChange = viewModel::onFormValeur1Change,
                    isSaving         = state.formIsSaving,
                    onSauvegarder    = viewModel::sauvegarderCategorie,
                    onAnnuler        = viewModel::fermerFormulaire,
                )
                SectionParametres.FEDERATIONS -> FormulaireGenerique(
                    titre           = "Fédération",
                    isEdition       = state.formEditId != null,
                    labelPrincipal  = "Nom de la fédération",
                    valeurLabel     = state.formLabel,
                    onLabelChange   = viewModel::onFormLabelChange,
                    isSaving        = state.formIsSaving,
                    onSauvegarder   = viewModel::sauvegarderFederation,
                    onAnnuler       = viewModel::fermerFormulaire,
                )
                else -> {}
            }
        }
    }
}

@Composable
private fun ParametresGroupTitle(title: String) {
    Text(
        title,
        style    = MaterialTheme.typography.labelSmall,
        color    = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun ParametresItem(
    icon    : ImageVector,
    label   : String,
    subLabel: String,
    onClick : () -> Unit,
) {
    ListItem(
        headlineContent   = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = { Text(subLabel, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent    = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent   = {
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}

private fun sectionTitre(s: SectionParametres) = when (s) {
    SectionParametres.ASSOCIATION    -> "Association"
    SectionParametres.PROJETS        -> "Projets"
    SectionParametres.MODES_PAIEMENT -> "Modes de paiement"
    SectionParametres.CATEGORIES     -> "Catégories"
    SectionParametres.FEDERATIONS    -> "Fédérations"
    SectionParametres.ALERTES        -> "Alertes & Taux"
    SectionParametres.PREFERENCES    -> "Préférences"
    else -> "Paramètres"
}
