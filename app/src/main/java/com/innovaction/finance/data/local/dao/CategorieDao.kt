package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.CategorieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategorieDao : BaseDao<CategorieEntity> {
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllActive(): Flow<List<CategorieEntity>>

    @Query("SELECT * FROM categories WHERE isActive = 1 AND (typeDefaut = :type OR typeDefaut = 'TOUS') ORDER BY ordre ASC")
    fun getByType(type: String): Flow<List<CategorieEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategorieEntity?

    @Query("UPDATE categories SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}
