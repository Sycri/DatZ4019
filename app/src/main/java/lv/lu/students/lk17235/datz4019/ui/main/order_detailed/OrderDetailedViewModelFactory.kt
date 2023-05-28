package lv.lu.students.lk17235.datz4019.ui.main.order_detailed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OrderDetailedViewModelFactory(private val documentId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderDetailedViewModel(documentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
