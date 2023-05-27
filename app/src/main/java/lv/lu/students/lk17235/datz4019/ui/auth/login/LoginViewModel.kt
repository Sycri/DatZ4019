package lv.lu.students.lk17235.datz4019.ui.auth.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.data.AuthRepository
import lv.lu.students.lk17235.datz4019.ui.auth.UserResult

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    val editTextEmail = MutableLiveData<String>()
    val editTextPassword = MutableLiveData<String>()

    private val _loadingBarVisibility = MutableLiveData<Int>()
    val loadingBarVisibility: LiveData<Int>
        get() = _loadingBarVisibility

    private val _navigateToRegister = MutableLiveData<Boolean>()
    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister

    private val _userResult = MutableLiveData<UserResult?>()
    val userResult: MutableLiveData<UserResult?>
        get() = _userResult

    private val _isDataValid = MediatorLiveData<Boolean>()
    val isDataValid: LiveData<Boolean>
        get() = _isDataValid

    private val checkDataValidity: Boolean
        get() = !editTextEmail.value.isNullOrBlank() && !editTextPassword.value.isNullOrBlank()

    init {
        _loadingBarVisibility.value = View.GONE
        _navigateToRegister.value = false

        with(_isDataValid) {
            addSource(editTextEmail) { _isDataValid.value = checkDataValidity }
            addSource(editTextPassword) { _isDataValid.value = checkDataValidity }
        }
    }

    fun login() {
        if (!checkDataValidity) {
            return
        }

        viewModelScope.launch {
            _loadingBarVisibility.value = View.VISIBLE

            _userResult.value = authRepository.loginWithEmail(
                editTextEmail.value.orEmpty(),
                editTextPassword.value.orEmpty()
            )
            _userResult.value = null

            _loadingBarVisibility.value = View.GONE
        }
    }

    fun onRegisterClick() {
        _navigateToRegister.value = true
    }

    fun afterNavigatingToRegister() {
        _navigateToRegister.value = false
    }
}
