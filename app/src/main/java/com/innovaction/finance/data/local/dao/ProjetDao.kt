package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.ProjetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjetDao : BaseDao<ProjetEntity> {
    @Query("SELECT * FROM projets WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllActive(): Flow<List<ProjetEntity>>

    @Query("SELECT * FROM projets ORDER BY ordre ASC")
    fun getAll(): Flow<List<ProjetEntity>>

    @Query("SELECT * FROM projets WHERE id = :id")
    suspend fun getById(id: Long): ProjetEntity?

    @Query("UPDATE projets SET budgetCdf = :budgetCdf, budgetUsd = :budgetUsd, updatedAt = :now WHERE id = :id")
    suspend fun updateBudget(id: Long, budgetCdf: Double, budgetUsd: Double, now: Long = System.currentTimeMillis())
}
