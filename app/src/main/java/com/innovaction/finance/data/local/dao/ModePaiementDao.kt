package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.ModePaiementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModePaiementDao : BaseDao<ModePaiementEntity> {
    @Query("SELECT * FROM modes_paiement WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllActive(): Flow<List<ModePaiementEntity>>

    @Query("SELECT * FROM modes_paiement ORDER BY ordre ASC")
    fun getAll(): Flow<List<ModePaiementEntity>>

    @Query("SELECT * FROM modes_paiement WHERE id = :id")
    suspend fun getById(id: Long): ModePaiementEntity?

    @Query("UPDATE modes_paiement SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}
