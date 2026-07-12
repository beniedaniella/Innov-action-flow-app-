package com.innovaction.finance.presentation.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.entity.OperationEntity
import com.innovaction.finance.data.local.relation.OperationWithDetails
import com.innovaction.finance.data.repository.OperationRepository
import com.innovaction.finance.util.AppConstants
import com.innovaction.finance.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OperationsViewModel @Inject constructor(
    private val operationRepo  : OperationRepository,
    private val compteDao      : CompteDao,
    private val projetDao      : ProjetDao,
    private val categorieDao   : CategorieDao,
    private val modePaiementDao: ModePaiementDao,
    private val deviseDao      : DeviseDao,
    private val federationDao  : FederationDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperationsUiState())
    val uiState: StateFlow<OperationsUiState> = _uiState.asStateFlow()

    private val fmtDate = SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault())
    private val fmtAnnee = SimpleDateFormat("yyyy", Locale.getDefault())

    init {
        chargerReferentiels()
        observerJournal()
        observerSoldes()
    }

    // ── Chargement des référentiels (dropdowns) ────────────────────────────
    private fun chargerReferentiels() {
        viewModelScope.launch {
            combine(
                compteDao.getAllActive(),
                projetDao.getAllActive(),
                categorieDao.getAllActive(),
                modePaiementDao.getAllActive(),
                deviseDao.getAllActive(),
                federationDao.getAllActive(),
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                _uiState.update { s -> s.copy(
                    comptes       = args[0] as List<com.innovaction.finance.data.local.entity.CompteEntity>,
                    projets       = args[1] as List<com.innovaction.finance.data.local.entity.ProjetEntity>,
                    categories    = args[2] as List<com.innovaction.finance.data.local.entity.CategorieEntity>,
                    modesPaiement = args[3] as List<com.innovaction.finance.data.local.entity.ModePaiementEntity>,
                    devises       = args[4] as List<com.innovaction.finance.data.local.entity.DeviseEntity>,
                    federations   = args[5] as List<com.innovaction.finance.data.local.entity.FederationEntity>,
                    isLoading     = false,
                )}
            }.launchIn(viewModelScope)
        }
    }

    // ── Journal — réactif aux filtres ──────────────────────────────────────
    private fun observerJournal() {
        _uiState
            .map { it.filtres }
            .distinctUntilChanged()
            .flatMapLatest { f ->
                operationRepo.search(
                    type         = f.type,
                    compteId     = f.compteId,
                    projetId     = f.projetId,
                    deviseId     = f.deviseId,
                    federationId = f.federationId,
                    dateDebut    = f.dateDebut,
                    dateFin      = f.dateFin,
                    recherche    = f.recherche.ifBlank { null },
                    limit        = 100,
                )
            }
            .onEach { ops -> _uiState.update { it.copy(operations = ops) } }
            .launchIn(viewModelScope)
    }

    // ── Soldes en temps réel ──────────────────────────────────────────────
    private fun observerSoldes() {
        viewModelScope.launch {
            val cdf = deviseDao.getByCode("CDF")
            val usd = deviseDao.getByCode("USD")
            if (cdf != null) {
                operationRepo.sumEntrees(cdf.id)
                    .combine(operationRepo.sumSorties(cdf.id)) { e, s -> e - s }
                    .collect { solde -> _uiState.update { it.copy(soldeCdf = solde) } }
            }
            if (usd != null) {
                operationRepo.sumEntrees(usd.id)
                    .combine(operationRepo.sumSorties(usd.id)) { e, s -> e - s }
                    .collect { solde -> _uiState.update { it.copy(soldeUsd = solde) } }
            }
        }
    }

    // ── Filtres ────────────────────────────────────────────────────────────
    fun setRecherche(v: String)         { _uiState.update { it.copy(filtres = it.filtres.copy(recherche    = v)) } }
    fun setFiltreType(v: String?)       { _uiState.update { it.copy(filtres = it.filtres.copy(type         = v)) } }
    fun setFiltreCompte(v: Long?)       { _uiState.update { it.copy(filtres = it.filtres.copy(compteId     = v)) } }
    fun setFiltreProjet(v: Long?)       { _uiState.update { it.copy(filtres = it.filtres.copy(projetId     = v)) } }
    fun setFiltreDevise(v: Long?)       { _uiState.update { it.copy(filtres = it.filtres.copy(deviseId     = v)) } }
    fun setFiltreFederation(v: Long?)   { _uiState.update { it.copy(filtres = it.filtres.copy(federationId = v)) } }
    fun reinitialiserFiltres()          { _uiState.update { it.copy(filtres = FiltresJournal()) } }

    // ── Formulaire — ouverture ─────────────────────────────────────────────
    fun ouvrirNouvelleOperation(typeDefaut: String = "ENTREE") {
        val now = Date()
        _uiState.update { s ->
            // Pré-sélectionner la première devise disponible
            val premiereDevise = s.devises.firstOrNull()
            val premierCompte  = s.comptes.firstOrNull()
            val premierMode    = s.modesPaiement.firstOrNull()
            val categoriesType = s.categories.filter { it.typeDefaut == typeDefaut || it.typeDefaut == "TOUS" }
            s.copy(
                showFormulaire = true,
                formulaire     = FormulaireOperation(
                    type           = typeDefaut,
                    date           = now.time,
                    dateAffichage  = fmtDate.format(now),
                    deviseId       = premiereDevise?.id,
                    compteId       = premierCompte?.id,
                    modePaiementId = premierMode?.id,
                    categorieId    = categoriesType.firstOrNull()?.id,
                ),
            )
        }
    }

    fun ouvrirEdition(op: OperationWithDetails) {
        _uiState.update { s ->
            s.copy(
                showFormulaire = true,
                formulaire     = FormulaireOperation(
                    id             = op.operation.id,
                    type           = op.operation.type,
                    date           = op.operation.date,
                    dateAffichage  = fmtDate.format(Date(op.operation.date)),
                    libelle        = op.operation.libelle,
                    montant        = op.operation.montant.toString(),
                    compteId       = op.operation.compteId,
                    compteDestId   = op.operation.compteDestId,
                    projetId       = op.operation.projetId,
                    categorieId    = op.operation.categorieId,
                    modePaiementId = op.operation.modePaiementId,
                    deviseId       = op.operation.deviseId,
                    federationId   = op.operation.federationId,
                    numeroPiece    = op.operation.numeroPiece,
                    remarques      = op.operation.remarques,
                ),
            )
        }
    }

    fun fermerFormulaire() { _uiState.update { it.copy(showFormulaire = false) } }

    // ── Formulaire — mutations ─────────────────────────────────────────────
    fun onTypeChange(v: String) {
        _uiState.update { s ->
            val cats = s.categories.filter { it.typeDefaut == v || it.typeDefaut == "TOUS" }
            s.copy(formulaire = s.formulaire.copy(
                type        = v,
                categorieId = cats.firstOrNull()?.id,
            ))
        }
    }
    fun onLibelleChange(v: String)    { update { copy(libelle        = v, erreurLibelle   = null) } }
    fun onMontantChange(v: String)    { update { copy(montant        = v, erreurMontant   = null) } }
    fun onCompteChange(v: Long)       { update { copy(compteId       = v, erreurCompte    = null) } }
    fun onCompteDestChange(v: Long?)  { update { copy(compteDestId   = v) } }
    fun onProjetChange(v: Long?)      { update { copy(projetId       = v) } }
    fun onCategorieChange(v: Long)    { update { copy(categorieId    = v, erreurCategorie = null) } }
    fun onModeChange(v: Long)         { update { copy(modePaiementId = v, erreurMode      = null) } }
    fun onDeviseChange(v: Long)       { update { copy(deviseId       = v, erreurDevise    = null) } }
    fun onFederationChange(v: Long?)  { update { copy(federationId   = v) } }
    fun onPieceChange(v: String)      { update { copy(numeroPiece    = v) } }
    fun onRemarquesChange(v: String)  { update { copy(remarques      = v) } }
    fun onDateChange(timestamp: Long) {
        update { copy(date = timestamp, dateAffichage = fmtDate.format(Date(timestamp))) }
    }

    private fun update(block: FormulaireOperation.() -> FormulaireOperation) {
        _uiState.update { it.copy(formulaire = it.formulaire.block()) }
    }

    // ── Sauvegarde ────────────────────────────────────────────────────────
    fun sauvegarder() {
        val f = _uiState.value.formulaire
        if (!valider()) return

        viewModelScope.launch {
            _uiState.update { it.copy(formulaire = it.formulaire.copy(isSaving = true)) }

            val entity = OperationEntity(
                id             = f.id ?: 0L,
                date           = f.date,
                libelle        = f.libelle.trim(),
                type           = f.type,
                montant        = f.montant.toDouble(),
                compteId       = f.compteId!!,
                compteDestId   = f.compteDestId,
                projetId       = f.projetId,
                categorieId    = f.categorieId!!,
                modePaiementId = f.modePaiementId!!,
                deviseId       = f.deviseId!!,
                federationId   = f.federationId,
                numeroPiece    = f.numeroPiece.trim(),
                remarques      = f.remarques.trim(),
            )

            val result = if (f.id == null) operationRepo.save(entity)
                         else              operationRepo.update(entity)

            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(showFormulaire = false,
                        formulaire = it.formulaire.copy(isSaving = false))
                }
                is Result.Error   -> _uiState.update {
                    it.copy(error = result.message,
                        formulaire = it.formulaire.copy(isSaving = false))
                }
                else -> {}
            }
        }
    }

    private fun valider(): Boolean {
        val f = _uiState.value.formulaire
        var ok = true
        _uiState.update { s -> s.copy(formulaire = s.formulaire.copy(
            erreurLibelle    = if (f.libelle.isBlank())              { ok=false; "Libellé obligatoire" }      else null,
            erreurMontant    = if (f.montant.toDoubleOrNull() == null || f.montant.toDouble() <= 0)
                               { ok=false; "Montant invalide" }      else null,
            erreurCompte     = if (f.compteId == null)               { ok=false; "Compte obligatoire" }       else null,
            erreurCategorie  = if (f.categorieId == null)            { ok=false; "Catégorie obligatoire" }    else null,
            erreurMode       = if (f.modePaiementId == null)         { ok=false; "Mode de paiement requis" }  else null,
            erreurDevise     = if (f.deviseId == null)               { ok=false; "Devise obligatoire" }       else null,
        ))}
        return ok
    }

    // ── Suppression ────────────────────────────────────────────────────────
    fun demanderSuppression(op: OperationWithDetails) {
        _uiState.update { it.copy(showConfirmDelete = op) }
    }
    fun annulerSuppression() { _uiState.update { it.copy(showConfirmDelete = null) } }
    fun confirmerSuppression() {
        val op = _uiState.value.showConfirmDelete ?: return
        viewModelScope.launch {
            when (val r = operationRepo.delete(op.operation)) {
                is Result.Error -> _uiState.update { it.copy(error = r.message, showConfirmDelete = null) }
                else            -> _uiState.update { it.copy(showConfirmDelete = null) }
            }
        }
    }

    // ── Détail ─────────────────────────────────────────────────────────────
    fun voirDetail(op: OperationWithDetails) {
        _uiState.update { it.copy(operationDetail = op, showDetail = true) }
    }
    fun fermerDetail() { _uiState.update { it.copy(showDetail = false, operationDetail = null) } }

    fun effacerErreur() { _uiState.update { it.copy(error = null) } }
}
