package com.innovaction.finance.data.repository

import com.innovaction.finance.data.local.dao.*
import com.innovaction.finance.data.local.entity.*
import com.innovaction.finance.data.local.relation.CompteWithDevise
import com.innovaction.finance.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ConfigRepository {
    fun getDevises(): Flow<List<DeviseEntity>>
    suspend fun saveTauxChange(taux: TauxChangeEntity): Result<Unit>
    suspend fun getTauxChange(sourceId: Long, cibleId: Long): Double?
    fun getModesPaiement(): Flow<List<ModePaiementEntity>>
    suspend fun saveModePaiement(mode: ModePaiementEntity): Result<Unit>
    fun getCategories(): Flow<List<CategorieEntity>>
    fun getCategoriesByType(type: String): Flow<List<CategorieEntity>>
    suspend fun saveCategorie(cat: CategorieEntity): Result<Unit>
    fun getFederations(): Flow<List<FederationEntity>>
    suspend fun saveFederation(fed: FederationEntity): Result<Unit>
    fun getProjets(): Flow<List<ProjetEntity>>
    suspend fun saveProjet(projet: ProjetEntity): Result<Unit>
    suspend fun updateBudget(id: Long, budgetCdf: Double, budgetUsd: Double): Result<Unit>
    fun getComptes(): Flow<List<CompteWithDevise>>
    suspend fun saveCompte(compte: CompteEntity): Result<Unit>
    fun getParametres(): Flow<List<ParametreEntity>>
    suspend fun getParametre(cle: String): String?
    fun getParametreFlow(cle: String): Flow<String?>
    suspend fun setParametre(cle: String, valeur: String): Result<Unit>
}

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val deviseDao     : DeviseDao,
    private val tauxDao       : TauxChangeDao,
    private val modeDao       : ModePaiementDao,
    private val categorieDao  : CategorieDao,
    private val federationDao : FederationDao,
    private val projetDao     : ProjetDao,
    private val compteDao     : CompteDao,
    private val parametreDao  : ParametreDao,
) : ConfigRepository {

    override fun getDevises(): Flow<List<DeviseEntity>> = deviseDao.getAllActive()

    override suspend fun getTauxChange(sourceId: Long, cibleId: Long): Double? =
        tauxDao.getDernier(sourceId, cibleId)?.taux

    override suspend fun saveTauxChange(taux: TauxChangeEntity): Result<Unit> = try {
        tauxDao.insert(taux); Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override fun getModesPaiement(): Flow<List<ModePaiementEntity>> = modeDao.getAllActive()

    override suspend fun saveModePaiement(mode: ModePaiementEntity): Result<Unit> = try {
        if (mode.id == 0L) modeDao.insert(mode) else modeDao.update(mode)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override fun getCategories(): Flow<List<CategorieEntity>> = categorieDao.getAllActive()

    override fun getCategoriesByType(type: String): Flow<List<CategorieEntity>> =
        categorieDao.getByType(type)

    override suspend fun saveCategorie(cat: CategorieEntity): Result<Unit> = try {
        if (cat.id == 0L) categorieDao.insert(cat) else categorieDao.update(cat)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override fun getFederations(): Flow<List<FederationEntity>> = federationDao.getAllActive()

    override suspend fun saveFederation(fed: FederationEntity): Result<Unit> = try {
        if (fed.id == 0L) federationDao.insert(fed) else federationDao.update(fed)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override fun getProjets(): Flow<List<ProjetEntity>> = projetDao.getAllActive()

    override suspend fun saveProjet(projet: ProjetEntity): Result<Unit> = try {
        if (projet.id == 0L) projetDao.insert(projet) else projetDao.update(projet)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override suspend fun updateBudget(id: Long, budgetCdf: Double, budgetUsd: Double): Result<Unit> = try {
        projetDao.updateBudget(id, budgetCdf, budgetUsd); Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override fun getComptes(): Flow<List<CompteWithDevise>> = compteDao.getAllWithDevise()

    override suspend fun saveCompte(compte: CompteEntity): Result<Unit> = try {
        if (compte.id == 0L) compteDao.insert(compte) else compteDao.update(compte)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override fun getParametres(): Flow<List<ParametreEntity>> = parametreDao.getAll()

    override suspend fun getParametre(cle: String): String? = parametreDao.getValeur(cle)

    override fun getParametreFlow(cle: String): Flow<String?> = parametreDao.getValeurFlow(cle)

    override suspend fun setParametre(cle: String, valeur: String): Result<Unit> = try {
        parametreDao.upsert(ParametreEntity(cle = cle, valeur = valeur))
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }
}
