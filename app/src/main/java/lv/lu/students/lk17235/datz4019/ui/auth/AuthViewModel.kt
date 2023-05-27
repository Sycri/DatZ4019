package lv.lu.students.lk17235.datz4019.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import lv.lu.students.lk17235.datz4019.data.AuthRepository

class AuthViewModel: ViewModel() {
    private val authRepository
        get() = AuthRepository()

    private val _userResult = MutableLiveData<UserResult>()
    val userResult
        get() = _userResult

    val userDisplayName
        get() = authRepository.getUserName ?: authRepository.getUserEmail

    fun onLoginAttempt(userResult: UserResult) {
        _userResult.value = userResult
    }
}
