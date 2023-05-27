package lv.lu.students.lk17235.datz4019.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import lv.lu.students.lk17235.datz4019.databinding.FragmentLoginBinding
import lv.lu.students.lk17235.datz4019.ui.auth.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = view.findNavController()

        val sharedViewModel: AuthViewModel by activityViewModels()
        val viewModel: LoginViewModel by viewModels()

        with(binding) {
            editTextEmail.addTextChangedListener {
                viewModel.editTextEmail.value = it.toString()
            }

            with(editTextPassword) {
                addTextChangedListener {
                    viewModel.editTextPassword.value = it.toString()
                }
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE -> viewModel.login()
                    }
                    false
                }
            }

            buttonLogin.setOnClickListener {
                viewModel.login()
            }

            buttonSignup.setOnClickListener {
                viewModel.onRegisterClick()
            }
        }

        with(viewModel) {
            loadingBarVisibility.observe(viewLifecycleOwner) {
                binding.loadingBar.visibility = it
            }

            navigateToRegister.observe(viewLifecycleOwner) {
                if (it) {
                    val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
                    navController.navigate(action)
                    afterNavigatingToRegister()
                }
            }

            userResult.observe(viewLifecycleOwner) {
                it?.let { userResult ->
                    sharedViewModel.onLoginAttempt(userResult)
                }
            }

            isDataValid.observe(viewLifecycleOwner) {
                binding.buttonLogin.isEnabled = it
            }
        }
    }
}
