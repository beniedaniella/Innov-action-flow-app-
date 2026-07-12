package com.innovaction.finance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.local.dao.AvanceDao
import com.innovaction.finance.data.local.dao.CompteDao
import com.innovaction.finance.data.local.dao.DeviseDao
import com.innovaction.finance.data.local.dao.OperationDao
import com.innovaction.finance.data.local.dao.ProjetDao
import com.innovaction.finance.data.local.dao.ParametreDao
import com.innovaction.finance.data.local.relation.CompteWithDevise
import com.innovaction.finance.data.local.entity.ProjetEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val operationDao : OperationDao,
    private val avanceDao    : AvanceDao,
    private val compteDao    : CompteDao,
    private val deviseDao    : DeviseDao,
    private val projetDao    : ProjetDao,
    private val parametreDao : ParametreDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { chargerDonnees() }

    private fun chargerDonnees() {
        viewModelScope.launch {
            try {
                // Flux combinés — se mettent à jour automatiquement dès que la DB change
                combine(
                    compteDao.getAllWithDevise(),
                    avanceDao.countEnRetard(),
                    avanceDao.getEnRetard(),
                    operationDao.countAll(),
                    operationDao.getRecents(5),
                    projetDao.getAllActive(),
                ) { args ->
                    val comptes          = args[0] as List<*>
                    val nbRetard         = args[1] as Int
                    val avancesRetard    = args[2] as List<*>
                    val nbOps            = args[3] as Int
                    val recentes         = args[4] as List<*>
                    val projets          = args[5] as List<*>

                    // Paramètres (lecture ponctuelle, pas besoin de flow ici)
                    val seuilCdf = parametreDao.getValeur("seuil_alerte_cdf")?.toDoubleOrNull() ?: 500_000.0
                    val seuilUsd = parametreDao.getValeur("seuil_alerte_usd")?.toDoubleOrNull() ?: 150.0
                    val nomAsso  = parametreDao.getValeur("nom_association") ?: "INNOV'ACTION"
                    val exercice = parametreDao.getValeur("exercice_en_cours") ?: "2026"

                    // Calcul des soldes par devise
                    @Suppress("UNCHECKED_CAST")
                    val comptesTyped = comptes as List<CompteWithDevise>
                    val comptesResumes = mutableListOf<CompteResume>()
                    var soldeCdf = 0.0
                    var soldeUsd = 0.0

                    for (cwd in comptesTyped) {
                        val solde = compteDao.getSoldeCalcule(cwd.compte.id)
                        comptesResumes.add(CompteResume(
                            nom          = cwd.compte.nom,
                            solde        = solde,
                            deviseCode   = cwd.devise.code,
                            deviseSymbole = cwd.devise.symbole,
                            couleur      = cwd.compte.couleur,
                        ))
                        when (cwd.devise.code) {
                            "CDF" -> soldeCdf += solde
                            "USD" -> soldeUsd += solde
                        }
                    }

                    // Totaux entrées/sorties (toutes périodes, depuis les DAOs déjà collectés)
                    val devCdf = deviseDao.getByCode("CDF")
                    val devUsd = deviseDao.getByCode("USD")

                    // Graphique 6 derniers mois
                    val graphique = calculerGraphique6Mois(devCdf?.id ?: 1L)

                    // Projets avec dépenses
                    @Suppress("UNCHECKED_CAST")
                    val projetsTyped = projets as List<ProjetEntity>
                    val projetsResumes = projetsTyped.map { projet ->
                        val depenses = if (devCdf != null)
                            operationDao.sumSortiesParProjet(projet.id, devCdf.id).first()
                        else 0.0
                        ProjetResume(
                            id          = projet.id,
                            nom         = projet.nom,
                            budgetCdf   = projet.budgetCdf,
                            depensesCdf = depenses,
                            couleur     = projet.couleur,
                        )
                    }

                    // Totaux globaux
                    val entCdf = if (devCdf != null) operationDao.sumEntrees(devCdf.id).first() else 0.0
                    val sorCdf = if (devCdf != null) operationDao.sumSorties(devCdf.id).first() else 0.0
                    val entUsd = if (devUsd != null) operationDao.sumEntrees(devUsd.id).first() else 0.0
                    val sorUsd = if (devUsd != null) operationDao.sumSorties(devUsd.id).first() else 0.0

                    @Suppress("UNCHECKED_CAST")
                    DashboardUiState(
                        isLoading           = false,
                        soldeTotalCdf       = soldeCdf,
                        soldeTotalUsd       = soldeUsd,
                        comptes             = comptesResumes,
                        totalEntreesCdf     = entCdf,
                        totalSortiesCdf     = sorCdf,
                        totalEntreesUsd     = entUsd,
                        totalSortiesUsd     = sorUsd,
                        nbOperations        = nbOps as Int,
                        nbAvancesEnRetard   = nbRetard as Int,
                        alerteSoldeBasCdf   = soldeCdf < seuilCdf,
                        alerteSoldeBasUsd   = soldeUsd < seuilUsd,
                        seuilAlerteCdf      = seuilCdf,
                        seuilAlerteUsd      = seuilUsd,
                        donneesGraphique    = graphique,
                        projets             = projetsResumes,
                        dernieresOperations = recentes as List<com.innovaction.finance.data.local.relation.OperationWithDetails>,
                        avancesEnRetard     = avancesRetard as List<com.innovaction.finance.data.local.relation.AvanceWithDetails>,
                        nomAssociation      = nomAsso,
                        exercice            = exercice,
                    )
                }.catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun calculerGraphique6Mois(deviseCdfId: Long): List<MoisData> {
        val cal    = Calendar.getInstance()
        val result = mutableListOf<MoisData>()
        val fmtMois = SimpleDateFormat("MMM", Locale.FRENCH)
        val fmtAnnee = SimpleDateFormat("yyyy", Locale.getDefault())
        val fmtMoisNum = SimpleDateFormat("M", Locale.getDefault())

        for (i in 5 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.MONTH, -i)

            val mois  = fmtMoisNum.format(cal.time).toInt()
            val annee = fmtAnnee.format(cal.time)
            val label = fmtMois.format(cal.time).replaceFirstChar { it.uppercase() }

            val entrees = operationDao.sumEntrees(deviseCdfId, mois, annee).first()
            val sorties = operationDao.sumSorties(deviseCdfId, mois, annee).first()

            result.add(MoisData(label = label, entreesCdf = entrees, sortiesCdf = sorties))
        }
        return result
    }

    fun effacerErreur() {
        _uiState.update { it.copy(error = null) }
    }
}
