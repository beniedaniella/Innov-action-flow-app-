package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.DeviseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviseDao : BaseDao<DeviseEntity> {
    @Query("SELECT * FROM devises WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllActive(): Flow<List<DeviseEntity>>

    @Query("SELECT * FROM devises ORDER BY ordre ASC")
    fun getAll(): Flow<List<DeviseEntity>>

    @Query("SELECT * FROM devises WHERE id = :id")
    suspend fun getById(id: Long): DeviseEntity?

    @Query("SELECT * FROM devises WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): DeviseEntity?

    @Query("UPDATE devises SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}
