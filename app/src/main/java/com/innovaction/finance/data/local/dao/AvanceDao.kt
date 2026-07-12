package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.AvanceEntity
import com.innovaction.finance.data.local.relation.AvanceWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface AvanceDao : BaseDao<AvanceEntity> {

    @Transaction
    @Query("SELECT * FROM avances ORDER BY dateEcheance ASC, id DESC")
    fun getAllWithDetails(): Flow<List<AvanceWithDetails>>

    @Transaction
    @Query("SELECT * FROM avances WHERE statut = :statut ORDER BY dateEcheance ASC")
    fun getByStatut(statut: String): Flow<List<AvanceWithDetails>>

    /** Avances actives dont la date d'échéance est dépassée. */
    @Transaction
    @Query("""
        SELECT * FROM avances
        WHERE statut = 'ACTIVE' AND dateEcheance < :maintenant
        ORDER BY dateEcheance ASC
    """)
    fun getEnRetard(maintenant: Long = System.currentTimeMillis()): Flow<List<AvanceWithDetails>>

    @Query("SELECT COUNT(*) FROM avances WHERE statut = 'ACTIVE' AND dateEcheance < :maintenant")
    fun countEnRetard(maintenant: Long = System.currentTimeMillis()): Flow<Int>

    @Transaction
    @Query("SELECT * FROM avances WHERE id = :id")
    suspend fun getByIdWithDetails(id: Long): AvanceWithDetails?

    @Query("""
        UPDATE avances
        SET montantRembourse = :montantRembourse,
            statut           = :statut,
            dateRemboursement = :dateRemboursement,
            updatedAt        = :now
        WHERE id = :id
    """)
    suspend fun updateRemboursement(
        id                : Long,
        montantRembourse  : Double,
        statut            : String,
        dateRemboursement : Long?,
        now               : Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) + 1 FROM avances")
    suspend fun getProchainNumero(): Int
}
