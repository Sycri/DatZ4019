package lv.lu.students.lk17235.datz4019.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import lv.lu.students.lk17235.datz4019.R
import lv.lu.students.lk17235.datz4019.databinding.FragmentSignupBinding
import lv.lu.students.lk17235.datz4019.ui.auth.AuthViewModel

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: SignupViewModel by viewModels()
        val sharedViewModel: AuthViewModel by activityViewModels()

        with(binding) {
            editTextEmail.addTextChangedListener {
                viewModel.editTextEmail.value = it.toString()
            }

            editTextUsername.addTextChangedListener {
                viewModel.editTextUsername.value = it.toString()
            }

            editTextPassword.addTextChangedListener {
                viewModel.editTextPassword.value = it.toString()
            }

            with(editTextConfirmPassword) {
                addTextChangedListener {
                    viewModel.editTextConfirmPassword.value = it.toString()
                }
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE -> viewModel.register()
                    }
                    false
                }
            }

            buttonRegister.setOnClickListener {
                viewModel.register()
            }
        }

        with(viewModel) {
            editTextEmail.observe(viewLifecycleOwner) {
                binding.textInputLayoutEmail.error = when {
                    (!isEmailValid && !editTextEmail.value.isNullOrBlank()) -> getString(R.string.invalid_email)
                    else -> null
                }
            }

            editTextUsername.observe(viewLifecycleOwner) {
                binding.textInputLayoutUsername.error = when {
                    (!isUsernameValid && !editTextUsername.value.isNullOrBlank()) -> getString(R.string.invalid_username)
                    else -> null
                }
            }

            editTextPassword.observe(viewLifecycleOwner) {
                binding.textInputLayoutPassword.error = when {
                    (!isPasswordValid && !editTextPassword.value.isNullOrBlank()) -> getString(R.string.invalid_password)
                    else -> null
                }

                binding.textInputLayoutConfirmPassword.error = when {
                    (!arePasswordsMatching && !editTextConfirmPassword.value.isNullOrBlank()) -> getString(
                        R.string.invalid_confirm_password
                    )

                    else -> null
                }
            }

            editTextConfirmPassword.observe(viewLifecycleOwner) {
                binding.textInputLayoutConfirmPassword.error = when {
                    (!arePasswordsMatching && !editTextConfirmPassword.value.isNullOrBlank()) -> getString(
                        R.string.invalid_confirm_password
                    )

                    else -> null
                }
            }

            loadingBarVisibility.observe(viewLifecycleOwner) {
                binding.loadingBar.visibility = it
            }

            userResult.observe(viewLifecycleOwner) {
                it?.let {
                    if (it.error is FirebaseAuthUserCollisionException) {
                        binding.textInputLayoutEmail.error = getString(R.string.invalid_email_used)
                    }

                    sharedViewModel.onLoginAttempt(it)
                }
            }

            isDataValid.observe(viewLifecycleOwner) {
                binding.buttonRegister.isEnabled = it
            }
        }
    }
}
