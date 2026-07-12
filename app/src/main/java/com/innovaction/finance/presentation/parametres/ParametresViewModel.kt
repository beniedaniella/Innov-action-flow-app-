package com.innovaction.finance.presentation.parametres

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.entity.*
import com.innovaction.finance.data.notification.AlerteWorker
import com.innovaction.finance.data.repository.ConfigRepository
import com.innovaction.finance.util.AppConstants
import com.innovaction.finance.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametresViewModel @Inject constructor(
    @ApplicationContext private val context : Context,
    private val configRepo     : ConfigRepository,
    private val parametreDao   : ParametreDao,
    private val deviseDao      : DeviseDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParametresUiState())
    val uiState: StateFlow<ParametresUiState> = _uiState.asStateFlow()

    init { charger() }

    private fun charger() {
        viewModelScope.launch {
            combine(
                configRepo.getProjets(),
                configRepo.getModesPaiement(),
                configRepo.getCategories(),
                configRepo.getFederations(),
                configRepo.getParametres(),
            ) { projets, modes, cats, feds, params ->
                val pMap = params.associate { it.cle to it.valeur }
                _uiState.update { s -> s.copy(
                    isLoading       = false,
                    projets         = projets as List<ProjetEntity>,
                    modes           = modes   as List<ModePaiementEntity>,
                    categories      = cats    as List<CategorieEntity>,
                    federations     = feds    as List<FederationEntity>,
                    nomAssociation  = pMap["nom_association"]      ?: s.nomAssociation,
                    exercice        = pMap["exercice_en_cours"]    ?: s.exercice,
                    seuilAlerteCdf  = pMap["seuil_alerte_cdf"]     ?: s.seuilAlerteCdf,
                    seuilAlerteUsd  = pMap["seuil_alerte_usd"]     ?: s.seuilAlerteUsd,
                    tauxUsdCdf      = pMap["taux_usd_cdf"]         ?: s.tauxUsdCdf,
                    rappelAvanceJours= pMap["rappel_avances_jours"]?: s.rappelAvanceJours,
                )}
            }.launchIn(this)

            deviseDao.getAllActive()
                .onEach { devises -> _uiState.update { it.copy(devises = devises) } }
                .launchIn(this)
        }
    }

    // ── Navigation entre sections ─────────────────────────────────────────
    fun allerSection(s: SectionParametres) { _uiState.update { it.copy(section = s) } }
    fun retourAccueil() { _uiState.update { it.copy(section = SectionParametres.ACCUEIL) } }

    // ── Paramètres association ─────────────────────────────────────────────
    fun onNomAssocChange(v: String) { _uiState.update { it.copy(nomAssociation = v) } }
    fun onExerciceChange(v: String) { _uiState.update { it.copy(exercice = v) } }

    fun sauvegarderAssociation() {
        viewModelScope.launch {
            val s = _uiState.value
            configRepo.setParametre("nom_association",    s.nomAssociation)
            configRepo.setParametre("exercice_en_cours",  s.exercice)
            _uiState.update { it.copy(succesMessage = "Paramètres enregistrés") }
        }
    }

    // ── Paramètres alertes ────────────────────────────────────────────────
    fun onSeuilCdfChange(v: String)     { _uiState.update { it.copy(seuilAlerteCdf   = v) } }
    fun onSeuilUsdChange(v: String)     { _uiState.update { it.copy(seuilAlerteUsd   = v) } }
    fun onTauxChange(v: String)         { _uiState.update { it.copy(tauxUsdCdf       = v) } }
    fun onRappelJoursChange(v: String)  { _uiState.update { it.copy(rappelAvanceJours = v) } }

    fun sauvegarderAlertes() {
        viewModelScope.launch {
            val s = _uiState.value
            configRepo.setParametre("seuil_alerte_cdf",    s.seuilAlerteCdf)
            configRepo.setParametre("seuil_alerte_usd",    s.seuilAlerteUsd)
            configRepo.setParametre("taux_usd_cdf",         s.tauxUsdCdf)
            configRepo.setParametre("rappel_avances_jours", s.rappelAvanceJours)
            // Replanifier le worker avec les nouveaux seuils
            AlerteWorker.planifier(context)
            _uiState.update { it.copy(succesMessage = "Alertes mises à jour") }
        }
    }

    // ── Projets ────────────────────────────────────────────────────────────
    fun ouvrirFormulaireProjet(p: ProjetEntity? = null) {
        _uiState.update { it.copy(
            showFormulaire = true,
            formLabel      = p?.nom ?: "",
            formValeur1    = if ((p?.budgetCdf ?: 0.0) > 0) p!!.budgetCdf.toString() else "",
            formValeur2    = if ((p?.budgetUsd ?: 0.0) > 0) p!!.budgetUsd.toString() else "",
            formEditId     = p?.id,
        )}
    }

    fun sauvegarderProjet() {
        val s = _uiState.value
        if (s.formLabel.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(formIsSaving = true) }
            configRepo.saveProjet(ProjetEntity(
                id        = s.formEditId ?: 0L,
                nom       = s.formLabel.trim(),
                budgetCdf = s.formValeur1.toDoubleOrNull() ?: 0.0,
                budgetUsd = s.formValeur2.toDoubleOrNull() ?: 0.0,
                ordre     = s.projets.size,
            ))
            _uiState.update { it.copy(formIsSaving = false, showFormulaire = false,
                succesMessage = "Projet enregistré") }
        }
    }

    // ── Modes de paiement ─────────────────────────────────────────────────
    fun ouvrirFormulaireMode(m: ModePaiementEntity? = null) {
        _uiState.update { it.copy(
            showFormulaire = true,
            formLabel      = m?.nom ?: "",
            formValeur1    = m?.ordre?.toString() ?: _uiState.value.modes.size.toString(),
            formEditId     = m?.id,
        )}
    }

    fun sauvegarderMode() {
        val s = _uiState.value
        if (s.formLabel.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(formIsSaving = true) }
            configRepo.saveModePaiement(ModePaiementEntity(
                id    = s.formEditId ?: 0L,
                nom   = s.formLabel.trim(),
                ordre = s.formValeur1.toIntOrNull() ?: s.modes.size,
            ))
            _uiState.update { it.copy(formIsSaving = false, showFormulaire = false,
                succesMessage = "Mode de paiement enregistré") }
        }
    }

    // ── Catégories ─────────────────────────────────────────────────────────
    fun ouvrirFormulaireCategorie(c: CategorieEntity? = null) {
        _uiState.update { it.copy(
            showFormulaire = true,
            formLabel      = c?.nom ?: "",
            formValeur1    = c?.typeDefaut ?: "TOUS",
            formEditId     = c?.id,
        )}
    }

    fun sauvegarderCategorie() {
        val s = _uiState.value
        if (s.formLabel.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(formIsSaving = true) }
            configRepo.saveCategorie(CategorieEntity(
                id         = s.formEditId ?: 0L,
                nom        = s.formLabel.trim(),
                typeDefaut = s.formValeur1.ifBlank { "TOUS" },
                ordre      = s.categories.size,
            ))
            _uiState.update { it.copy(formIsSaving = false, showFormulaire = false,
                succesMessage = "Catégorie enregistrée") }
        }
    }

    // ── Fédérations ────────────────────────────────────────────────────────
    fun ouvrirFormulaireFederation(f: FederationEntity? = null) {
        _uiState.update { it.copy(
            showFormulaire = true,
            formLabel      = f?.nom ?: "",
            formValeur1    = f?.description ?: "",
            formEditId     = f?.id,
        )}
    }

    fun sauvegarderFederation() {
        val s = _uiState.value
        if (s.formLabel.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(formIsSaving = true) }
            configRepo.saveFederation(FederationEntity(
                id          = s.formEditId ?: 0L,
                nom         = s.formLabel.trim(),
                description = s.formValeur1.trim(),
                ordre       = s.federations.size,
            ))
            _uiState.update { it.copy(formIsSaving = false, showFormulaire = false,
                succesMessage = "Fédération enregistrée") }
        }
    }

    // ── Formulaire générique ───────────────────────────────────────────────
    fun onFormLabelChange(v: String)   { _uiState.update { it.copy(formLabel   = v) } }
    fun onFormValeur1Change(v: String) { _uiState.update { it.copy(formValeur1 = v) } }
    fun onFormValeur2Change(v: String) { _uiState.update { it.copy(formValeur2 = v) } }
    fun fermerFormulaire() { _uiState.update { it.copy(showFormulaire = false) } }

    // ── Préférences ────────────────────────────────────────────────────────
    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkMode = enabled) }
        viewModelScope.launch {
            configRepo.setParametre("dark_mode", enabled.toString())
        }
    }

    fun effacerMessages() { _uiState.update { it.copy(succesMessage = null, error = null) } }
}
