package com.innovaction.finance.data.local.dao

import androidx.room.*
import com.innovaction.finance.data.local.entity.OperationEntity
import com.innovaction.finance.data.local.relation.OperationWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao : BaseDao<OperationEntity> {

    // ── Lecture paginée ────────────────────────────────────────────────────
    @Transaction
    @Query("""
        SELECT * FROM operations
        ORDER BY date DESC, id DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getPagedWithDetails(limit: Int, offset: Int): Flow<List<OperationWithDetails>>

    // ── Recherche multi-critères ───────────────────────────────────────────
    @Transaction
    @Query("""
        SELECT * FROM operations
        WHERE (:type       IS NULL OR type        = :type)
          AND (:compteId   IS NULL OR compteId    = :compteId)
          AND (:projetId   IS NULL OR projetId    = :projetId)
          AND (:categorieId IS NULL OR categorieId = :categorieId)
          AND (:deviseId   IS NULL OR deviseId    = :deviseId)
          AND (:federationId IS NULL OR federationId = :federationId)
          AND (:dateDebut  IS NULL OR date >= :dateDebut)
          AND (:dateFin    IS NULL OR date <= :dateFin)
          AND (:recherche  IS NULL OR libelle LIKE '%' || :recherche || '%'
               OR numeroPiece LIKE '%' || :recherche || '%')
        ORDER BY date DESC, id DESC
        LIMIT :limit OFFSET :offset
    """)
    fun searchWithDetails(
        type         : String? = null,
        compteId     : Long?   = null,
        projetId     : Long?   = null,
        categorieId  : Long?   = null,
        deviseId     : Long?   = null,
        federationId : Long?   = null,
        dateDebut    : Long?   = null,
        dateFin      : Long?   = null,
        recherche    : String? = null,
        limit        : Int     = 30,
        offset       : Int     = 0,
    ): Flow<List<OperationWithDetails>>

    // ── Par compte (pour l'historique) ────────────────────────────────────
    @Transaction
    @Query("""
        SELECT * FROM operations
        WHERE compteId = :compteId OR compteDestId = :compteId
        ORDER BY date DESC, id DESC
    """)
    fun getByCompte(compteId: Long): Flow<List<OperationWithDetails>>

    // ── Par projet ─────────────────────────────────────────────────────────
    @Transaction
    @Query("""
        SELECT * FROM operations
        WHERE projetId = :projetId
        ORDER BY date DESC, id DESC
    """)
    fun getByProjet(projetId: Long): Flow<List<OperationWithDetails>>

    // ── Détail unique ──────────────────────────────────────────────────────
    @Transaction
    @Query("SELECT * FROM operations WHERE id = :id")
    suspend fun getByIdWithDetails(id: Long): OperationWithDetails?

    // ── Compteurs / agrégats ───────────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM operations")
    fun countAll(): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(montant), 0.0) FROM operations
        WHERE type = 'ENTREE' AND deviseId = :deviseId
          AND (:mois  IS NULL OR strftime('%m', date/1000, 'unixepoch') = printf('%02d', :mois))
          AND (:annee IS NULL OR strftime('%Y', date/1000, 'unixepoch') = :annee)
    """)
    fun sumEntrees(deviseId: Long, mois: Int? = null, annee: String? = null): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(montant), 0.0) FROM operations
        WHERE type = 'SORTIE' AND deviseId = :deviseId
          AND (:mois  IS NULL OR strftime('%m', date/1000, 'unixepoch') = printf('%02d', :mois))
          AND (:annee IS NULL OR strftime('%Y', date/1000, 'unixepoch') = :annee)
    """)
    fun sumSorties(deviseId: Long, mois: Int? = null, annee: String? = null): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(montant), 0.0) FROM operations
        WHERE projetId = :projetId AND type = 'ENTREE' AND deviseId = :deviseId
    """)
    fun sumEntreesParProjet(projetId: Long, deviseId: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(montant), 0.0) FROM operations
        WHERE projetId = :projetId AND type = 'SORTIE' AND deviseId = :deviseId
    """)
    fun sumSortiesParProjet(projetId: Long, deviseId: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(montant), 0.0) FROM operations
        WHERE (compteId = :compteId OR compteDestId = :compteId)
          AND type IN ('ENTREE', 'TRANSFERT_ENTREE')
          AND deviseId = :deviseId
    """)
    fun sumEntreesParCompte(compteId: Long, deviseId: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(montant), 0.0) FROM operations
        WHERE compteId = :compteId
          AND type IN ('SORTIE', 'FRAIS', 'TRANSFERT_SORTIE')
          AND deviseId = :deviseId
    """)
    fun sumSortiesParCompte(compteId: Long, deviseId: Long): Flow<Double>

    // ── Numérotation ───────────────────────────────────────────────────────
    @Query("""
        SELECT COUNT(*) + 1 FROM operations
        WHERE strftime('%Y', date/1000, 'unixepoch') = :annee
    """)
    suspend fun getProchainNumero(annee: String): Int

    @Query("UPDATE operations SET numero = :numero WHERE id = :id")
    suspend fun updateNumero(id: Long, numero: String)

    // ── Recents pour le Dashboard ──────────────────────────────────────────
    @Transaction
    @Query("SELECT * FROM operations ORDER BY date DESC, id DESC LIMIT :n")
    fun getRecents(n: Int = 5): Flow<List<OperationWithDetails>>
}
