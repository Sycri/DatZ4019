package lv.lu.students.lk17235.datz4019.ui.auth.signup

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.data.AuthRepository
import lv.lu.students.lk17235.datz4019.ui.auth.UserResult

class SignupViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    val editTextUsername = MutableLiveData<String>()
    val editTextEmail = MutableLiveData<String>()
    val editTextPassword = MutableLiveData<String>()
    val editTextConfirmPassword = MutableLiveData<String>()

    private val _loadingBarVisibility = MutableLiveData<Int>()
    val loadingBarVisibility: LiveData<Int>
        get() = _loadingBarVisibility

    private val _userResult = MutableLiveData<UserResult>()
    val userResult: LiveData<UserResult>
        get() = _userResult

    private val _isDataValid = MediatorLiveData<Boolean>()
    val isDataValid: LiveData<Boolean>
        get() = _isDataValid

    val isUsernameValid: Boolean
        get() = editTextUsername.value.orEmpty().length > 3

    val isEmailValid: Boolean
        get() = editTextEmail.value.orEmpty().contains('@')

    val isPasswordValid: Boolean
        get() = editTextPassword.value.orEmpty().length > 5

    val arePasswordsMatching: Boolean
        get() = editTextPassword.value.orEmpty() == editTextConfirmPassword.value.orEmpty()

    private val checkDataValidity: Boolean
        get() = isUsernameValid && isEmailValid && isPasswordValid && arePasswordsMatching

    init {
        _loadingBarVisibility.value = View.GONE

        with(_isDataValid) {
            addSource(editTextUsername) { _isDataValid.value = checkDataValidity }
            addSource(editTextEmail) { _isDataValid.value = checkDataValidity }
            addSource(editTextPassword) { _isDataValid.value = checkDataValidity }
            addSource(editTextConfirmPassword) { _isDataValid.value = checkDataValidity }
        }
    }

    fun register() {
        if (!checkDataValidity) {
            return
        }

        viewModelScope.launch {
            _loadingBarVisibility.value = View.VISIBLE

            _userResult.value = authRepository.registerWithEmail(
                editTextEmail.value.orEmpty(),
                editTextPassword.value.orEmpty(),
                editTextUsername.value.orEmpty()
            )

            _loadingBarVisibility.value = View.GONE
        }
    }
}
