package com.innovaction.finance.data.local.dao

import androidx.room.*

/** Interface générique partagée par tous les DAOs. */
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(entities: List<T>): List<Long>

    @Update
    suspend fun update(entity: T)

    @Delete
    suspend fun delete(entity: T)
}
