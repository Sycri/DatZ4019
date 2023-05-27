package lv.lu.students.lk17235.datz4019.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import lv.lu.students.lk17235.datz4019.data.model.Order

class OrderPagingSource(
    private val repository: OrderRepository,
    private val userId: String?
) : PagingSource<List<Order>, Order>() {
    override suspend fun load(params: LoadParams<List<Order>>): LoadResult<List<Order>, Order> {
        return try {
            val currentPage = params.key ?: emptyList()
            val nextPage = repository.getOrders(userId, params.loadSize, currentPage.lastOrNull())

            LoadResult.Page(
                data = nextPage,
                prevKey = null,
                nextKey = nextPage.takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<List<Order>, Order>): List<Order>? {
        return null
    }
}
