package com.innovaction.finance.data.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.innovaction.finance.data.local.dao.AvanceDao
import com.innovaction.finance.data.local.dao.CompteDao
import com.innovaction.finance.data.local.dao.DeviseDao
import com.innovaction.finance.data.local.dao.OperationDao
import com.innovaction.finance.data.local.dao.ParametreDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Worker périodique — vérifie les alertes toutes les 6 heures.
 * Tous les seuils sont lus depuis la base de données (ParametreDao).
 * Aucune valeur n'est codée en dur.
 */
@HiltWorker
class AlerteWorker @AssistedInject constructor(
    @Assisted context          : Context,
    @Assisted workerParams     : WorkerParameters,
    private val avanceDao      : AvanceDao,
    private val compteDao      : CompteDao,
    private val operationDao   : OperationDao,
    private val deviseDao      : DeviseDao,
    private val parametreDao   : ParametreDao,
    private val notifications  : NotificationHelper,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Lire les seuils depuis la DB (configurables par l'utilisateur)
            val seuilCdf = parametreDao.getValeur("seuil_alerte_cdf")?.toDoubleOrNull() ?: 500_000.0
            val seuilUsd = parametreDao.getValeur("seuil_alerte_usd")?.toDoubleOrNull() ?: 150.0

            val devCdf = deviseDao.getByCode("CDF")
            val devUsd = deviseDao.getByCode("USD")

            // 1. Avances en retard
            val avancesRetard = avanceDao.getEnRetard().first()
            if (avancesRetard.isNotEmpty()) {
                val montantTotal = avancesRetard.sumOf {
                    it.avance.montant - it.avance.montantRembourse
                }
                notifications.notifierAvancesEnRetard(
                    avancesRetard.size, montantTotal,
                    devCdf?.symbole ?: "FC"
                )
            }

            // 2. Solde CDF bas
            if (devCdf != null) {
                val entCdf = operationDao.sumEntrees(devCdf.id).first()
                val sorCdf = operationDao.sumSorties(devCdf.id).first()
                val soldeCdf = entCdf - sorCdf
                if (soldeCdf < seuilCdf) {
                    notifications.notifierSoldeBasCdf(soldeCdf, seuilCdf)
                }
            }

            // 3. Solde USD bas
            if (devUsd != null) {
                val entUsd = operationDao.sumEntrees(devUsd.id).first()
                val sorUsd = operationDao.sumSorties(devUsd.id).first()
                val soldeUsd = entUsd - sorUsd
                if (soldeUsd < seuilUsd) {
                    notifications.notifierSoldeBasUsd(soldeUsd, seuilUsd)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "innovaction_alertes_periodiques"

        fun planifier(context: Context) {
            val request = PeriodicWorkRequestBuilder<AlerteWorker>(6, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
