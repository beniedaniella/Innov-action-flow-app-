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
import com.innovaction.finance.presentation.operations.OperationFormState
import com.innovaction.finance.presentation.operations.TypeOperation

/**
 * Formulaire adaptatif selon le type d'opération.
 * Tous les champs sont alimentés par des entités Room — 0 valeur codée.
 */
@Composable
fun OperationFormFields(
    form              : OperationFormState,
    comptes           : List<CompteEntity>,
    projets           : List<ProjetEntity>,
    categories        : List<CategorieEntity>,
    modesPaiement     : List<ModePaiementEntity>,
    devises           : List<DeviseEntity>,
    federations       : List<FederationEntity>,
    isEdition         : Boolean,
    onLibelleChange   : (String)  -> Unit,
    onMontantChange   : (String)  -> Unit,
    onDateChange      : (Long, String) -> Unit,
    onCompteChange    : (Long)    -> Unit,
    onCompteDestChange: (Long?)   -> Unit,
    onProjetChange    : (Long?)   -> Unit,
    onCategorieChange : (Long)    -> Unit,
    onModeChange      : (Long)    -> Unit,
    onDeviseChange    : (Long)    -> Unit,
    onFederationChange: (Long?)   -> Unit,
    onPieceChange     : (String)  -> Unit,
    onRemarquesChange : (String)  -> Unit,
    onSauvegarder     : ()        -> Unit,
    onAnnuler         : ()        -> Unit,
    modifier          : Modifier  = Modifier,
) {
    val deviseSelectionnee = devises.find { it.id == form.deviseId }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // ── Description ───────────────────────────────────────────────────
        InnovTextField(
            value         = form.libelle,
            onValueChange = onLibelleChange,
            label         = "Description *",
            placeholder   = "Ex : Cotisation Fédération Kinshasa",
            isError       = form.erreurLibelle != null,
            supportingText = form.erreurLibelle,
        )

        // ── Montant + Devise ───────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InnovDropdown(
                items          = devises,
                selectedItem   = deviseSelectionnee,
                onItemSelected = { onDeviseChange(it.id) },
                label          = "Devise *",
                itemLabel      = { it.code },
                isError        = form.erreurDevise != null,
                modifier       = Modifier.width(110.dp),
            )
            AmountTextField(
                value         = form.montant,
                onValueChange = onMontantChange,
                label         = "Montant *",
                currency      = deviseSelectionnee?.symbole ?: "",
                isError       = form.erreurMontant != null,
                supportingText = form.erreurMontant,
                modifier      = Modifier.weight(1f),
            )
        }

        // ── Date ───────────────────────────────────────────────────────────
        InnovDateField(
            selectedDate   = form.dateAffichee,
            onDateSelected = { affichee ->
                // Convertir la date affichée en timestamp
                try {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    val ts  = sdf.parse(affichee)?.time ?: System.currentTimeMillis()
                    onDateChange(ts, affichee)
                } catch (_: Exception) { onDateChange(System.currentTimeMillis(), affichee) }
            },
            label = "Date *",
        )

        // ── Compte principal ───────────────────────────────────────────────
        InnovDropdown(
            items          = comptes,
            selectedItem   = comptes.find { it.id == form.compteId },
            onItemSelected = { onCompteChange(it.id) },
            label          = "Compte *",
            itemLabel      = { it.nom },
            isError        = form.erreurCompte != null,
            supportingText = form.erreurCompte,
        )

        // ── Compte destination (transferts uniquement) ─────────────────────
        if (form.type == TypeOperation.TRANSFERT) {
            InnovDropdown(
                items          = comptes.filter { it.id != form.compteId },
                selectedItem   = comptes.find { it.id == form.compteDestId },
                onItemSelected = { onCompteDestChange(it.id) },
                label          = "Compte destinataire *",
                itemLabel      = { it.nom },
                supportingText = "Compte qui reçoit le transfert",
            )
        }

        // ── Catégorie ──────────────────────────────────────────────────────
        InnovDropdown(
            items          = categories,
            selectedItem   = categories.find { it.id == form.categorieId },
            onItemSelected = { onCategorieChange(it.id) },
            label          = "Catégorie *",
            itemLabel      = { it.nom },
            isError        = form.erreurCategorie != null,
            supportingText = form.erreurCategorie,
        )

        // ── Mode de paiement ───────────────────────────────────────────────
        InnovDropdown(
            items          = modesPaiement,
            selectedItem   = modesPaiement.find { it.id == form.modePaiementId },
            onItemSelected = { onModeChange(it.id) },
            label          = "Mode de paiement *",
            itemLabel      = { it.nom },
            isError        = form.erreurMode != null,
            supportingText = form.erreurMode,
        )

        // ── Projet (optionnel) ─────────────────────────────────────────────
        InnovDropdown(
            items          = listOf(null) + projets,
            selectedItem   = projets.find { it.id == form.projetId },
            onItemSelected = { onProjetChange(it?.id) },
            label          = "Projet (optionnel)",
            itemLabel      = { it?.nom ?: "— Aucun projet —" },
        )

        // ── Fédération (optionnel) ─────────────────────────────────────────
        if (form.type == TypeOperation.ENTREE) {
            InnovDropdown(
                items          = listOf(null) + federations,
                selectedItem   = federations.find { it.id == form.federationId },
                onItemSelected = { onFederationChange(it?.id) },
                label          = "Fédération (optionnel)",
                itemLabel      = { it?.nom ?: "— Aucune —" },
            )
        }

        // ── Numéro de pièce ────────────────────────────────────────────────
        InnovTextField(
            value         = form.numeroPiece,
            onValueChange = onPieceChange,
            label         = "N° Pièce justificative",
            placeholder   = "RECU-001, BON-001…",
        )

        // ── Remarques ──────────────────────────────────────────────────────
        InnovTextField(
            value         = form.remarques,
            onValueChange = onRemarquesChange,
            label         = "Remarques",
            singleLine    = false,
            placeholder   = "Informations complémentaires…",
        )

        Spacer(Modifier.height(4.dp))

        // ── Boutons ────────────────────────────────────────────────────────
        if (form.isSaving) {
            Box(Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                text  = if (isEdition) "Modifier l'opération" else "Enregistrer l'opération",
                onClick = onSauvegarder,
            )
            TextActionButton("Annuler", onAnnuler, Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(32.dp))
    }
}
