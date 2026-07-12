package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.TauxChangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TauxChangeDao : BaseDao<TauxChangeEntity> {
    /** Dernier taux en vigueur entre deux devises. */
    @Query("""
        SELECT * FROM taux_change
        WHERE deviseSourceId = :sourceId AND deviseCibleId = :cibleId
        ORDER BY dateEffet DESC LIMIT 1
    """)
    suspend fun getDernier(sourceId: Long, cibleId: Long): TauxChangeEntity?

    @Query("""
        SELECT * FROM taux_change
        WHERE deviseSourceId = :sourceId AND deviseCibleId = :cibleId
        ORDER BY dateEffet DESC
    """)
    fun getHistorique(sourceId: Long, cibleId: Long): Flow<List<TauxChangeEntity>>
}
