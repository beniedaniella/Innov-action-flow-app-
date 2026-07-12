package com.innovaction.finance.presentation.rapports

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovaction.finance.data.export.*
import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.relation.OperationWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RapportsViewModel @Inject constructor(
    @ApplicationContext private val context : Context,
    private val operationDao : OperationDao,
    private val avanceDao    : AvanceDao,
    private val projetDao    : ProjetDao,
    private val deviseDao    : DeviseDao,
    private val modePaiementDao: ModePaiementDao,
    private val parametreDao : ParametreDao,
    private val csvExport    : CsvExportService,
    private val pdfExport    : PdfExportService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RapportsUiState())
    val uiState: StateFlow<RapportsUiState> = _uiState.asStateFlow()

    init { calculer() }

    // ── Navigation ────────────────────────────────────────────────────────
    fun setType(t: TypeRapport) { _uiState.update { it.copy(typeActif = t) }; calculer() }
    fun setMois(m: Int)   { _uiState.update { it.copy(moisSelectionne   = m) }; calculer() }
    fun setAnnee(a: Int)  { _uiState.update { it.copy(anneeSelectionnee = a) }; calculer() }

    fun moisSuivant() {
        val s = _uiState.value
        if (s.moisSelectionne == 12) setMois(1).also { setAnnee(s.anneeSelectionnee + 1) }
        else setMois(s.moisSelectionne + 1)
    }
    fun moisPrecedent() {
        val s = _uiState.value
        if (s.moisSelectionne == 1) setMois(12).also { setAnnee(s.anneeSelectionnee - 1) }
        else setMois(s.moisSelectionne - 1)
    }

    // ── Calcul des données ─────────────────────────────────────────────────
    private fun calculer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val s   = _uiState.value
            val cdf = deviseDao.getByCode("CDF")
            val usd = deviseDao.getByCode("USD")

            when (s.typeActif) {
                TypeRapport.MENSUEL -> calculerMensuel(s.moisSelectionne, s.anneeSelectionnee, cdf?.id, usd?.id)
                TypeRapport.ANNUEL  -> calculerAnnuel(s.anneeSelectionnee, cdf?.id, usd?.id)
                TypeRapport.PAR_PROJET -> calculerParProjet(cdf?.id)
            }
        }
    }

    private suspend fun calculerMensuel(mois: Int, annee: Int, cdfId: Long?, usdId: Long?) {
        val fmtMois = SimpleDateFormat("MMMM", Locale.FRENCH)
        val cal     = Calendar.getInstance().also { it.set(Calendar.MONTH, mois - 1) }
        val nomMois = fmtMois.format(cal.time).replaceFirstChar { it.uppercase() }

        val entCdf = if (cdfId != null) operationDao.sumEntrees(cdfId, mois, annee.toString()).first() else 0.0
        val sorCdf = if (cdfId != null) operationDao.sumSorties(cdfId, mois, annee.toString()).first() else 0.0
        val entUsd = if (usdId != null) operationDao.sumEntrees(usdId, mois, annee.toString()).first() else 0.0
        val sorUsd = if (usdId != null) operationDao.sumSorties(usdId, mois, annee.toString()).first() else 0.0

        // Répartition par projet
        val projets = projetDao.getAllActive().first()
        val repProjet = projets.mapNotNull { p ->
            val dep = if (cdfId != null)
                operationDao.sumSortiesParProjet(p.id, cdfId).first() else 0.0
            if (dep > 0) Pair(p.nom, dep) else null
        }.sortedByDescending { it.second }

        // Répartition par mode
        val modes = modePaiementDao.getAllActive().first()
        val repMode = modes.mapNotNull { m ->
            val ops = operationDao.searchWithDetails(modePaiementId = m.id, limit = 1000).first()
            val total = ops.filter {
                val parts = it.operation.date.toString()
                it.operation.type == "ENTREE"
            }.sumOf { it.operation.montant }
            if (total > 0) Pair(m.nom, total) else null
        }

        val nbOps = operationDao.searchWithDetails(
            dateDebut = calDebut(mois, annee),
            dateFin   = calFin(mois, annee),
            limit     = 10000
        ).first().size

        _uiState.update { it.copy(
            isLoading       = false,
            donneesMois     = DonneesMois(mois, annee, nomMois, entCdf, sorCdf, entUsd, sorUsd, nbOps),
            repartitionProjet = repProjet,
            repartitionMode   = repMode,
        )}
    }

    private suspend fun calculerAnnuel(annee: Int, cdfId: Long?, usdId: Long?) {
        val fmtMois = SimpleDateFormat("MMM", Locale.FRENCH)
        val donnees = (1..12).map { m ->
            val cal = Calendar.getInstance().also { it.set(Calendar.MONTH, m - 1) }
            val nomMois = fmtMois.format(cal.time).replaceFirstChar { it.uppercase() }
            val eC = if (cdfId != null) operationDao.sumEntrees(cdfId, m, annee.toString()).first() else 0.0
            val sC = if (cdfId != null) operationDao.sumSorties(cdfId, m, annee.toString()).first() else 0.0
            val eU = if (usdId != null) operationDao.sumEntrees(usdId, m, annee.toString()).first() else 0.0
            val sU = if (usdId != null) operationDao.sumSorties(usdId, m, annee.toString()).first() else 0.0
            DonneesMois(m, annee, nomMois, eC, sC, eU, sU)
        }
        _uiState.update { it.copy(
            isLoading         = false,
            donnees12Mois     = donnees,
            totalAnneeEntCdf  = donnees.sumOf { it.entreesCdf },
            totalAnneeSorCdf  = donnees.sumOf { it.sortiesCdf },
        )}
    }

    private suspend fun calculerParProjet(cdfId: Long?) {
        val projets = projetDao.getAllActive().first()
        val rep = projets.mapNotNull { p ->
            val dep = if (cdfId != null) operationDao.sumSortiesParProjet(p.id, cdfId).first() else 0.0
            if (dep > 0 || p.budgetCdf > 0) Pair(p.nom, dep) else null
        }
        _uiState.update { it.copy(isLoading = false, repartitionProjet = rep) }
    }

    // ── Exports ───────────────────────────────────────────────────────────
    fun exporterCsv() {
        viewModelScope.launch {
            _uiState.update { it.copy(exportEnCours = true) }
            val donnees = construireDonneesExport()
            val s       = _uiState.value
            val result  = when (s.typeActif) {
                TypeRapport.MENSUEL  -> csvExport.exporterRapportMensuel(
                    context, donnees, s.moisSelectionne, s.anneeSelectionnee)
                TypeRapport.ANNUEL   -> csvExport.exporterRapportAnnuel(
                    context, donnees, s.anneeSelectionnee)
                TypeRapport.PAR_PROJET -> csvExport.exporterJournal(context, donnees)
            }
            traiterResultatExport(result)
        }
    }

    fun exporterPdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(exportEnCours = true) }
            val donnees = construireDonneesExport()
            val s       = _uiState.value
            val result  = pdfExport.exporterRapportMensuel(
                context, donnees, s.moisSelectionne, s.anneeSelectionnee)
            traiterResultatExport(result)
        }
    }

    private fun traiterResultatExport(result: ExportResult) {
        when (result) {
            is ExportResult.Success -> {
                partagerFichier(result.uri, result.fileName)
                _uiState.update { it.copy(exportEnCours = false, exportSucces = result.fileName) }
            }
            is ExportResult.Error   -> _uiState.update {
                it.copy(exportEnCours = false, exportErreur = result.message)
            }
        }
    }

    private fun partagerFichier(uri: Uri, fileName: String) {
        val mimeType = if (fileName.endsWith(".pdf")) "application/pdf"
                       else "text/csv"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type    = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Partager $fileName")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private suspend fun construireDonneesExport(): DonneesExport {
        val fmtDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val nomAsso = parametreDao.getValeur("nom_association") ?: "INNOV'ACTION"
        val exercice = parametreDao.getValeur("exercice_en_cours") ?: "2026"

        val ops = operationDao.getPagedWithDetails(10000, 0).first()
            .map { o -> OperationExport(
                numero       = o.operation.numero,
                date         = fmtDate.format(Date(o.operation.date)),
                libelle      = o.operation.libelle,
                type         = o.operation.type,
                categorie    = o.categorie.nom,
                montant      = o.operation.montant,
                devise       = o.devise.code,
                compte       = o.compte.nom,
                modePaiement = o.modePaiement.nom,
                projet       = o.projet?.nom ?: "",
                federation   = o.federation?.nom ?: "",
                numeroPiece  = o.operation.numeroPiece,
                remarques    = o.operation.remarques,
            )}

        val avances = avanceDao.getAllWithDetails().first()
            .map { a -> AvanceExport(
                numero          = a.avance.numero,
                beneficiaire    = a.avance.beneficiaire,
                objet           = a.avance.objet,
                montant         = a.avance.montant,
                devise          = a.devise.code,
                dateEmission    = fmtDate.format(Date(a.avance.dateEmission)),
                dateEcheance    = fmtDate.format(Date(a.avance.dateEcheance)),
                statut          = a.avance.statut,
                montantRembourse= a.avance.montantRembourse,
                projet          = a.projet?.nom ?: "",
                numeroDecharge  = a.avance.numeroDecharge,
            )}

        val cdfId   = deviseDao.getByCode("CDF")?.id ?: 1L
        val projets = projetDao.getAllActive().first()
            .map { p -> ProjetExport(
                nom       = p.nom,
                budgetCdf = p.budgetCdf,
                budgetUsd = p.budgetUsd,
                entreesCdf = operationDao.sumEntreesParProjet(p.id, cdfId).first(),
                sortiesCdf = operationDao.sumSortiesParProjet(p.id, cdfId).first(),
                soldeCdf   = operationDao.sumEntreesParProjet(p.id, cdfId).first() -
                             operationDao.sumSortiesParProjet(p.id, cdfId).first(),
            )}

        return DonneesExport(nomAsso, exercice, ops, avances, projets, emptyList())
    }

    private fun calDebut(mois: Int, annee: Int): Long {
        return Calendar.getInstance().also {
            it.set(annee, mois - 1, 1, 0, 0, 0); it.set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    private fun calFin(mois: Int, annee: Int): Long {
        return Calendar.getInstance().also {
            it.set(annee, mois - 1, 1, 23, 59, 59)
            it.set(Calendar.DAY_OF_MONTH, it.getActualMaximum(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

    fun effacerMessages() { _uiState.update { it.copy(exportSucces = null, exportErreur = null) } }
}
