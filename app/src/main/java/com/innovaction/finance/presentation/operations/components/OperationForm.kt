package com.innovaction.finance.presentation.operations.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.innovaction.finance.data.local.entity.*
import com.innovaction.finance.presentation.components.buttons.PrimaryButton
import com.innovaction.finance.presentation.components.buttons.TextActionButton
import com.innovaction.finance.presentation.components.inputs.*
import com.innovaction.finance.presentation.operations.FormulaireOperation

/**
 * Formulaire adaptatif — s\'adapte selon le type d\'opération.
 * ENTREE / SORTIE / TRANSFERT / FRAIS
 * Tous les dropdowns sont chargés depuis la base de données.
 */
@Composable
fun OperationForm(
    formulaire     : FormulaireOperation,
    comptes        : List<CompteEntity>,
    projets        : List<ProjetEntity>,
    categories     : List<CategorieEntity>,
    modesPaiement  : List<ModePaiementEntity>,
    devises        : List<DeviseEntity>,
    federations    : List<FederationEntity>,
    onTypeChange   : (String) -> Unit,
    onLibelleChange: (String) -> Unit,
    onMontantChange: (String) -> Unit,
    onCompteChange : (Long) -> Unit,
    onCompteDestChange: (Long?) -> Unit,
    onProjetChange : (Long?) -> Unit,
    onCategorieChange : (Long) -> Unit,
    onModeChange   : (Long) -> Unit,
    onDeviseChange : (Long) -> Unit,
    onFederationChange: (Long?) -> Unit,
    onPieceChange  : (String) -> Unit,
    onRemarquesChange: (String) -> Unit,
    onDateChange   : (Long) -> Unit,
    onSauvegarder  : () -> Unit,
    onAnnuler      : () -> Unit,
    modifier       : Modifier = Modifier,
) {
    val types = listOf("ENTREE" to "📥 Entrée", "SORTIE" to "📤 Sortie",
        "TRANSFERT" to "🔄 Transfert", "FRAIS" to "💸 Frais")

    val catsFiltrees = categories.filter {
        it.typeDefaut == formulaire.type || it.typeDefaut == "TOUS"
    }

    val compteSelectionne   = comptes.find { it.id == formulaire.compteId }
    val compteDestSelectionne = comptes.find { it.id == formulaire.compteDestId }
    val projetSelectionne   = projets.find { it.id == formulaire.projetId }
    val categorieSelectionnee = catsFiltrees.find { it.id == formulaire.categorieId }
    val modeSelectionne     = modesPaiement.find { it.id == formulaire.modePaiementId }
    val deviseSelectionnee  = devises.find { it.id == formulaire.deviseId }
    val federationSelectionnee = federations.find { it.id == formulaire.federationId }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {

        // ── Type d\'opération ─────────────────────────────────────────────
        Text("Type d\'opération", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { (code, label) ->
                FilterChip(
                    selected = formulaire.type == code,
                    onClick  = { onTypeChange(code) },
                    label    = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        HorizontalDivider()

        // ── Date ─────────────────────────────────────────────────────────
        InnovDateField(
            selectedDate   = formulaire.dateAffichage,
            onDateSelected = { /* parsing géré dans le ViewModel via timestamp */ },
            label          = "Date de l\'opération",
        )

        // ── Libellé ───────────────────────────────────────────────────────
        InnovTextField(
            value         = formulaire.libelle,
            onValueChange = onLibelleChange,
            label         = "Description / Libellé",
            placeholder   = when (formulaire.type) {
                "ENTREE"    -> "Ex : Cotisation Fédération Kinshasa"
                "SORTIE"    -> "Ex : Achat fournitures bureau"
                "TRANSFERT" -> "Ex : Virement caisse → banque"
                "FRAIS"     -> "Ex : Frais bancaires juillet"
                else        -> "Description de l\'opération"
            },
            supportingText = formulaire.erreurLibelle,
            isError        = formulaire.erreurLibelle != null,
        )

        // ── Devise + Montant ──────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InnovDropdown(
                items          = devises,
                selectedItem   = deviseSelectionnee,
                onItemSelected = { onDeviseChange(it.id) },
                label          = "Devise",
                itemLabel      = { it.code },
                modifier       = Modifier.weight(0.35f),
                isError        = formulaire.erreurDevise != null,
                supportingText = formulaire.erreurDevise,
            )
            AmountTextField(
                value         = formulaire.montant,
                onValueChange = onMontantChange,
                label         = "Montant",
                currency      = deviseSelectionnee?.symbole ?: "",
                modifier      = Modifier.weight(0.65f),
                supportingText = formulaire.erreurMontant,
                isError        = formulaire.erreurMontant != null,
            )
        }

        // ── Compte source ─────────────────────────────────────────────────
        InnovDropdown(
            items          = comptes,
            selectedItem   = compteSelectionne,
            onItemSelected = { onCompteChange(it.id) },
            label          = if (formulaire.type == "TRANSFERT") "Compte source" else "Compte",
            itemLabel      = { it.nom },
            isError        = formulaire.erreurCompte != null,
            supportingText = formulaire.erreurCompte,
        )

        // ── Compte destination (transferts uniquement) ─────────────────────
        if (formulaire.type == "TRANSFERT") {
            InnovDropdown(
                items          = comptes.filter { it.id != formulaire.compteId },
                selectedItem   = compteDestSelectionne,
                onItemSelected = { onCompteDestChange(it.id) },
                label          = "Compte destination",
                itemLabel      = { it.nom },
            )
        }

        // ── Catégorie ─────────────────────────────────────────────────────
        InnovDropdown(
            items          = catsFiltrees,
            selectedItem   = categorieSelectionnee,
            onItemSelected = { onCategorieChange(it.id) },
            label          = "Catégorie",
            itemLabel      = { it.nom },
            isError        = formulaire.erreurCategorie != null,
            supportingText = formulaire.erreurCategorie,
        )

        // ── Mode de paiement ──────────────────────────────────────────────
        InnovDropdown(
            items          = modesPaiement,
            selectedItem   = modeSelectionne,
            onItemSelected = { onModeChange(it.id) },
            label          = "Mode de paiement",
            itemLabel      = { it.nom },
            isError        = formulaire.erreurMode != null,
            supportingText = formulaire.erreurMode,
        )

        // ── Projet (optionnel) ────────────────────────────────────────────
        InnovDropdown(
            items          = listOf(null) + projets,
            selectedItem   = projetSelectionne,
            onItemSelected = { onProjetChange(it?.id) },
            label          = "Projet (optionnel)",
            itemLabel      = { it?.nom ?: "— Aucun projet —" },
        )

        // ── Fédération (optionnel — Entrées uniquement) ────────────────────
        if (formulaire.type == "ENTREE") {
            InnovDropdown(
                items          = listOf(null) + federations,
                selectedItem   = federationSelectionnee,
                onItemSelected = { onFederationChange(it?.id) },
                label          = "Fédération (optionnel)",
                itemLabel      = { it?.nom ?: "— Aucune fédération —" },
            )
        }

        // ── N° Pièce ──────────────────────────────────────────────────────
        InnovTextField(
            value         = formulaire.numeroPiece,
            onValueChange = onPieceChange,
            label         = "N° Pièce justificative",
            placeholder   = "Ex : RECU-001, BON-023…",
        )

        // ── Remarques ─────────────────────────────────────────────────────
        InnovTextField(
            value         = formulaire.remarques,
            onValueChange = onRemarquesChange,
            label         = "Remarques (optionnel)",
            placeholder   = "Informations supplémentaires…",
            singleLine    = false,
        )

        Spacer(Modifier.height(4.dp))

        if (formulaire.isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                text    = if (formulaire.id == null) "Enregistrer l\'opération" else "Modifier l\'opération",
                onClick = onSauvegarder,
            )
            TextActionButton("Annuler", onAnnuler, Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(24.dp))
    }
}
