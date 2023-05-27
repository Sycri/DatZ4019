package lv.lu.students.lk17235.datz4019.ui.main.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import lv.lu.students.lk17235.datz4019.data.OrderRepository
import lv.lu.students.lk17235.datz4019.data.OrderPagingSource

class OrdersViewModel : ViewModel() {
    val orderRepository
        get() = OrderRepository()

    val flow = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 10,
            enablePlaceholders = false
        )
    ) {
        OrderPagingSource(orderRepository, null)
    }.flow.cachedIn(viewModelScope)
}
