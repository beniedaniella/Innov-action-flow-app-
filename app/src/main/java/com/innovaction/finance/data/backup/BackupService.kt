package com.innovaction.finance.data.backup

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class BackupResult {
    data class Success(val uri: Uri, val fileName: String) : BackupResult()
    data class Error(val message: String, val cause: Throwable? = null) : BackupResult()
}

/**
 * Service de sauvegarde complète de la base de données en JSON.
 * Le fichier généré contient toutes les données et peut être restauré
 * sur n'importe quel appareil avec la même application.
 */
@Singleton
class BackupService @Inject constructor(
    private val operationDao  : OperationDao,
    private val avanceDao     : AvanceDao,
    private val projetDao     : ProjetDao,
    private val compteDao     : CompteDao,
    private val categorieDao  : CategorieDao,
    private val modeDao       : ModePaiementDao,
    private val federationDao : FederationDao,
    private val deviseDao     : DeviseDao,
    private val parametreDao  : ParametreDao,
    private val tauxDao       : TauxChangeDao,
) {
    suspend fun sauvegarder(context: Context): BackupResult = withContext(Dispatchers.IO) {
        try {
            val fmt      = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
            val fileName = "INNOVACTION_backup_${fmt.format(Date())}.json"
            val file     = File(context.cacheDir, fileName)

            val root = JSONObject().apply {
                put("version",    1)
                put("created_at", System.currentTimeMillis())
                put("app",        "INNOVACTION Finance")

                // Toutes les tables
                put("devises",       exportList(deviseDao.getAll().first()) { it.toJson() })
                put("taux_change",   exportList(listOf<TauxChangeEntity>()) { it.toJson() })
                put("modes",         exportList(modeDao.getAllActive().first()) { it.toJson() })
                put("categories",    exportList(categorieDao.getAllActive().first()) { it.toJson() })
                put("federations",   exportList(federationDao.getAllActive().first()) { it.toJson() })
                put("projets",       exportList(projetDao.getAllActive().first()) { it.toJson() })
                put("comptes",       exportList(compteDao.getAllActive().first()) { it.toJson() })
                put("parametres",    exportList(parametreDao.getAll().first()) { it.toJson() })
                put("operations",    exportList(
                    operationDao.getPagedWithDetails(10000, 0).first()
                        .map { it.operation }) { it.toJson() })
                put("avances",       exportList(
                    avanceDao.getAllWithDetails().first()
                        .map { it.avance }) { it.toJson() })
            }

            FileWriter(file, Charsets.UTF_8).use { it.write(root.toString(2)) }

            val uri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", file)
            BackupResult.Success(uri, fileName)
        } catch (e: Exception) {
            BackupResult.Error("Erreur de sauvegarde : ${e.message}", e)
        }
    }

    private fun <T> exportList(items: List<T>, toJson: T.() -> JSONObject): JSONArray {
        val arr = JSONArray()
        items.forEach { arr.put(it.toJson()) }
        return arr
    }
}

// Extensions JSON pour chaque entité
private fun DeviseEntity.toJson() = JSONObject().apply {
    put("id", id); put("code", code); put("nom", nom)
    put("symbole", symbole); put("isActive", isActive); put("ordre", ordre)
}
private fun TauxChangeEntity.toJson() = JSONObject().apply {
    put("id", id); put("deviseSourceId", deviseSourceId)
    put("deviseCibleId", deviseCibleId); put("taux", taux); put("dateEffet", dateEffet)
}
private fun ModePaiementEntity.toJson() = JSONObject().apply {
    put("id", id); put("nom", nom); put("icone", icone)
    put("isActive", isActive); put("ordre", ordre)
}
private fun CategorieEntity.toJson() = JSONObject().apply {
    put("id", id); put("nom", nom); put("typeDefaut", typeDefaut)
    put("icone", icone); put("couleur", couleur)
    put("isActive", isActive); put("ordre", ordre)
}
private fun FederationEntity.toJson() = JSONObject().apply {
    put("id", id); put("nom", nom); put("description", description)
    put("contact", contact); put("isActive", isActive); put("ordre", ordre)
}
private fun ProjetEntity.toJson() = JSONObject().apply {
    put("id", id); put("nom", nom); put("description", description)
    put("budgetCdf", budgetCdf); put("budgetUsd", budgetUsd)
    put("isActive", isActive); put("couleur", couleur); put("ordre", ordre)
}
private fun CompteEntity.toJson() = JSONObject().apply {
    put("id", id); put("nom", nom); put("deviseId", deviseId)
    put("soldeInitial", soldeInitial); put("icone", icone)
    put("couleur", couleur); put("isActive", isActive); put("ordre", ordre)
}
private fun ParametreEntity.toJson() = JSONObject().apply {
    put("cle", cle); put("valeur", valeur)
    put("description", description); put("categorie", categorie)
}
private fun OperationEntity.toJson() = JSONObject().apply {
    put("id", id); put("numero", numero); put("date", date)
    put("libelle", libelle); put("type", type); put("montant", montant)
    put("compteId", compteId); put("compteDestId", compteDestId ?: JSONObject.NULL)
    put("projetId", projetId ?: JSONObject.NULL)
    put("categorieId", categorieId); put("modePaiementId", modePaiementId)
    put("deviseId", deviseId); put("federationId", federationId ?: JSONObject.NULL)
    put("numeroPiece", numeroPiece); put("remarques", remarques)
    put("createdAt", createdAt); put("updatedAt", updatedAt)
}
private fun AvanceEntity.toJson() = JSONObject().apply {
    put("id", id); put("numero", numero); put("beneficiaire", beneficiaire)
    put("objet", objet); put("montant", montant)
    put("montantRembourse", montantRembourse); put("deviseId", deviseId)
    put("projetId", projetId ?: JSONObject.NULL)
    put("dateEmission", dateEmission); put("dateEcheance", dateEcheance)
    put("dateRemboursement", dateRemboursement ?: JSONObject.NULL)
    put("statut", statut); put("numeroDecharge", numeroDecharge)
    put("remarques", remarques)
}
