package lv.lu.students.lk17235.datz4019.data

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import lv.lu.students.lk17235.datz4019.data.model.Order

private data class OrderFirebaseData(
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val photoFileName: String? = null
)

class OrderRepository {
    private val orderDocs = Firebase.firestore.collection("orders")
    private val orderStorage = Firebase.storage.reference.child("orders")

    suspend fun addOrder(order: Order, photoUri: Uri?): Boolean {
        return try {
            if (order.photoFileName != null && photoUri != null) {
                orderStorage
                    .child(order.photoFileName)
                    .putFile(photoUri)
                    .await()
            }

            orderDocs
                .add(
                    OrderFirebaseData(
                        userId = order.userId,
                        name = order.name,
                        description = order.description,
                        photoFileName = order.photoFileName
                    )
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteOrder(id: String): Boolean {
        return try {
            orderDocs
                .document(id)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateOrder(order: Order, photoUri: Uri?): Boolean {
        return try {
            if (order.photoFileName != null && photoUri != null && order.photoFileName != photoUri.lastPathSegment) {
                orderStorage
                    .child(order.photoFileName)
                    .putFile(photoUri)
                    .await()
            }

            orderDocs
                .document(order.id)
                .update(
                    hashMapOf<String, Any?>(
                        "userId" to order.userId,
                        "name" to order.name,
                        "description" to order.description,
                        "photoFileName" to order.photoFileName
                    ),
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getOrder(documentId: String): Order? {
        return try {
            orderDocs
                .document(documentId)
                .get()
                .await()
                .toObject(OrderFirebaseData::class.java)
                ?.let {
                    Order(
                        id = documentId,
                        userId = it.userId,
                        name = it.name,
                        description = it.description,
                        photoFileName = it?.photoFileName
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getOrders(userId: String?, pageSize: Int, lastVisibleOrder: Order?): List<Order> {
        return try {
            val querySnapshot = if (userId != null) {
                orderDocs
                    .whereEqualTo("userId", userId)
                    .orderBy("name")
                    .startAfter(
                        lastVisibleOrder?.name
                    )
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            } else {
                orderDocs
                    .orderBy("name")
                    .startAfter(
                        lastVisibleOrder?.name
                    )
                    .limit(pageSize.toLong())
                    .get()
                    .await()

            }

            querySnapshot.documents.mapNotNull {
                val id = it.id
                val userId = it.getString("userId") ?: ""
                val name = it.getString("name") ?: ""
                val description = it.getString("description") ?: ""
                val photoFileName = it.getString("photoFileName")

                Order(id, userId, name, description, photoFileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getOrderPhotoUri(photoFileName: String): Uri? {
        return try {
            orderStorage
                .child(photoFileName)
                .downloadUrl
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
