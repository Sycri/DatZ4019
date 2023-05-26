package lv.lu.students.lk17235.datz4019.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import lv.lu.students.lk17235.datz4019.ui.auth.UserResult

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun loginWithEmail(email: String, password: String): UserResult {
        return try {
            val data = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            UserResult(success = data)
        } catch (e: Exception) {
            UserResult(error = e)
        }
    }

    suspend fun registerWithEmail(email: String, password: String, username: String): UserResult {
        return try {
            val data = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            data.user?.updateProfile(profileUpdates)?.await()

            UserResult(success = data)
        } catch (e: Exception) {
            UserResult(error = e)
        }
    }

    val currentUser = firebaseAuth.currentUser
    fun logout() = firebaseAuth.signOut()
}