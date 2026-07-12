package com.innovaction.finance.data.repository

import com.innovaction.finance.data.local.dao.OperationDao
import com.innovaction.finance.data.local.entity.OperationEntity
import com.innovaction.finance.data.local.relation.OperationWithDetails
import com.innovaction.finance.util.Result
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface OperationRepository {
    fun getRecents(n: Int = 5): Flow<List<OperationWithDetails>>
    fun search(
        type         : String? = null,
        compteId     : Long?   = null,
        projetId     : Long?   = null,
        categorieId  : Long?   = null,
        deviseId     : Long?   = null,
        federationId : Long?   = null,
        dateDebut    : Long?   = null,
        dateFin      : Long?   = null,
        recherche    : String? = null,
        limit        : Int     = 30,
        offset       : Int     = 0,
    ): Flow<List<OperationWithDetails>>
    fun countAll(): Flow<Int>
    fun sumEntrees(deviseId: Long, mois: Int? = null, annee: String? = null): Flow<Double>
    fun sumSorties(deviseId: Long, mois: Int? = null, annee: String? = null): Flow<Double>
    fun sumEntreesParProjet(projetId: Long, deviseId: Long): Flow<Double>
    fun sumSortiesParProjet(projetId: Long, deviseId: Long): Flow<Double>
    suspend fun getById(id: Long): OperationWithDetails?
    suspend fun save(operation: OperationEntity): Result<Long>
    suspend fun update(operation: OperationEntity): Result<Unit>
    suspend fun delete(operation: OperationEntity): Result<Unit>
}

@Singleton
class OperationRepositoryImpl @Inject constructor(
    private val dao: OperationDao
) : OperationRepository {

    override fun getRecents(n: Int): Flow<List<OperationWithDetails>> = dao.getRecents(n)

    override fun search(
        type: String?, compteId: Long?, projetId: Long?, categorieId: Long?,
        deviseId: Long?, federationId: Long?, dateDebut: Long?, dateFin: Long?,
        recherche: String?, limit: Int, offset: Int
    ): Flow<List<OperationWithDetails>> = dao.searchWithDetails(
        type, compteId, projetId, categorieId, deviseId,
        federationId, dateDebut, dateFin, recherche, limit, offset
    )

    override fun countAll(): Flow<Int> = dao.countAll()

    override fun sumEntrees(deviseId: Long, mois: Int?, annee: String?): Flow<Double> =
        dao.sumEntrees(deviseId, mois, annee)

    override fun sumSorties(deviseId: Long, mois: Int?, annee: String?): Flow<Double> =
        dao.sumSorties(deviseId, mois, annee)

    override fun sumEntreesParProjet(projetId: Long, deviseId: Long): Flow<Double> =
        dao.sumEntreesParProjet(projetId, deviseId)

    override fun sumSortiesParProjet(projetId: Long, deviseId: Long): Flow<Double> =
        dao.sumSortiesParProjet(projetId, deviseId)

    override suspend fun getById(id: Long): OperationWithDetails? =
        dao.getByIdWithDetails(id)

    override suspend fun save(operation: OperationEntity): Result<Long> = try {
        val annee = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(operation.date))
        val num   = dao.getProchainNumero(annee)
        val op    = operation.copy(numero = "$annee-${"$num".padStart(4, '0')}")
        val id    = dao.insert(op)
        Result.Success(id)
    } catch (e: Exception) {
        Result.Error("Erreur lors de l'enregistrement : ${e.message}", e)
    }

    override suspend fun update(operation: OperationEntity): Result<Unit> = try {
        dao.update(operation.copy(updatedAt = System.currentTimeMillis()))
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error("Erreur lors de la modification : ${e.message}", e)
    }

    override suspend fun delete(operation: OperationEntity): Result<Unit> = try {
        dao.delete(operation)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error("Impossible de supprimer : ${e.message}", e)
    }
}
