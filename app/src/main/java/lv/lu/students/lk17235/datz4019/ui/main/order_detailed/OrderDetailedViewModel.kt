package lv.lu.students.lk17235.datz4019.ui.main.order_detailed

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.data.AuthRepository
import lv.lu.students.lk17235.datz4019.data.OrderRepository

class OrderDetailedViewModel(private val documentId: String) : ViewModel() {
    private val authRepository
        get() = AuthRepository()

    private val orderRepository
        get() = OrderRepository()

    private val _orderTitle = MutableLiveData<String>()
    val orderTitle: LiveData<String>
        get() = _orderTitle

    private val _orderDescription = MutableLiveData<String>()
    val orderDescription: LiveData<String>
        get() = _orderDescription

    private val _orderPhotoUri = MutableLiveData<Uri>()
    val orderPhotoUri: LiveData<Uri>
        get() = _orderPhotoUri

    init {
        viewModelScope.launch {
            val order = orderRepository.getOrder(documentId)

            order?.let {
                _orderTitle.value = it.name
                _orderDescription.value = it.description

                it.photoFileName?.let { photoFileName ->
                    _orderPhotoUri.value = orderRepository.getOrderPhotoUri(photoFileName)
                }
            }
        }
    }
}
