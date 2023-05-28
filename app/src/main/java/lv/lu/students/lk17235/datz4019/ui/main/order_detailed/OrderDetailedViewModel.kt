package lv.lu.students.lk17235.datz4019.ui.main.order_detailed

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.data.AuthRepository
import lv.lu.students.lk17235.datz4019.data.OrderRepository
import lv.lu.students.lk17235.datz4019.data.model.Order
import java.util.UUID

class OrderDetailedViewModel(private val documentId: String?) : ViewModel() {
    private val authRepository
        get() = AuthRepository()

    private val orderRepository
        get() = OrderRepository()

    private val _orderId = MutableLiveData<String?>()
    val orderId: LiveData<String?>
        get() = _orderId

    private val _orderAddress = MutableLiveData<String>()
    val orderAddress: LiveData<String>
        get() = _orderAddress

    private val _orderComment = MutableLiveData<String>()
    val orderComment: LiveData<String>
        get() = _orderComment

    private val _orderPhotoFileName = MutableLiveData<String?>()
    val orderPhotoFileName: LiveData<String?>
        get() = _orderPhotoFileName

    private val _orderPhotoUri = MutableLiveData<Uri?>()
    val orderPhotoUri: LiveData<Uri?>
        get() = _orderPhotoUri

    private val _orderUserCreated = MutableLiveData<Boolean>()
    val orderUserCreated: LiveData<Boolean>
        get() = _orderUserCreated

    private val _loadingBarVisibility = MutableLiveData<Int>()
    val loadingBarVisibility: LiveData<Int>
        get() = _loadingBarVisibility

    private val _isDataValid = MediatorLiveData<Boolean>()
    val isDataValid: LiveData<Boolean>
        get() = _isDataValid

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    private val _choosingPhoto = MutableLiveData<Boolean>()
    val choosingPhoto: LiveData<Boolean>
        get() = _choosingPhoto

    val isAddressBlank: Boolean
        get() = orderAddress.value.isNullOrBlank()

    private val checkDataValidity: Boolean
        get() = !isAddressBlank && (orderPhotoUri.value != null)

    init {
        _loadingBarVisibility.value = View.GONE
        _navigateBack.value = false
        _choosingPhoto.value = false

        with(_isDataValid) {
            addSource(orderAddress) { _isDataValid.value = checkDataValidity }
            addSource(orderPhotoUri) { _isDataValid.value = checkDataValidity }
        }

        if (documentId == null) {
            _orderUserCreated.value = true
        } else {
            viewModelScope.launch {
                val order = orderRepository.getOrder(documentId)

                order?.let {
                    _orderId.value = it.id
                    _orderAddress.value = it.address
                    _orderComment.value = it.comment
                    _orderPhotoFileName.value = it.photoFileName

                    it.photoFileName?.let { photoFileName ->
                        _orderPhotoUri.value = orderRepository.getOrderPhotoUri(photoFileName)
                    }

                    _orderUserCreated.value = it.userId == authRepository.getUserId
                }
            }
        }
    }

    fun setOrderAddress(address: String) {
        _orderAddress.value = address
    }

    fun setOrderComment(comment: String) {
        _orderComment.value = comment
    }

    fun onOrderSaveClick() {
        if (authRepository.getUserId == null) {
            return
        }

        val order = Order(
            id = orderId.value,
            userId = authRepository.getUserId!!,
            address = orderAddress.value.orEmpty(),
            comment = orderComment.value.orEmpty(),
            pickupTime = null,
            photoFileName = orderPhotoFileName.value,
            createdAt = null,
            updatedAt = null
        )

        viewModelScope.launch {
            _loadingBarVisibility.value = View.VISIBLE

            if (order.id == null) {
                orderRepository.addOrder(order, orderPhotoUri.value)
            } else {
                orderRepository.updateOrder(order, orderPhotoUri.value)
            }

            _loadingBarVisibility.value = View.GONE
            _navigateBack.value = true
        }
    }

    fun afterNavigateBack() {
        _navigateBack.value = false
    }

    fun onPhotoClick() {
        _choosingPhoto.value = true
    }

    fun afterPhotoClick(uri: Uri?) {
        if (uri != null) {
            _orderPhotoUri.value = uri
            _orderPhotoFileName.value = UUID.randomUUID().toString().replace("-", "")
        }

        _choosingPhoto.value = false
    }
}
