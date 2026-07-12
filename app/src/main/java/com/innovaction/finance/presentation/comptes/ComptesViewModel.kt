package com.innovaction.finance.presentation.comptes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.local.dao.CompteDao
import com.innovaction.finance.data.local.dao.DeviseDao
import com.innovaction.finance.data.local.dao.OperationDao
import com.innovaction.finance.data.local.entity.CompteEntity
import com.innovaction.finance.data.repository.ConfigRepository
import com.innovaction.finance.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComptesViewModel @Inject constructor(
    private val compteDao     : CompteDao,
    private val deviseDao     : DeviseDao,
    private val operationDao  : OperationDao,
    private val configRepo    : ConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComptesUiState())
    val uiState: StateFlow<ComptesUiState> = _uiState.asStateFlow()

    init { chargerComptes() }

    // ── Chargement ─────────────────────────────────────────────────────────
    private fun chargerComptes() {
        viewModelScope.launch {
            compteDao.getAllWithDevise()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { comptes ->
                    val details = comptes.map { cwd ->
                        val devId = cwd.devise.id
                        val solde = compteDao.getSoldeCalcule(cwd.compte.id)
                        val entrees = operationDao.sumEntreesParCompte(cwd.compte.id, devId).first()
                        val sorties = operationDao.sumSortiesParCompte(cwd.compte.id, devId).first()
                        CompteDetail(cwd, solde, entrees, sorties)
                    }
                    _uiState.update {
                        it.copy(isLoading = false, comptes = details)
                    }
                }
        }
    }

    // ── Sélection d'un compte (pour voir son historique) ──────────────────
    fun selectionnerCompte(detail: CompteDetail) {
        viewModelScope.launch {
            _uiState.update { it.copy(compteSelectionne = detail) }
            operationDao.getByCompte(detail.compteWithDevise.compte.id)
                .collect { ops ->
                    _uiState.update { it.copy(historiqueOps = ops) }
                }
        }
    }

    fun deselectionnerCompte() {
        _uiState.update { it.copy(compteSelectionne = null, historiqueOps = emptyList()) }
    }

    // ── Formulaire ajout ───────────────────────────────────────────────────
    fun ouvrirFormulaire(existant: CompteEntity? = null) {
        _uiState.update {
            it.copy(
                showFormulaire   = true,
                formNom          = existant?.nom ?: "",
                formDeviseId     = existant?.deviseId,
                formSoldeInitial = existant?.soldeInitial?.toString() ?: "0",
                formCouleur      = existant?.couleur ?: "#1F3864",
            )
        }
    }

    fun fermerFormulaire() {
        _uiState.update { it.copy(showFormulaire = false) }
    }

    fun onNomChange(v: String)          { _uiState.update { it.copy(formNom = v) } }
    fun onDeviseChange(id: Long)        { _uiState.update { it.copy(formDeviseId = id) } }
    fun onSoldeInitialChange(v: String) { _uiState.update { it.copy(formSoldeInitial = v) } }
    fun onCouleurChange(v: String)      { _uiState.update { it.copy(formCouleur = v) } }

    fun sauvegarderCompte() {
        val state = _uiState.value
        if (state.formNom.isBlank() || state.formDeviseId == null) {
            _uiState.update { it.copy(error = "Nom et devise sont obligatoires") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(formIsSaving = true) }
            val compte = CompteEntity(
                nom          = state.formNom.trim(),
                deviseId     = state.formDeviseId,
                soldeInitial = state.formSoldeInitial.toDoubleOrNull() ?: 0.0,
                couleur      = state.formCouleur,
                ordre        = state.comptes.size,
            )
            when (val r = configRepo.saveCompte(compte)) {
                is Result.Success -> _uiState.update {
                    it.copy(formIsSaving = false, showFormulaire = false)
                }
                is Result.Error -> _uiState.update {
                    it.copy(formIsSaving = false, error = r.message)
                }
                else -> {}
            }
        }
    }

    fun effacerErreur() { _uiState.update { it.copy(error = null) } }
}
