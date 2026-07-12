package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.FederationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FederationDao : BaseDao<FederationEntity> {
    @Query("SELECT * FROM federations WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllActive(): Flow<List<FederationEntity>>

    @Query("SELECT * FROM federations ORDER BY ordre ASC")
    fun getAll(): Flow<List<FederationEntity>>

    @Query("SELECT * FROM federations WHERE id = :id")
    suspend fun getById(id: Long): FederationEntity?
}
