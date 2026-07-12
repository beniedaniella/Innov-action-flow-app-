package com.innovaction.finance.data.repository

import com.innovaction.finance.data.local.dao.AvanceDao
import com.innovaction.finance.data.local.entity.AvanceEntity
import com.innovaction.finance.data.local.relation.AvanceWithDetails
import com.innovaction.finance.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

interface AvanceRepository {
    fun getAll(): Flow<List<AvanceWithDetails>>
    fun getEnRetard(): Flow<List<AvanceWithDetails>>
    fun countEnRetard(): Flow<Int>
    fun getByStatut(statut: String): Flow<List<AvanceWithDetails>>
    suspend fun getById(id: Long): AvanceWithDetails?
    suspend fun save(avance: AvanceEntity): Result<Long>
    suspend fun update(avance: AvanceEntity): Result<Unit>
    suspend fun rembourser(id: Long, montant: Double, date: Long): Result<Unit>
    suspend fun delete(avance: AvanceEntity): Result<Unit>
}

@Singleton
class AvanceRepositoryImpl @Inject constructor(
    private val dao: AvanceDao
) : AvanceRepository {

    override fun getAll(): Flow<List<AvanceWithDetails>> = dao.getAllWithDetails()
    override fun getEnRetard(): Flow<List<AvanceWithDetails>> = dao.getEnRetard()
    override fun countEnRetard(): Flow<Int> = dao.countEnRetard()
    override fun getByStatut(statut: String): Flow<List<AvanceWithDetails>> = dao.getByStatut(statut)
    override suspend fun getById(id: Long): AvanceWithDetails? = dao.getByIdWithDetails(id)

    override suspend fun save(avance: AvanceEntity): Result<Long> = try {
        val num = dao.getProchainNumero()
        val a   = avance.copy(numero = "AVA-${"$num".padStart(3, '0')}")
        Result.Success(dao.insert(a))
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override suspend fun update(avance: AvanceEntity): Result<Unit> = try {
        dao.update(avance.copy(updatedAt = System.currentTimeMillis()))
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override suspend fun rembourser(id: Long, montant: Double, date: Long): Result<Unit> = try {
        val avance = dao.getByIdWithDetails(id)?.avance
            ?: return Result.Error("Avance introuvable")
        val total  = avance.montantRembourse + montant
        val statut = if (total >= avance.montant) "REMBOURSEE_TOTALE" else "REMBOURSEE_PARTIELLE"
        dao.updateRemboursement(
            id                = id,
            montantRembourse  = total,
            statut            = statut,
            dateRemboursement = if (statut == "REMBOURSEE_TOTALE") date else null,
        )
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }

    override suspend fun delete(avance: AvanceEntity): Result<Unit> = try {
        dao.delete(avance); Result.Success(Unit)
    } catch (e: Exception) { Result.Error(e.message ?: "Erreur", e) }
}
