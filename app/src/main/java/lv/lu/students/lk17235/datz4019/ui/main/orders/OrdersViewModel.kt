package lv.lu.students.lk17235.datz4019.ui.main.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import lv.lu.students.lk17235.datz4019.data.AuthRepository
import lv.lu.students.lk17235.datz4019.data.OrderPagingSource
import lv.lu.students.lk17235.datz4019.data.OrderRepository
import lv.lu.students.lk17235.datz4019.data.model.Order

class OrdersViewModel : ViewModel() {
    private val authRepository
        get() = AuthRepository()

    val isUserCourier = MutableLiveData<Boolean>()

    val orderRepository
        get() = OrderRepository()

    private val _flow: Flow<PagingData<Order>>
    val flow
        get() = _flow

    init {
        _flow = Pager(
            PagingConfig(
                pageSize = 10,
                prefetchDistance = 10,
                enablePlaceholders = false
            )
        ) {
            if (isUserCourier.value == true) {
                OrderPagingSource(orderRepository, null)
            } else {
                OrderPagingSource(orderRepository, authRepository.getUserId)
            }
        }.flow.cachedIn(viewModelScope)
    }
}
