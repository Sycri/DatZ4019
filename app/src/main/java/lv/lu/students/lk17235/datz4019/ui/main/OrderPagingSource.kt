package lv.lu.students.lk17235.datz4019.ui.main

import androidx.paging.PagingSource
import androidx.paging.PagingState
import lv.lu.students.lk17235.datz4019.data.OrderRepository
import lv.lu.students.lk17235.datz4019.data.model.OrderModel

class OrderPagingSource(private val repository: OrderRepository) : PagingSource<String, OrderModel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, OrderModel> {
        return try {
            val orders = repository.getOrders(null, params.loadSize, params.key)
            LoadResult.Page(
                data = orders,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, OrderModel>): String? {
        return state.lastItemOrNull()?.id;
    }

}