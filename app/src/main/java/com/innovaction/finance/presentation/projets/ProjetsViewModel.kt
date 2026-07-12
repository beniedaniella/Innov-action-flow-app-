package com.innovaction.finance.presentation.projets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.local.dao.DeviseDao
import com.innovaction.finance.data.local.dao.OperationDao
import com.innovaction.finance.data.local.dao.ProjetDao
import com.innovaction.finance.data.local.entity.ProjetEntity
import com.innovaction.finance.data.repository.ConfigRepository
import com.innovaction.finance.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProjetsViewModel @Inject constructor(
    private val projetDao    : ProjetDao,
    private val operationDao : OperationDao,
    private val deviseDao    : DeviseDao,
    private val configRepo   : ConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjetsUiState())
    val uiState: StateFlow<ProjetsUiState> = _uiState.asStateFlow()

    init { charger() }

    private fun charger() {
        viewModelScope.launch {
            // Devises disponibles
            deviseDao.getAllActive()
                .onEach { devises -> _uiState.update { it.copy(devises = devises) } }
                .launchIn(this)

            // Projets + stats calculées en temps réel
            projetDao.getAllActive()
                .collect { projets ->
                    val devCdf = deviseDao.getByCode("CDF")
                    val devUsd = deviseDao.getByCode("USD")

                    val stats = projets.map { projet ->
                        val entCdf = if (devCdf != null)
                            operationDao.sumEntreesParProjet(projet.id, devCdf.id).first() else 0.0
                        val sorCdf = if (devCdf != null)
                            operationDao.sumSortiesParProjet(projet.id, devCdf.id).first() else 0.0
                        val entUsd = if (devUsd != null)
                            operationDao.sumEntreesParProjet(projet.id, devUsd.id).first() else 0.0
                        val sorUsd = if (devUsd != null)
                            operationDao.sumSortiesParProjet(projet.id, devUsd.id).first() else 0.0
                        val nbOps = operationDao
                            .searchWithDetails(projetId = projet.id, limit = 1000)
                            .first().size

                        ProjetStats(
                            projet       = projet,
                            entreesCdf   = entCdf,
                            sortiesCdf   = sorCdf,
                            soldeCdf     = entCdf - sorCdf,
                            pctBudgetCdf = if (projet.budgetCdf > 0)
                                (sorCdf / projet.budgetCdf).toFloat().coerceIn(0f, 2f) else 0f,
                            entreesUsd   = entUsd,
                            sortiesUsd   = sorUsd,
                            soldeUsd     = entUsd - sorUsd,
                            pctBudgetUsd = if (projet.budgetUsd > 0)
                                (sorUsd / projet.budgetUsd).toFloat().coerceIn(0f, 2f) else 0f,
                            nbOperations = nbOps,
                        )
                    }
                    _uiState.update { it.copy(isLoading = false, projetsStats = stats) }
                }
        }
    }

    // ── Détail d'un projet ────────────────────────────────────────────────
    fun selectionnerProjet(stats: ProjetStats) {
        viewModelScope.launch {
            _uiState.update { it.copy(projetSelectionne = stats) }

            // Opérations du projet
            operationDao.getByProjet(stats.projet.id)
                .onEach { ops -> _uiState.update { it.copy(operationsProjet = ops) } }
                .launchIn(this)

            // Graphique 6 mois
            val graphique = calculerGraphique6Mois(stats.projet.id)
            _uiState.update { it.copy(graphiqueMois = graphique) }
        }
    }

    fun deselectionnerProjet() {
        _uiState.update { it.copy(
            projetSelectionne = null,
            operationsProjet  = emptyList(),
            graphiqueMois     = emptyList(),
        )}
    }

    private suspend fun calculerGraphique6Mois(projetId: Long): List<ProjetMoisData> {
        val devCdf = deviseDao.getByCode("CDF") ?: return emptyList()
        val cal    = Calendar.getInstance()
        val fmtMois = SimpleDateFormat("MMM", Locale.FRENCH)
        val fmtNum  = SimpleDateFormat("M", Locale.getDefault())
        val fmtAn   = SimpleDateFormat("yyyy", Locale.getDefault())

        return (5 downTo 0).map { i ->
            cal.time = Date()
            cal.add(Calendar.MONTH, -i)
            val entrees = operationDao.sumEntreesParProjet(projetId, devCdf.id).first()
            val sorties = operationDao.sumSortiesParProjet(projetId, devCdf.id).first()
            ProjetMoisData(
                label      = fmtMois.format(cal.time).replaceFirstChar { it.uppercase() },
                entreesCdf = entrees,
                sortiesCdf = sorties,
            )
        }
    }

    // ── Formulaire ────────────────────────────────────────────────────────
    fun ouvrirFormulaire(stats: ProjetStats? = null) {
        _uiState.update { s -> s.copy(
            showFormulaire = true,
            formProjetId   = stats?.projet?.id,
            formNom        = stats?.projet?.nom ?: "",
            formDescription= stats?.projet?.description ?: "",
            formBudgetCdf  = if ((stats?.projet?.budgetCdf ?: 0.0) > 0)
                stats!!.projet.budgetCdf.toString() else "",
            formBudgetUsd  = if ((stats?.projet?.budgetUsd ?: 0.0) > 0)
                stats!!.projet.budgetUsd.toString() else "",
            formCouleur    = stats?.projet?.couleur ?: "#1F3864",
        )}
    }

    fun fermerFormulaire() { _uiState.update { it.copy(showFormulaire = false) } }

    fun onNomChange(v: String)         { _uiState.update { it.copy(formNom = v) } }
    fun onDescChange(v: String)        { _uiState.update { it.copy(formDescription = v) } }
    fun onBudgetCdfChange(v: String)   { _uiState.update { it.copy(formBudgetCdf = v) } }
    fun onBudgetUsdChange(v: String)   { _uiState.update { it.copy(formBudgetUsd = v) } }
    fun onCouleurChange(v: String)     { _uiState.update { it.copy(formCouleur = v) } }

    fun sauvegarder() {
        val s = _uiState.value
        if (s.formNom.isBlank()) {
            _uiState.update { it.copy(error = "Le nom du projet est obligatoire") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(formIsSaving = true) }
            val projet = ProjetEntity(
                id          = s.formProjetId ?: 0L,
                nom         = s.formNom.trim(),
                description = s.formDescription.trim(),
                budgetCdf   = s.formBudgetCdf.toDoubleOrNull() ?: 0.0,
                budgetUsd   = s.formBudgetUsd.toDoubleOrNull() ?: 0.0,
                couleur     = s.formCouleur,
                ordre       = s.projetsStats.size,
            )
            when (val r = configRepo.saveProjet(projet)) {
                is Result.Success -> _uiState.update {
                    it.copy(formIsSaving = false, showFormulaire = false)
                }
                is Result.Error   -> _uiState.update {
                    it.copy(formIsSaving = false, error = r.message)
                }
                else -> {}
            }
        }
    }

    // ── Mise à jour budget rapide ─────────────────────────────────────────
    fun mettreAJourBudget(id: Long, cdf: Double, usd: Double) {
        viewModelScope.launch {
            configRepo.updateBudget(id, cdf, usd)
        }
    }

    // ── Désactivation ─────────────────────────────────────────────────────
    fun demanderDesactivation(stats: ProjetStats) {
        _uiState.update { it.copy(showConfirmDesactiver = stats) }
    }
    fun annulerDesactivation() { _uiState.update { it.copy(showConfirmDesactiver = null) } }
    fun confirmerDesactivation() {
        val stats = _uiState.value.showConfirmDesactiver ?: return
        viewModelScope.launch {
            val p = stats.projet.copy(isActive = false)
            configRepo.saveProjet(p)
            _uiState.update { it.copy(showConfirmDesactiver = null) }
        }
    }

    fun effacerErreur() { _uiState.update { it.copy(error = null) } }
}
