package lv.lu.students.lk17235.datz4019.ui.main.orders

import androidx.lifecycle.ViewModel
import lv.lu.students.lk17235.datz4019.data.OrderRepository

class OrdersViewModel : ViewModel() {
    val orderRepository
        get() = OrderRepository()
}
