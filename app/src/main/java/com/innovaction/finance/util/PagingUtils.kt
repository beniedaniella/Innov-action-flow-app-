package com.innovaction.finance.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.innovaction.finance.data.local.dao.OperationDao
import com.innovaction.finance.data.local.relation.OperationWithDetails
import com.innovaction.finance.presentation.operations.FiltresJournal
import kotlinx.coroutines.flow.first

/**
 * PagingSource pour le Journal de caisse.
 * Charge les opérations par pages de [AppConstants.PAGE_SIZE] éléments.
 * Compatible avec LazyPagingItems dans Compose.
 */
class OperationPagingSource(
    private val dao     : OperationDao,
    private val filtres : FiltresJournal,
) : PagingSource<Int, OperationWithDetails>() {

    override fun getRefreshKey(state: PagingState<Int, OperationWithDetails>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OperationWithDetails> {
        val page   = params.key ?: 0
        val limit  = params.loadSize
        val offset = page * limit

        return try {
            val items = dao.searchWithDetails(
                type         = filtres.type,
                compteId     = filtres.compteId,
                projetId     = filtres.projetId,
                deviseId     = filtres.deviseId,
                federationId = filtres.federationId,
                dateDebut    = filtres.dateDebut,
                dateFin      = filtres.dateFin,
                recherche    = filtres.recherche.ifBlank { null },
                limit        = limit,
                offset       = offset,
            ).first()

            LoadResult.Page(
                data     = items,
                prevKey  = if (page == 0) null else page - 1,
                nextKey  = if (items.size < limit) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
