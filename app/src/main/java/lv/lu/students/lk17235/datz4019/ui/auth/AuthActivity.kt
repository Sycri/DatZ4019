package lv.lu.students.lk17235.datz4019.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import lv.lu.students.lk17235.datz4019.R
import lv.lu.students.lk17235.datz4019.databinding.ActivityAuthBinding
import lv.lu.students.lk17235.datz4019.ui.main.MainActivity

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_auth)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_login, R.id.navigation_signup
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        viewModel.userResult.observe(this) {
            it.error?.let { exception ->
                showLoginFailed(exception)
            }
            it.success?.let { authResult ->
                showLoginSuccess(authResult.user?.displayName ?: authResult.user?.email!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.currentUser?.let { user ->
            showLoginSuccess(user.displayName ?: user.email!!)
        }
    }

    private fun showLoginSuccess(username: String) {
        Toast.makeText(this, getString(R.string.welcome, username), Toast.LENGTH_LONG).show()

        startActivity(Intent(this, MainActivity::class.java))
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showLoginFailed(e: Exception) {
        val errorMessage = getString(
            when (e) {
                is FirebaseAuthInvalidCredentialsException,
                is FirebaseAuthInvalidUserException -> R.string.invalid_account
                is FirebaseAuthUserCollisionException -> R.string.invalid_email_used
                else -> R.string.auth_failed
            }
        )

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}