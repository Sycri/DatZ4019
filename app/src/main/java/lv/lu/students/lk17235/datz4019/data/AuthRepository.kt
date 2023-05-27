package lv.lu.students.lk17235.datz4019.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import lv.lu.students.lk17235.datz4019.ui.auth.UserResult

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userDocs = Firebase.firestore.collection("users")

    suspend fun loginWithEmail(email: String, password: String): UserResult {
        return try {
            val data = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            UserResult(success = data)
        } catch (e: Exception) {
            UserResult(error = e)
        }
    }

    suspend fun registerWithEmail(email: String, password: String, username: String): UserResult {
        return try {
            val data = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            data
                .user!!
                .updateProfile(
                    UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(username)
                        .build()
                )
                .await()

            userDocs
                .document(data.user!!.uid)
                .set(
                    hashMapOf(
                        "isCourier" to false
                    ),
                    SetOptions.mergeFields("isCourier")
                )
                .await()

            UserResult(success = data)
        } catch (e: Exception) {
            UserResult(error = e)
        }
    }

    fun logout() = firebaseAuth.signOut()

    val getUserId
        get() = firebaseAuth.currentUser?.uid

    val getUserEmail
        get() = firebaseAuth.currentUser?.email

    val getUserName
        get() = firebaseAuth.currentUser?.displayName

    suspend fun isUserCourier(): Boolean {
        return try {
            if (firebaseAuth.currentUser == null) {
                false
            } else {
                userDocs
                    .document(firebaseAuth.currentUser!!.uid)
                    .get()
                    .await()
                    .get("isCourier") as Boolean? ?: false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun setUserCourier(isCourier: Boolean): Boolean {
        return try {
            userDocs
                .document(firebaseAuth.currentUser!!.uid)
                .set(
                    hashMapOf(
                        "isCourier" to isCourier
                    ),
                    SetOptions.mergeFields("isCourier")
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
