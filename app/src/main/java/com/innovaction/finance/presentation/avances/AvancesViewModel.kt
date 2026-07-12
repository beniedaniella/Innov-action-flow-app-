package com.innovaction.finance.presentation.avances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.local.dao.CompteDao
import com.innovaction.finance.data.local.dao.DeviseDao
import com.innovaction.finance.data.local.dao.ProjetDao
import com.innovaction.finance.data.local.entity.AvanceEntity
import com.innovaction.finance.data.local.relation.AvanceWithDetails
import com.innovaction.finance.data.repository.AvanceRepository
import com.innovaction.finance.util.AppConstants
import com.innovaction.finance.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AvancesViewModel @Inject constructor(
    private val avanceRepo : AvanceRepository,
    private val deviseDao  : DeviseDao,
    private val projetDao  : ProjetDao,
    private val compteDao  : CompteDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvancesUiState())
    val uiState: StateFlow<AvancesUiState> = _uiState.asStateFlow()

    private val fmtDate = SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault())

    init { charger() }

    private fun charger() {
        viewModelScope.launch {
            // Référentiels
            combine(
                deviseDao.getAllActive(),
                projetDao.getAllActive(),
                compteDao.getAllActive(),
            ) { devises, projets, comptes ->
                _uiState.update { it.copy(devises = devises, projets = projets, comptes = comptes) }
            }.launchIn(this)

            // Avances — total
            avanceRepo.getAll()
                .onEach { avances ->
                    val retard = avances.filter {
                        it.avance.statut == "ACTIVE" &&
                        it.avance.dateEcheance < System.currentTimeMillis()
                    }
                    _uiState.update { s -> s.copy(
                        isLoading          = false,
                        toutes             = avances,
                        enRetard           = retard,
                        nbEnRetard         = retard.size,
                        montantTotalRetard = retard.sumOf { it.avance.montant - it.avance.montantRembourse },
                    )}
                }.launchIn(this)
        }
    }

    // ── Filtre ────────────────────────────────────────────────────────────
    fun setFiltre(f: FiltreAvance) { _uiState.update { it.copy(filtre = f) } }

    // ── Détail ─────────────────────────────────────────────────────────────
    fun voirDetail(av: AvanceWithDetails) {
        _uiState.update { it.copy(avanceSelectionnee = av, showDetail = true) }
    }
    fun fermerDetail() { _uiState.update { it.copy(showDetail = false, avanceSelectionnee = null) } }

    // ── Formulaire nouvelle avance / édition ──────────────────────────────
    fun ouvrirFormulaire(av: AvanceWithDetails? = null) {
        val now = Date()
        val e = av?.avance
        val premiereDevise = _uiState.value.devises.firstOrNull()
        _uiState.update { s -> s.copy(
            showFormulaire = true,
            formulaire     = FormulaireAvance(
                id              = e?.id,
                beneficiaire    = e?.beneficiaire ?: "",
                objet           = e?.objet ?: "",
                montant         = if (e != null) e.montant.toString() else "",
                deviseId        = e?.deviseId ?: premiereDevise?.id,
                projetId        = e?.projetId,
                dateEmission    = e?.dateEmission ?: now.time,
                dateEmissionAff = fmtDate.format(if (e != null) Date(e.dateEmission) else now),
                dateEcheance    = e?.dateEcheance,
                dateEcheanceAff = if (e?.dateEcheance != null) fmtDate.format(Date(e.dateEcheance)) else "",
                numeroDecharge  = e?.numeroDecharge ?: "",
                remarques       = e?.remarques ?: "",
            ),
        )}
    }
    fun fermerFormulaire() { _uiState.update { it.copy(showFormulaire = false) } }

    // Mutations formulaire
    fun onBenefChange(v: String)   { upF { copy(beneficiaire = v, erreurBenef = null) } }
    fun onObjetChange(v: String)   { upF { copy(objet = v, erreurObjet = null) } }
    fun onMontantChange(v: String) { upF { copy(montant = v, erreurMontant = null) } }
    fun onDeviseChange(v: Long)    { upF { copy(deviseId = v, erreurDevise = null) } }
    fun onProjetChange(v: Long?)   { upF { copy(projetId = v) } }
    fun onDechargeChange(v: String){ upF { copy(numeroDecharge = v) } }
    fun onRemarquesChange(v: String){ upF { copy(remarques = v) } }
    fun onDateEmissionChange(ts: Long) {
        upF { copy(dateEmission = ts, dateEmissionAff = fmtDate.format(Date(ts))) }
    }
    fun onDateEcheanceChange(ts: Long) {
        upF { copy(dateEcheance = ts, dateEcheanceAff = fmtDate.format(Date(ts)), erreurEcheance = null) }
    }

    private fun upF(block: FormulaireAvance.() -> FormulaireAvance) {
        _uiState.update { it.copy(formulaire = it.formulaire.block()) }
    }

    fun sauvegarder() {
        val f = _uiState.value.formulaire
        if (!validerFormulaire()) return
        viewModelScope.launch {
            upF { copy(isSaving = true) }
            val entity = AvanceEntity(
                id             = f.id ?: 0L,
                beneficiaire   = f.beneficiaire.trim(),
                objet          = f.objet.trim(),
                montant        = f.montant.toDouble(),
                deviseId       = f.deviseId!!,
                projetId       = f.projetId,
                dateEmission   = f.dateEmission,
                dateEcheance   = f.dateEcheance!!,
                numeroDecharge = f.numeroDecharge.trim(),
                remarques      = f.remarques.trim(),
            )
            when (val r = if (f.id == null) avanceRepo.save(entity)
                          else              avanceRepo.update(entity)) {
                is Result.Success -> _uiState.update {
                    it.copy(showFormulaire = false,
                        formulaire = it.formulaire.copy(isSaving = false))
                }
                is Result.Error   -> _uiState.update {
                    it.copy(error = r.message,
                        formulaire = it.formulaire.copy(isSaving = false))
                }
                else -> {}
            }
        }
    }

    private fun validerFormulaire(): Boolean {
        val f = _uiState.value.formulaire
        var ok = true
        upF { copy(
            erreurBenef   = if (beneficiaire.isBlank())            { ok=false; "Bénéficiaire obligatoire" } else null,
            erreurObjet   = if (objet.isBlank())                   { ok=false; "Objet obligatoire" }        else null,
            erreurMontant = if (montant.toDoubleOrNull() == null || montant.toDouble() <= 0)
                                                                   { ok=false; "Montant invalide" }         else null,
            erreurDevise  = if (deviseId == null)                  { ok=false; "Devise obligatoire" }       else null,
            erreurEcheance= if (dateEcheance == null)              { ok=false; "Date limite obligatoire" }  else null,
        )}
        return ok
    }

    // ── Remboursement ──────────────────────────────────────────────────────
    fun ouvrirRemboursement(av: AvanceWithDetails) {
        val devise = _uiState.value.devises.find { it.id == av.avance.deviseId }
        val restant = av.avance.montant - av.avance.montantRembourse
        val now = Date()
        _uiState.update { it.copy(
            showRemboursement = true,
            frmRembours = FormulaireRemboursement(
                avanceId          = av.avance.id,
                montantRestant    = restant,
                montant           = "",
                dateRemboursement = now.time,
                dateAff           = fmtDate.format(now),
                deviseSymbole     = devise?.symbole ?: "FC",
            ),
        )}
    }
    fun fermerRemboursement() { _uiState.update { it.copy(showRemboursement = false) } }

    fun onMontantRemboursChange(v: String) {
        _uiState.update { it.copy(frmRembours = it.frmRembours.copy(
            montant = v, erreurMontant = null
        ))}
    }
    fun onDateRemboursChange(ts: Long) {
        _uiState.update { it.copy(frmRembours = it.frmRembours.copy(
            dateRemboursement = ts, dateAff = fmtDate.format(Date(ts))
        ))}
    }
    fun rembourserTout() {
        val r = _uiState.value.frmRembours
        _uiState.update { it.copy(frmRembours = it.frmRembours.copy(
            montant = r.montantRestant.toString()
        ))}
    }

    fun confirmerRemboursement() {
        val fr = _uiState.value.frmRembours
        val montant = fr.montant.toDoubleOrNull()
        if (montant == null || montant <= 0 || montant > fr.montantRestant) {
            _uiState.update { it.copy(frmRembours = it.frmRembours.copy(
                erreurMontant = if (montant != null && montant > fr.montantRestant)
                    "Montant supérieur au solde restant (${fr.montantRestant})"
                else "Montant invalide"
            ))}
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(frmRembours = it.frmRembours.copy(isSaving = true)) }
            when (val r = avanceRepo.rembourser(fr.avanceId, montant, fr.dateRemboursement)) {
                is Result.Success -> _uiState.update {
                    it.copy(showRemboursement = false, showDetail = false,
                        frmRembours = it.frmRembours.copy(isSaving = false))
                }
                is Result.Error   -> _uiState.update {
                    it.copy(error = r.message,
                        frmRembours = it.frmRembours.copy(isSaving = false))
                }
                else -> {}
            }
        }
    }

    // ── Suppression ────────────────────────────────────────────────────────
    fun demanderSuppression(av: AvanceWithDetails) {
        _uiState.update { it.copy(showConfirmDelete = av) }
    }
    fun annulerSuppression() { _uiState.update { it.copy(showConfirmDelete = null) } }
    fun confirmerSuppression() {
        val av = _uiState.value.showConfirmDelete ?: return
        viewModelScope.launch {
            avanceRepo.delete(av.avance)
            _uiState.update { it.copy(showConfirmDelete = null, showDetail = false) }
        }
    }

    fun effacerErreur() { _uiState.update { it.copy(error = null) } }
}
