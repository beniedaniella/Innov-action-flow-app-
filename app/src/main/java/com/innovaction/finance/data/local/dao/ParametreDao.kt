package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.ParametreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParametreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(parametre: ParametreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(parametres: List<ParametreEntity>)

    @Query("SELECT * FROM parametres ORDER BY categorie ASC, cle ASC")
    fun getAll(): Flow<List<ParametreEntity>>

    @Query("SELECT * FROM parametres WHERE categorie = :categorie ORDER BY cle ASC")
    fun getByCategorie(categorie: String): Flow<List<ParametreEntity>>

    @Query("SELECT valeur FROM parametres WHERE cle = :cle")
    suspend fun getValeur(cle: String): String?

    @Query("SELECT valeur FROM parametres WHERE cle = :cle")
    fun getValeurFlow(cle: String): Flow<String?>

    @Query("DELETE FROM parametres WHERE cle = :cle")
    suspend fun delete(cle: String)
}
