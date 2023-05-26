package lv.lu.students.lk17235.datz4019.ui.auth

import com.google.firebase.auth.AuthResult

/**
 * Authentication result : success (AuthResult) or exception.
 */
data class UserResult(
    val success: AuthResult? = null,
    val error: Exception? = null
)
