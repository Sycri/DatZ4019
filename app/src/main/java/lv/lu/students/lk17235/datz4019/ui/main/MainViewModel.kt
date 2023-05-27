package lv.lu.students.lk17235.datz4019.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.runBlocking
import lv.lu.students.lk17235.datz4019.data.AuthRepository

class MainViewModel: ViewModel() {
    private val authRepository
        get() = AuthRepository()

    private val _isUserCourier = MutableLiveData<Boolean>()
    val isUserCourier: LiveData<Boolean>
        get() = _isUserCourier

    init {
        _isUserCourier.value = runBlocking { authRepository.isUserCourier() }
    }

    fun setIsUserCourier(isCourier: Boolean) {
        runBlocking {
            if (authRepository.setUserCourier(isCourier)) {
                _isUserCourier.value = isCourier
            }
        }
    }
}
