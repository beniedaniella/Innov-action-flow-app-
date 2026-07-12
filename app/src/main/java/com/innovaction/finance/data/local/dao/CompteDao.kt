package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.CompteEntity
import com.innovaction.finance.data.local.relation.CompteWithDevise
import kotlinx.coroutines.flow.Flow

@Dao
interface CompteDao : BaseDao<CompteEntity> {
    @Transaction
    @Query("SELECT * FROM comptes WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllWithDevise(): Flow<List<CompteWithDevise>>

    @Query("SELECT * FROM comptes WHERE isActive = 1 ORDER BY ordre ASC")
    fun getAllActive(): Flow<List<CompteEntity>>

    @Query("SELECT * FROM comptes WHERE id = :id")
    suspend fun getById(id: Long): CompteEntity?

    /**
     * Calcule le solde réel d'un compte :
     * soldeInitial + somme des entrées - somme des sorties
     */
    @Query("""
        SELECT c.soldeInitial +
            COALESCE((SELECT SUM(o.montant) FROM operations o
                WHERE o.compteId = c.id AND o.type IN ('ENTREE','TRANSFERT_ENTREE')), 0.0) -
            COALESCE((SELECT SUM(o.montant) FROM operations o
                WHERE o.compteId = c.id AND o.type IN ('SORTIE','FRAIS','TRANSFERT_SORTIE')), 0.0)
        FROM comptes c WHERE c.id = :compteId
    """)
    suspend fun getSoldeCalcule(compteId: Long): Double

    @Query("""
        SELECT c.soldeInitial +
            COALESCE((SELECT SUM(o.montant) FROM operations o
                WHERE o.compteId = c.id AND o.type IN ('ENTREE','TRANSFERT_ENTREE')), 0.0) -
            COALESCE((SELECT SUM(o.montant) FROM operations o
                WHERE o.compteId = c.id AND o.type IN ('SORTIE','FRAIS','TRANSFERT_SORTIE')), 0.0)
        FROM comptes c WHERE c.id = :compteId
    """)
    fun getSoldeCalculeFlow(compteId: Long): Flow<Double>
}
