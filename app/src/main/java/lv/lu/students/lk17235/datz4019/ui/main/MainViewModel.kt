package lv.lu.students.lk17235.datz4019.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lv.lu.students.lk17235.datz4019.data.AuthRepository

class MainViewModel: ViewModel() {
    private val authRepository
        get() = AuthRepository()

    private val _isUserCourier = MutableLiveData<Boolean>()
    val isUserCourier: LiveData<Boolean>
        get() = _isUserCourier

    init {
        viewModelScope.launch {
            _isUserCourier.value = authRepository.isUserCourier()
        }
    }

    fun setIsUserCourier(isCourier: Boolean) {
        viewModelScope.launch {
            if (authRepository.setUserCourier(isCourier)) {
                _isUserCourier.value = isCourier
            }
        }
    }

    fun onLogoutClick() {
        authRepository.logout()
    }
}
